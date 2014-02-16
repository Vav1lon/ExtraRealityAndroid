package com.vav1lon.mrg.webcontent;

import com.vav1lon.AppContext;

public class WebContentManagerFactory {

    public static WebContentManager makeWebContentManager(AppContext appContext) {
        return new WebPageMgrImpl(appContext);
    }

}
