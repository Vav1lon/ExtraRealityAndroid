package com.vav1lon.mrg.downloader;

public interface DownloadManager {

    enum DownloadManagerState {
        OnLine, //manage downlad request
        OffLine, // No OnLine
        Downloading, //Process some Download Request
        Confused // Internal state not congruent
    }

    void resetActivity();

    String submitJob(DownloadRequest job);

    DownloadResult getReqResult(String jobId);

    DownloadResult getNextResult();

    int getResultSize();

    Boolean isDone();

    void switchOn();

    void switchOff();

    DownloadManagerState getState();

}