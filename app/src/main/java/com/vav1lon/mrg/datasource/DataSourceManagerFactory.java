package com.vav1lon.mrg.datasource;

import com.vav1lon.AppContext;

public class DataSourceManagerFactory {

    public static DataSourceManager makeDataSourceManager(AppContext ctx) {
        return new DataSourceMgrImpl(ctx);
    }
}
