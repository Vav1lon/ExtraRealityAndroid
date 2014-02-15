package com.vav1lon.mrg.datasource;

import com.vav1lon.MixContext;

public class DataSourceManagerFactory {

    public static DataSourceManager makeDataSourceManager(MixContext ctx) {
        return new DataSourceMgrImpl(ctx);
    }
}
