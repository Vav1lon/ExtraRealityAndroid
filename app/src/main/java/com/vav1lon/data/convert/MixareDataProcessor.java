package com.vav1lon.data.convert;

import com.vav1lon.data.DataHandler;
import com.vav1lon.lib.marker.Marker;
import com.vav1lon.marker.POIMarker;

import org.json.JSONException;

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

        markers.add(new POIMarker("1", "Бабушкинская", 37.664524, 55.869738, 200.0, "", taskId, colour));
        markers.add(new POIMarker("2", "Медведково", 37.661563, 55.887409, 200.0, "", taskId, colour));
        markers.add(new POIMarker("3", "Магазин", 37.645641, 55.878339, 200.0, "", taskId, colour));
        markers.add(new POIMarker("4", "Мост на Яузе", 37.657228, 55.876675, 200.0, "", taskId, colour));
//
//        markers.add(new POIMarker("9", "Маслянный трансформатор", 55.87830248683688, 37.64838195858, 50, "", taskId, colour));
//
//        markers.add(new POIMarker("10", "Маслянный трансформатор", 65.87830248683688, 27.64838195858, 50, "", taskId, colour));
//        markers.add(new POIMarker("8", "Материал №2",  55.87830248683688, 37.648382558, 50, "", taskId, colour));
//        markers.add(new POIMarker("8", "Материал №8", 55.87916912212519, 37.64292098102568, 120, "", taskId, colour));

        return markers;
    }

}
