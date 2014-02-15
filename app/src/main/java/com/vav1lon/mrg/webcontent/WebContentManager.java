package com.vav1lon.mrg.webcontent;

import android.content.Context;

public interface WebContentManager {

    void loadWebPage(String url, Context context) throws Exception;

    boolean processUrl(String url, Context ctx);

}