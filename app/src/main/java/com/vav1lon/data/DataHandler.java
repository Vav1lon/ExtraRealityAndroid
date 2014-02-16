package com.vav1lon.data;

import android.location.Location;
import android.util.Log;

import com.vav1lon.AppContext;
import com.vav1lon.AppView;
import com.vav1lon.lib.marker.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class DataHandler {

    private List<Marker> markerList = new ArrayList<Marker>();

    public void addMarkers(List<Marker> markers) {

        Log.v(AppView.TAG, "Marker before: " + markerList.size());
        for (Marker ma : markers) {
            if (!markerList.contains(ma))
                markerList.add(ma);
        }

        Log.d(AppView.TAG, "Marker count: " + markerList.size());
    }

    public void sortMarkerList() {
        Collections.sort(markerList);
    }

    public void updateDistances(Location location) {
        for (Marker ma : markerList) {
            float[] dist = new float[3];
            Location.distanceBetween(ma.getLatitude(), ma.getLongitude(), location.getLatitude(), location.getLongitude(), dist);
            ma.setDistance(dist[0]);
        }
    }

    public void updateActivationStatus(AppContext appContext) {

        Hashtable<Class, Integer> map = new Hashtable<Class, Integer>();
        for (Marker ma : markerList) {

            Class<? extends Marker> mClass = ma.getClass();
            map.put(mClass, (map.get(mClass) != null) ? map.get(mClass) + 1 : 1);

            boolean belowMax = (map.get(mClass) <= ma.getMaxObjects());

            ma.setActive((belowMax));
        }
    }

    public void onLocationChanged(Location location) {
        updateDistances(location);
        sortMarkerList();
        for (Marker ma : markerList) {
            ma.update(location);
        }
    }

    public List<Marker> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<Marker> markerList) {
        this.markerList = markerList;
    }

    public int getMarkerCount() {
        return markerList.size();
    }

    public Marker getMarker(int index) {
        return markerList.get(index);
    }
}
