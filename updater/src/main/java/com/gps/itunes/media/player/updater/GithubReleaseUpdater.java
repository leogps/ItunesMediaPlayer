package com.gps.itunes.media.player.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gps.imp.utils.ssl.HttpClientUtils;
import com.gps.imp.utils.ui.AsyncTaskListener;
import com.gps.imp.utils.ui.InterruptableAsyncTask;
import com.gps.itunes.media.player.updater.checksum.ChecksumHandler;
import com.gps.itunes.media.player.updater.github.Asset;
import com.gps.itunes.media.player.updater.github.Release;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by leogps on 2/25/17.
 */
public class GithubReleaseUpdater {

    private static final Logger LOG = LogManager.getLogger(GithubReleaseUpdater.class);

    private final ServiceLoader<ChecksumHandler> checksumHandlers = ServiceLoader.load(ChecksumHandler.class);

    public List<ChecksumHandler> getChecksumHandlers() {
        List<ChecksumHandler> handlers = new ArrayList<>();
        for (ChecksumHandler handler: checksumHandlers) {
            handlers.add(handler);
        }
        return handlers;
    }

    public InterruptableAsyncTask<Void, UpdateResult> update(String filePath, String repositoryUrl, String assetName,
                                                             String supportedChecksums) {

        if(filePath == null || repositoryUrl == null || assetName == null) {
            return null;
        }
        Set<String> supportedChecksumSet = Arrays
                .stream(
                        StringUtils.split(supportedChecksums, ",")
                ).collect(Collectors.toSet());
        return updateProcess(filePath, repositoryUrl, assetName, supportedChecksumSet);
    }

    public static String getContent(String url) throws Exception {
        Client client = null;
        try {
            client = HttpClientUtils.getNewClient();
            Response response = client.target(url)
                    .request()
                    .get();
            if (response == null || response.getStatus() != Response.Status.OK.getStatusCode()) {
                String message = String.format("StatusCode: %s; Body: [%s]", response.getStatus(), response.readEntity(String.class));
                throw new Exception("Failed to retrieve update: " + message);
            }
            return response.readEntity(String.class);
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }

    private InterruptableAsyncTask<Void, UpdateResult> updateProcess(final String filePath,
                                                                     final String repositoryUrl,
                                                                     final String assetName,
                                                                     final Set<String> supportedChecksumSet) {
        return new InterruptableAsyncTask<Void, UpdateResult>() {

            private final ExecutorService executorService = Executors.newSingleThreadExecutor();
            private final List<AsyncTaskListener> asyncTaskListeners = new ArrayList();

            private UpdateResult updateResult;

            public Void execute() throws Exception {
                try {
                    Callable<UpdateResult> callable = () -> {

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

                        if(!isUpdateAvailable(filePath, release, assetName, supportedChecksumSet)) {
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
                                     Set<String> supportedChecksumSet) throws IOException, NoSuchAlgorithmException {
        if (supportedChecksumSet.isEmpty()) {
            LOG.warn("No supported checksum found.");
            return true;
        }

        File updatable = new File(filePath);
        if(!updatable.exists()) {
            LOG.warn("Updatable file does not exist");
            return true;
        }

        if (release.getAssets() == null) {
            LOG.warn("No assets found. Something's fishy");
            return false;
        }
        for (Asset releaseAsset: release.getAssets()) {
            String releaseAssetName = releaseAsset.getName();
            if (!supportedChecksumSet.contains(releaseAssetName)) {
                continue;
            }
            LOG.info("Found supported checksum: {}", releaseAssetName);

            for (ChecksumHandler handler: checksumHandlers) {
                if (!handler.canHandle(releaseAssetName)) {
                    LOG.debug("Not a checksum or not supported by this handler: {}, {}. Skipping...",
                            releaseAssetName, handler.getName());
                    continue;
                }

                LOG.info("Handling checksum: {}", handler.getName());
                String calculatedChecksum = handler.calculateChecksum(updatable);
                LOG.debug("CalculatedChecksum: " + calculatedChecksum);
                if(StringUtils.isBlank(calculatedChecksum)) {
                    LOG.warn("CalculatedChecksum is empty");
                    return true;
                }
                File checksumsFile = retrieveChecksumAsset(releaseAsset);
                if(checksumsFile == null) {
                    LOG.warn("checksum asset could not be downloaded: " + releaseAssetName);
                    continue;
                }
                return !readChecksumsFileAndCompare(calculatedChecksum, checksumsFile, assetName);
            }
        }
        return true;
    }

    private boolean readChecksumsFileAndCompare(String calculatedChecksum, File checksumsFile, String assetName) throws IOException {
        try (InputStream fis = new FileInputStream(checksumsFile)) {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                String[] checksumSplit = StringUtils.split(line, " ");
                if (checksumSplit != null && checksumSplit.length >= 2) {
                    String checksum = checksumSplit[0];
                    String asset = checksumSplit[1];
                    if (StringUtils.equals(asset, assetName)) {
                        LOG.debug("Asset matched. Checksum read: " + checksum);
                        boolean isChecksumMatched = StringUtils.equals(checksum, calculatedChecksum);
                        LOG.debug("Checksums matched? " + isChecksumMatched);
                        return isChecksumMatched;
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Exception occurred when reading checksums file: ", e);
            throw e;
        }

        LOG.debug("Checksum comparison failed.");
        return false;
    }

    private File retrieveChecksumAsset(Asset asset) throws IOException {
        LOG.debug("Matched Asset Url: " + asset.getDownloadUrl());
        if(StringUtils.isBlank(asset.getDownloadUrl())) {
            LOG.warn("No download url for the asset");
            return null;
        }

        String prefix = "imp-asset-checksum-";
        String suffix = ".hash";
        LOG.debug(String.format("Creating tmp file, %s.%s", prefix, suffix));
        File tmpFile = File.createTempFile(prefix, suffix);

        LOG.debug("Writing checksums file to: " + tmpFile);
        FileUtils.copyURLToFile(new URL(asset.getDownloadUrl()), tmpFile);
        LOG.debug("Writing checksums file completed successfully: " + tmpFile);

        return tmpFile;
    }
}