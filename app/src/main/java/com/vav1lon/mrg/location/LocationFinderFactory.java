package com.vav1lon.mrg.location;

import com.vav1lon.AppContext;

public class LocationFinderFactory {

    public static LocationFinder makeLocationFinder(AppContext appContext) {
        return new LocationMgrImpl(appContext);
    }

}
