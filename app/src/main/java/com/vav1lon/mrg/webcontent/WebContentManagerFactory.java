package com.vav1lon.mrg.webcontent;

import com.vav1lon.MixContext;

public class WebContentManagerFactory {
    /**
     * Hide implementation Of WebContentManager
     *
     * @param mixContext
     * @return WebContentManager
     */
    public static WebContentManager makeWebContentManager(MixContext mixContext) {
        return new WebPageMgrImpl(mixContext);
    }

}
