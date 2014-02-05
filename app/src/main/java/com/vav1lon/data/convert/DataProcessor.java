package com.vav1lon.data.convert;

import com.vav1lon.lib.marker.Marker;

import org.json.JSONException;

import java.util.List;

public interface DataProcessor {

    String[] getUrlMatch();

    String[] getDataMatch();

    boolean matchesRequiredType(String type);

    List<Marker> load(String rawData, int taskId, int colour) throws JSONException;
}
