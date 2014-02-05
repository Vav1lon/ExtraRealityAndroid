package com.vav1lon.mrg.location;

import com.vav1lon.MixContext;

public class LocationFinderFactory {

    /**
     * Hide implementation Of LocationFinder
     *
     * @param mixContext
     * @return LocationFinder
     */
    public static LocationFinder makeLocationFinder(MixContext mixContext) {
        return new LocationMgrImpl(mixContext);
    }

}
