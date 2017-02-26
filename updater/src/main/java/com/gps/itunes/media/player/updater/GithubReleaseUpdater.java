package com.gps.itunes.media.player.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gps.imp.utils.ui.AsyncTaskListener;
import com.gps.imp.utils.ui.InterruptableAsyncTask;
import com.gps.itunes.media.player.updater.github.Asset;
import com.gps.itunes.media.player.updater.github.Release;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by leogps on 2/25/17.
 */
public class GithubReleaseUpdater {

    private static final Logger LOG = Logger.getLogger(GithubReleaseUpdater.class);

    public InterruptableAsyncTask<Void, UpdateResult> update(String filePath, String repositoryUrl, String assetName,
                                                       String md5SumsAssetName) {

        if(filePath == null || repositoryUrl == null || assetName == null) {
            return null;
        }
        return updateProcess(filePath, repositoryUrl, assetName, md5SumsAssetName);
    }

    public static String getContent(String url) throws IOException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);

        int response = client.executeMethod(method);
        if(response == HttpStatus.SC_OK) {
            return method.getResponseBodyAsString();
        }
        return null;
    }

    private InterruptableAsyncTask<Void, UpdateResult> updateProcess(final String filePath, final String repositoryUrl, final String assetName, final String md5SumsAssetName) {

        return new InterruptableAsyncTask<Void, UpdateResult>() {

            private final ExecutorService executorService = Executors.newSingleThreadExecutor();
            private final List<AsyncTaskListener> asyncTaskListeners = new ArrayList();

            private UpdateResult updateResult;

            public Void execute() throws Exception {
                try {
                    Callable<UpdateResult> callable = new Callable<UpdateResult>() {
                        public UpdateResult call() throws Exception {

                            UpdateResult updateResult = new UpdateResult();

                            ObjectMapper objectMapper = new ObjectMapper();
                            String content = getContent(repositoryUrl);

                            if(content == null) {
                                LOG.error("Release metadata could not be loaded!! " + repositoryUrl);
                                updateResult.setUpdated(false);
                                updateResult.setReason(UpdateResult.Reason.METADATA_COULD_NOT_BE_LOADED);
                                return updateResult;
                            }

                            Release release = objectMapper.readValue(content, Release.class);

                            if(!isUpdateAvailable(filePath, release, assetName, md5SumsAssetName)) {
                                LOG.debug("No Update available.");
                                updateResult.setUpdated(false);
                                updateResult.setReason(UpdateResult.Reason.UPDATE_NOT_AVAILABLE);
                                return updateResult;
                            }
                            LOG.debug("Update available...");
                            String assetUrl = resolveAssetURL(assetName, release);

                            boolean replaced = replace(filePath, assetUrl);
                            updateResult.setUpdated(replaced);
                            if(replaced) {
                                updateResult.setReason(UpdateResult.Reason.UPDATE_SUCCESS);
                            } else {
                                updateResult.setReason(UpdateResult.Reason.UPDATE_FAILED_UNKNOWN);
                            }
                            return updateResult;
                        }
                    };
                    Future<UpdateResult> future = executorService.submit(callable);
                    awaitResult(future);
                    return null;

                } catch (Exception ex) {
                    informFailure();
                    executorService.awaitTermination(1, TimeUnit.MICROSECONDS);
                    executorService.shutdownNow();
                    return null;
                }
            }

            private void informSuccess() {
                for(AsyncTaskListener asyncTaskListener : asyncTaskListeners) {
                    asyncTaskListener.onSuccess(this);
                }
            }

            private void informFailure() {
                for(AsyncTaskListener asyncTaskListener : asyncTaskListeners) {
                    asyncTaskListener.onFailure(this);
                }
            }

            private void awaitResult(final Future<UpdateResult> future) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            updateResult = future.get();

                            if(updateResult.isUpdated()) {
                                informSuccess();
                            } else if(updateResult.getReason() == UpdateResult.Reason.UPDATE_NOT_AVAILABLE) {
                                informSuccess();
                            } else {
                                informFailure();
                            }
                        } catch (Exception e) {

                            updateResult = new UpdateResult();
                            updateResult.setUpdated(false);
                            updateResult.setReason(UpdateResult.Reason.EXCEPTION_OCCURRED);
                            informFailure();

                            LOG.error("Execption occurred awaiting component update", e);
                        } finally {
                            try {
                                executorService.awaitTermination(1, TimeUnit.MICROSECONDS);
                                executorService.shutdownNow();
                                LOG.error("Shutdown complete for Release updater ExecutorService.");
                            } catch (InterruptedException e) {
                                LOG.error("Failed to shutdown the ExecutorService for Release updater.");
                            }
                        }
                    }
                }).start();
            }

            public void registerListener(AsyncTaskListener asyncTaskListener) {
                asyncTaskListeners.add(asyncTaskListener);
            }

            public void interrupt() {
                try {
                    executorService.awaitTermination(1, TimeUnit.MICROSECONDS);
                } catch (InterruptedException e) {
                    LOG.error(e);
                } finally {
                    executorService.shutdownNow();
                }
            }

            public boolean isInterrupted() {
                return executorService.isTerminated();
            }

            public UpdateResult getResult() {
                return updateResult;
            }
        };
    }

    public Boolean replace(String filePath, String assetUrl) throws IOException {
        if(filePath == null || StringUtils.isBlank(assetUrl)) {
            return false;
        }

        File updatabelFile = new File(filePath);
        FileUtils.copyURLToFile(new URL(assetUrl), updatabelFile);
        updatabelFile.setExecutable(true);

        return true;
    }

    public String resolveAssetURL(String assetName, Release release) {
        if(release == null || release.getAssets() == null || release.getAssets().length < 1 || StringUtils.isBlank(assetName)) {
            return null;
        }

        for(Asset asset : release.getAssets()) {
            if(StringUtils.equals(asset.getName(), assetName)) {
                return asset.getDownloadUrl();
            }
        }

        return null;
    }

    public boolean isUpdateAvailable(String filePath, Release release, String assetName,
                                     String md5sAssetName) throws IOException, NoSuchAlgorithmException {

        File updatable = new File(filePath);
        if(!updatable.exists()) {
            LOG.debug("Updatable file does not exist");
            return true;
        }

        String calculatedMD5Checksum = getMD5Checksum(updatable);
        LOG.debug("CalculatedMD5Checksum: " + calculatedMD5Checksum);
        if(StringUtils.isBlank(calculatedMD5Checksum)) {
            return true;
        }

        File md5Sums = getAssetByByName(md5sAssetName, release);
        if(md5Sums == null) {
            LOG.debug("md5Asset could not be downloaded: " + md5sAssetName);
            return true;
        }

        return !readMD5ChecksumsAndCompare(calculatedMD5Checksum, md5Sums, assetName);
    }

    private boolean readMD5ChecksumsAndCompare(String calculatedMD5Checksum, File md5Sums, String assetName) throws IOException {
        InputStream fis = null;
        try {
            fis = new FileInputStream(md5Sums);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null) {
                String[] md5SumSplit = StringUtils.split(line, " ");
                if(md5SumSplit != null && md5SumSplit.length >= 2) {
                    String md5 = md5SumSplit[0];
                    String asset = md5SumSplit[1];
                    if(StringUtils.equals(asset, assetName)) {

                        LOG.debug("Asset matched. MD5 Checksum read: " + md5);
                        boolean md5Matched = StringUtils.equals(md5, calculatedMD5Checksum);
                        LOG.debug("MD5 Checksums matched? " + md5Matched);

                        return md5Matched;
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Exception occurred when reading md5Sums file: ", e);
        } finally {
            if(fis != null) {
                fis.close();
            }
        }

        LOG.debug("MD5 Checksum comparision failed.");
        return false;
    }

    public static String getMD5Checksum(File file) throws NoSuchAlgorithmException, IOException {
        //Use MD5 algorithm
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        return getFileChecksum(md5Digest, file);
    }

    private File getAssetByByName(String md5sAssetName, Release release) throws IOException {
        if(release == null || release.getAssets() == null || release.getAssets().length < 1
                || StringUtils.isBlank(md5sAssetName)) {
            LOG.debug("Sanity check failed.");
            return null;
        }

        for(Asset asset : release.getAssets()) {
            if(asset == null) {
                continue;
            }

            if(StringUtils.equals(asset.getName(), md5sAssetName)) {
                LOG.debug("Asset matched: " + asset.getDownloadUrl());
                if(StringUtils.isBlank(asset.getDownloadUrl())) {
                    return null;
                }

                String prefix = "md5sum-";
                String suffix = ".md5";
                LOG.debug(String.format("Creating tmp file, %s.%s", prefix, suffix));
                File tmpFile = File.createTempFile(prefix, suffix);

                LOG.debug("Writing md5Sums file to: " + tmpFile);
                FileUtils.copyURLToFile(new URL(asset.getDownloadUrl()), tmpFile);
                LOG.debug("Writing md5Sums completed successfully: " + tmpFile);

                return tmpFile;
            }
        }
        return null;
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i< bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}