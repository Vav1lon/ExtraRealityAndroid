package com.vav1lon.mrg.downloader;

import com.vav1lon.AppContext;

public class DownloadManagerFactory {

    public static DownloadManager makeDownloadManager(AppContext appContext) {
        return new DownloadMgrImpl(appContext);
    }
}
