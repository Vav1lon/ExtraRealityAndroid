package com.vav1lon.mrg.datasource;

import com.vav1lon.data.DataSource;

public interface DataSourceManager {

    boolean isAtLeastOneDatasourceSelected();

    void refreshDataSources();

    void setAllDataSourcesforLauncher(DataSource source);

    void requestDataFromAllActiveDataSource(double lat, double lon, double alt, float radius);

}
