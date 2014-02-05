package com.vav1lon.mrg.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationResolver implements LocationListener {

    private String provider;
    private LocationMgrImpl locationMgrImpl;
    private LocationManager lm;

    public LocationResolver(LocationManager lm, String provider, LocationMgrImpl locationMgrImpl) {
        this.lm = lm;
        this.provider = provider;
        this.locationMgrImpl = locationMgrImpl;
    }

    @Override
    public void onLocationChanged(Location location) {
        lm.removeUpdates(this);
        locationMgrImpl.locationCallback(provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
