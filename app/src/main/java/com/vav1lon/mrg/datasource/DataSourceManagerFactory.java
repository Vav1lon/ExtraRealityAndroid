package com.vav1lon.mrg.datasource;

import com.vav1lon.MixContext;

/**
 * Factory Of DataSourceManager
 */
public class DataSourceManagerFactory {
    /**
     * Hide implementation Of DataSourceManager
     *
     * @param context
     * @return DataSourceManager
     */
    public static DataSourceManager makeDataSourceManager(MixContext ctx) {
        return new DataSourceMgrImpl(ctx);
    }
}
