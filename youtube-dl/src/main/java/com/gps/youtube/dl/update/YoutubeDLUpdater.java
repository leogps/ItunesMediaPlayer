package com.gps.youtube.dl.update;

import com.gps.imp.utils.ui.InterruptableAsyncTask;
import com.gps.itunes.media.player.updater.GithubReleaseUpdater;
import com.gps.itunes.media.player.updater.UpdateResult;

/**
 * Created by leogps on 2/25/17.
 */
public class YoutubeDLUpdater {

    public InterruptableAsyncTask<Void, UpdateResult> update(String youtubeDLExecutable, String repositoryUrl, String assetName, String supportedChecksums) {
        GithubReleaseUpdater githubReleaseUpdater = new GithubReleaseUpdater();
        return githubReleaseUpdater.update(youtubeDLExecutable, repositoryUrl, assetName, supportedChecksums);
    }
}
