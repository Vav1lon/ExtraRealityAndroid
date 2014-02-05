package com.vav1lon.lib.data;

import com.vav1lon.lib.marker.InitialMarkerData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public abstract class PluginDataProcessor {

    /**
     * @return the strings that should match the url.
     */
    public abstract String[] getUrlMatch();

    /**
     * @return the strings that should match the content of the url.
     */
    public abstract String[] getDataMatch();

    /**
     * This method converts raw data (main.res.xml, json, html) from the content from an url, to marker objects
     *
     * @return a list of markerdata, which can be used to build markers.
     */
    public abstract List<InitialMarkerData> load(String rawData, int taskId, int colour) throws JSONException;

    protected JSONObject convertToJSON(String rawData) {
        try {
            return new JSONObject(rawData);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
