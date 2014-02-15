package com.vav1lon;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.widget.Toast;

import com.vav1lon.lib.MixContextInterface;
import com.vav1lon.lib.render.Matrix;
import com.vav1lon.mrg.datasource.DataSourceManager;
import com.vav1lon.mrg.datasource.DataSourceManagerFactory;
import com.vav1lon.mrg.downloader.DownloadManager;
import com.vav1lon.mrg.downloader.DownloadManagerFactory;
import com.vav1lon.mrg.location.LocationFinder;
import com.vav1lon.mrg.location.LocationFinderFactory;
import com.vav1lon.mrg.webcontent.WebContentManager;
import com.vav1lon.mrg.webcontent.WebContentManagerFactory;

public class MixContext extends ContextWrapper implements MixContextInterface {

    public static final String TAG = "Mixare";

    private MixView mixView;

    private Matrix rotationM = new Matrix();

    private DownloadManager downloadManager;

    private LocationFinder locationFinder;

    private DataSourceManager dataSourceManager;

    private WebContentManager webContentManager;

    public MixContext(MixView appCtx) {
        super(appCtx);
        mixView = appCtx;

        // TODO: RE-ORDER THIS SEQUENCE... IS NECESSARY?
        getDataSourceManager().refreshDataSources();

        if (!getDataSourceManager().isAtLeastOneDatasourceSelected()) {
            rotationM.toIdentity();
        }
        getLocationFinder().switchOn();
        getLocationFinder().findLocation();
    }

    public String getStartUrl() {
        Intent intent = ((Activity) getActualMixView()).getIntent();
        if (intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_VIEW)) {
            return intent.getData().toString();
        } else {
            return "";
        }
    }

    public void getRM(Matrix dest) {
        synchronized (rotationM) {
            dest.set(rotationM);
        }
    }

    public void loadMixViewWebPage(String url) throws Exception {
        // TODO: CHECK INTERFACE METHOD
        getWebContentManager().loadWebPage(url, getActualMixView());
    }

    public void doResume(MixView mixView) {
        setActualMixView(mixView);
    }

    public void updateSmoothRotation(Matrix smoothR) {
        synchronized (rotationM) {
            rotationM.set(smoothR);
        }
    }

    public DataSourceManager getDataSourceManager() {
        if (this.dataSourceManager == null) {
            dataSourceManager = DataSourceManagerFactory
                    .makeDataSourceManager(this);
        }
        return dataSourceManager;
    }

    public LocationFinder getLocationFinder() {
        if (this.locationFinder == null) {
            locationFinder = LocationFinderFactory.makeLocationFinder(this);
        }
        return locationFinder;
    }

    public DownloadManager getDownloadManager() {
        if (this.downloadManager == null) {
            downloadManager = DownloadManagerFactory.makeDownloadManager(this);
            getLocationFinder().setDownloadManager(downloadManager);
        }
        return downloadManager;
    }

    public WebContentManager getWebContentManager() {
        if (this.webContentManager == null) {
            webContentManager = WebContentManagerFactory
                    .makeWebContentManager(this);
        }
        return webContentManager;
    }

    public MixView getActualMixView() {
        synchronized (mixView) {
            return this.mixView;
        }
    }

    private void setActualMixView(MixView mv) {
        synchronized (mixView) {
            this.mixView = mv;
        }
    }

    public ContentResolver getContentResolver() {
        ContentResolver out = super.getContentResolver();
        if (super.getContentResolver() == null) {
            out = getActualMixView().getContentResolver();
        }
        return out;
    }

    public void doPopUp(final String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    public void doPopUp(int RidOfString) {
        doPopUp(this.getString(RidOfString));
    }
}