package com.vav1lon.plugin.connection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.vav1lon.data.convert.DataConvertor;
import com.vav1lon.lib.service.IDataHandlerService;
import com.vav1lon.plugin.PluginConnection;
import com.vav1lon.plugin.remoteobjects.RemoteDataHandler;

public class DataHandlerServiceConnection extends PluginConnection implements
        ServiceConnection {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // get instance of the aidl binder
        IDataHandlerService iDataHandlerService = IDataHandlerService.Stub
                .asInterface(service);
        RemoteDataHandler rm = new RemoteDataHandler(iDataHandlerService);
        rm.buildDataHandler();
        DataConvertor.getInstance().addDataProcessor(rm);
        storeFoundPlugin();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        DataConvertor.getInstance().clearDataProcessors();
    }

}
