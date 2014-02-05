package com.vav1lon.plugin.connection;

import android.app.Activity;
import android.content.ServiceConnection;

public interface ActivityConnection extends ServiceConnection {

    void startActivityForResult(Activity activity);

}
