package com.vav1lon.mrg.location;

import android.hardware.GeomagneticField;
import android.location.Location;

import com.vav1lon.mrg.downloader.DownloadManager;

public interface LocationFinder {

    public enum LocationFinderState {
        Active, // Providing Location Information
        Inactive, // No-Active
        Confused // Same problem in internal state
    }

    void findLocation();

    void locationCallback(String provider);

    Location getCurrentLocation();

    Location getLocationAtLastDownload();

    void setLocationAtLastDownload(Location locationAtLastDownload);

    void setDownloadManager(DownloadManager downloadManager);

    void switchOn();

    void switchOff();

    LocationFinderState getStatus();

    GeomagneticField getGeomagneticField();

}