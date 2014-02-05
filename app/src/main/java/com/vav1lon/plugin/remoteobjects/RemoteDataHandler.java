package com.vav1lon.plugin.remoteobjects;

import android.os.RemoteException;

import com.vav1lon.data.DataHandler;
import com.vav1lon.data.convert.DataProcessor;
import com.vav1lon.lib.marker.InitialMarkerData;
import com.vav1lon.lib.marker.Marker;
import com.vav1lon.lib.marker.draw.ParcelableProperty;
import com.vav1lon.lib.marker.draw.PrimitiveProperty;
import com.vav1lon.lib.service.IDataHandlerService;
import com.vav1lon.plugin.PluginLoader;
import com.vav1lon.plugin.PluginNotFoundException;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RemoteDataHandler extends DataHandler implements DataProcessor {

    private String dataHandlerName;
    private IDataHandlerService iDataHandlerService;

    public String getDataHandlerName() {
        return dataHandlerName;
    }

    public RemoteDataHandler(IDataHandlerService iDataHandlerService) {
        this.iDataHandlerService = iDataHandlerService;
    }

    public void buildDataHandler() {
        try {
            this.dataHandlerName = iDataHandlerService.build();
        } catch (RemoteException e) {
            throw new PluginNotFoundException(e);
        }
    }

    public String[] getUrlMatch() {
        try {
            return iDataHandlerService.getUrlMatch(dataHandlerName);
        } catch (RemoteException e) {
            throw new PluginNotFoundException(e);
        }
    }

    public String[] getDataMatch() {
        try {
            return iDataHandlerService.getDataMatch(dataHandlerName);
        } catch (RemoteException e) {
            throw new PluginNotFoundException(e);
        }
    }

    @Override
    public boolean matchesRequiredType(String type) {
        //TODO: change the datasource so that it can have more types,
        //      so that plugins can also have a required type
        return true;
    }


    public List<Marker> load(String rawData, int taskId, int colour) throws JSONException {
        try {
            List<InitialMarkerData> initialMarkerData = iDataHandlerService.load(dataHandlerName, rawData, taskId, colour);
            return initializeMarkerData(initialMarkerData);
        } catch (RemoteException e) {
            throw new PluginNotFoundException(e);
        }
    }

    private List<Marker> initializeMarkerData(List<InitialMarkerData> initialMarkerData) throws PluginNotFoundException, RemoteException {
        List<Marker> markers = new ArrayList<Marker>();
        for (InitialMarkerData i : initialMarkerData) {
            Marker marker = PluginLoader.getInstance().getMarkerInstance(i.getMarkerName(), (Integer) i.getConstr()[0],
                    (String) i.getConstr()[1], (Double) i.getConstr()[2], (Double) i.getConstr()[3],
                    (Double) i.getConstr()[4], (String) i.getConstr()[5], (Integer) i.getConstr()[6], (Integer) i.getConstr()[7]);
            fillExtraMarkerParcelableProperties(marker, i.getExtraParcelables());
            fillExtraMarkerPrimitiveProperties(marker, i.getExtraPrimitives());

            markers.add(marker);
        }
        return markers;
    }

    private Marker fillExtraMarkerParcelableProperties(Marker marker, Map<String, ParcelableProperty> properties) {
        Iterator<Entry<String, ParcelableProperty>> it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, ParcelableProperty> pairs = (Entry<String, ParcelableProperty>) it.next();
            marker.setExtras(pairs.getKey(), pairs.getValue());
        }
        return marker;
    }

    private Marker fillExtraMarkerPrimitiveProperties(Marker marker, Map<String, PrimitiveProperty> properties) {
        Iterator<Entry<String, PrimitiveProperty>> it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, PrimitiveProperty> pairs = (Entry<String, PrimitiveProperty>) it.next();
            marker.setExtras(pairs.getKey(), pairs.getValue());
        }
        return marker;
    }


}
