package com.vav1lon.mrg.downloader;

public interface DownloadManager {

    /**
     * Possible state of this Manager
     */
    enum DownloadManagerState {
        OnLine, //manage downlad request
        OffLine, // No OnLine
        Downloading, //Process some Download Request
        Confused // Internal state not congruent
    }

    /**
     * Reset all Request and Responce
     */
    void resetActivity();

    /**
     * Submit new DownloadRequest
     *
     * @param job
     * @return reference Of Job or null if job is rejected
     */
    String submitJob(DownloadRequest job);

    /**
     * Get result of job if exist, null otherwise
     *
     * @param jobId reference of Job
     * @return result
     */
    DownloadResult getReqResult(String jobId);

    /**
     * Pseudo Iterator on results
     *
     * @return actual Download Result
     */
    DownloadResult getNextResult();

    /**
     * Gets the number of downloaded results
     *
     * @return the number of results
     */
    int getResultSize();

    /**
     * check if all Download request is done
     *
     * @return BOOLEAN
     */
    Boolean isDone();

    /**
     * Request to active the service
     */
    void switchOn();

    /**
     * Request to deactive the service
     */
    void switchOff();

    /**
     * Request state of service
     *
     * @return
     */
    DownloadManagerState getState();

}