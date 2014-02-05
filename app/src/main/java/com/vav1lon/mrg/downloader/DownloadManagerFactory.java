package com.vav1lon.mrg.downloader;

import com.vav1lon.MixContext;

public class DownloadManagerFactory {

    /**
     * Hide implementation Of DownloadManager
     *
     * @param mixContext
     * @return DownloadManager
     */
    public static DownloadManager makeDownloadManager(MixContext mixContext) {
        return new DownloadMgrImpl(mixContext);
    }
}
