package com.vav1lon.data.convert;

import com.vav1lon.POIMarker;
import com.vav1lon.data.DataHandler;
import com.vav1lon.lib.marker.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MixareDataProcessor extends DataHandler implements DataProcessor {

    public static final int MAX_JSON_OBJECTS = 1000;

    @Override
    public String[] getUrlMatch() {
        String[] str = new String[0]; //only use this data source if all the others don't match
        return str;
    }

    @Override
    public String[] getDataMatch() {
        String[] str = new String[0]; //only use this data source if all the others don't match
        return str;
    }

    @Override
    public boolean matchesRequiredType(String type) {
        return true; //this datasources has no required type, it will always match.
    }

    @Override
    public List<Marker> load(String rawData, int taskId, int colour) throws JSONException {
        List<Marker> markers = new ArrayList<Marker>();
        JSONObject root = convertToJSON(rawData);
        JSONArray dataArray = root.getJSONArray("results");
        int top = Math.min(MAX_JSON_OBJECTS, dataArray.length());

        markers.add(new POIMarker("1", "Материал №1", 55.717608, 37.601652, 120, "", taskId, colour));
        markers.add(new POIMarker("2", "Материал №2", 55.717608, 37.602300, 120, "", taskId, colour));
        markers.add(new POIMarker("3", "Материал №3", 55.717608, 37.603900, 120, "", taskId, colour));
        markers.add(new POIMarker("4", "Материал №4", 55.717608, 37.604900, 120, "", taskId, colour));
        markers.add(new POIMarker("5", "Материал №5", 55.717608, 37.605900, 120, "", taskId, colour));
        markers.add(new POIMarker("6", "Материал №6", 55.717608, 37.606900, 120, "", taskId, colour));
        markers.add(new POIMarker("7", "Материал №7", 55.717608, 37.607900, 120, "", taskId, colour));
        markers.add(new POIMarker("8", "Материал №8", 55.717608, 37.608900, 120, "", taskId, colour));

        return markers;
    }

    private JSONObject convertToJSON(String rawData) {
        try {
            return new JSONObject(rawData);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}
