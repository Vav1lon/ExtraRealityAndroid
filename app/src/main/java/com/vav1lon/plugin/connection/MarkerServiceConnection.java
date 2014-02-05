package com.vav1lon.plugin.connection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.vav1lon.lib.service.IMarkerService;
import com.vav1lon.plugin.PluginConnection;
import com.vav1lon.plugin.PluginNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class MarkerServiceConnection extends PluginConnection implements ServiceConnection {

    private Map<String, IMarkerService> markerServices = new HashMap<String, IMarkerService>();

    @Override
    public void onServiceDisconnected(ComponentName name) {
        markerServices.clear();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // get instance of the aidl binder
        IMarkerService iMarkerService = IMarkerService.Stub
                .asInterface(service);
        try {
            String markername = iMarkerService.getPluginName();
            markerServices.put(markername, iMarkerService);
            storeFoundPlugin();
        } catch (RemoteException e) {
            throw new PluginNotFoundException(e);
        }
    }

    public Map<String, IMarkerService> getMarkerServices() {
        return markerServices;
    }
}
