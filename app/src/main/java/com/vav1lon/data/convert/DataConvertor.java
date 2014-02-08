package com.vav1lon.data.convert;

import com.vav1lon.data.DataSource;
import com.vav1lon.lib.marker.Marker;
import com.vav1lon.lib.reality.PhysicalPlace;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DataConvertor {

    private List<DataProcessor> dataProcessors = new ArrayList<DataProcessor>();

    private static DataConvertor instance;

    public static DataConvertor getInstance() {
        if (instance == null) {
            instance = new DataConvertor();
            instance.addDefaultDataProcessors();
        }
        return instance;
    }

    public void clearDataProcessors() {
        dataProcessors.clear();
        addDefaultDataProcessors();
    }

    public void addDataProcessor(DataProcessor dataProcessor) {
        dataProcessors.add(dataProcessor);
    }

    public void removeDataProcessor(DataProcessor dataProcessor) {
        dataProcessors.remove(dataProcessor);
    }

    public List<Marker> load(String url, String rawResult, DataSource ds) {
        DataProcessor dataProcessor = searchForMatchingDataProcessors(url, rawResult, ds.getType());
        if (dataProcessor == null) {
            dataProcessor = new MixareDataProcessor(); //using this as default if nothing is found.
        }
        try {
            return dataProcessor.load(rawResult, ds.getTaskId(), ds.getColor());
        } catch (JSONException e) {
        }
        return null;
    }

    private DataProcessor searchForMatchingDataProcessors(String url, String rawResult, DataSource.TYPE type) {
        for (DataProcessor dp : dataProcessors) {
            if (dp.matchesRequiredType(type.name())) {
                //checking if url matches any dataprocessor identifiers
                for (String urlIdentifier : dp.getUrlMatch()) {
                    if (url.toLowerCase().contains(urlIdentifier.toLowerCase())) {
                        return dp;
                    }
                }
                //checking if data matches any dataprocessor identifiers
                for (String dataIdentifier : dp.getDataMatch()) {
                    if (rawResult.contains(dataIdentifier)) {
                        return dp;
                    }
                }
            }
        }
        return null;
    }

    private void addDefaultDataProcessors() {
//        dataProcessors.add(new WikiDataProcessor());
//        dataProcessors.add(new OsmDataProcessor());
    }

    public static String getOSMBoundingBox(double lat, double lon, double radius) {
        String bbox = "[bbox=";
        PhysicalPlace lb = new PhysicalPlace(); // left bottom
        PhysicalPlace rt = new PhysicalPlace(); // right top
        PhysicalPlace.calcDestination(lat, lon, 225, radius * 1414, lb); // 1414: sqrt(2)*1000
        PhysicalPlace.calcDestination(lat, lon, 45, radius * 1414, rt);
        bbox += lb.getLongitude() + "," + lb.getLatitude() + "," + rt.getLongitude() + "," + rt.getLatitude() + "]";
        return bbox;
    }

}
