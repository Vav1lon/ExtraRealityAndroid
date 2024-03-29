package com.vav1lon;

import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.vav1lon.data.DataHandler;
import com.vav1lon.lib.gui.PaintScreen;
import com.vav1lon.lib.gui.ScreenLine;
import com.vav1lon.lib.marker.Marker;
import com.vav1lon.lib.render.Camera;
import com.vav1lon.mrg.downloader.DownloadManager;
import com.vav1lon.mrg.downloader.DownloadResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.KeyEvent.KEYCODE_CAMERA;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

public class DataView {

    private AppContext appContext;
    private boolean isInit;
    private int width;
    private int height;
    private Camera cam;
    private AppState state = new AppState();
    private boolean frozen;
    private int retry;
    private Location curFix;
    private DataHandler dataHandler = new DataHandler();
    private float radius = 5;
    private Timer refresh = null;
    private final long refreshDelay = 45 * 1000; // refresh every 45 seconds

    private boolean isLauncherStarted;

    private ArrayList<UIEvent> uiEvents = new ArrayList<UIEvent>();

    private ScreenLine lrl = new ScreenLine();
    private ScreenLine rrl = new ScreenLine();
    private float rx = 10, ry = 20;
    private float addX = 0, addY = 0;

    private List<Marker> markers;

    public DataView(AppContext ctx) {
        this.appContext = ctx;
    }

    public AppContext getContext() {
        return appContext;
    }

    public boolean isLauncherStarted() {
        return isLauncherStarted;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public boolean isDetailsView() {
        return state.isDetailsView();
    }

    public void setDetailsView(boolean detailsView) {
        state.setDetailsView(detailsView);
    }

    public void doStart() {
        state.nextLStatus = AppState.NOT_STARTED;
        appContext.getLocationFinder().setLocationAtLastDownload(curFix);
    }

    public boolean isInited() {
        return isInit;
    }

    public void init(int widthInit, int heightInit) {
        try {
            width = widthInit;
            height = heightInit;

            cam = new Camera(width, height, true);
            cam.setViewAngle(Camera.DEFAULT_VIEW_ANGLE);

            lrl.rotate(Camera.DEFAULT_VIEW_ANGLE / 2);
            rrl.rotate(-Camera.DEFAULT_VIEW_ANGLE / 2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        frozen = false;
        isInit = true;
    }

    public void requestData(String url) {

        state.nextLStatus = AppState.PROCESSING;
    }

    public void draw(PaintScreen dw) {
        appContext.getRM(cam.transform);
        curFix = appContext.getLocationFinder().getCurrentLocation();

//        curFix.setLongitude(37.640491);
//        curFix.setLatitude(55.879416);

        curFix.setLongitude(55.879416);
        curFix.setLatitude(37.640491);

        state.calcPitchBearing(cam.transform);

        // Load Layer
        if (state.nextLStatus == AppState.NOT_STARTED && !frozen) {
            loadDrawLayer();
            markers = new ArrayList<Marker>();
        } else if (state.nextLStatus == AppState.PROCESSING) {
            DownloadManager dm = appContext.getDownloadManager();
            DownloadResult dRes = null;

            markers.addAll(downloadDrawResults(dm, dRes));

            if (dm.isDone()) {
                retry = 0;
                state.nextLStatus = AppState.DONE;

                dataHandler = new DataHandler();
                dataHandler.addMarkers(markers);
                dataHandler.onLocationChanged(curFix);

                if (refresh == null) { // start the refresh timer if it is null
                    refresh = new Timer(false);
                    Date date = new Date(System.currentTimeMillis()
                            + refreshDelay);
                    refresh.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            callRefreshToast();
                            refresh();
                        }
                    }, date, refreshDelay);
                }
            }
        }

        // Update markers
        dataHandler.updateActivationStatus(appContext);
        for (int i = dataHandler.getMarkerCount() - 1; i >= 0; i--) {
            Marker ma = dataHandler.getMarker(i);
            // if (ma.isActive() && (ma.getDistance() / 1000f < radius || ma
            // instanceof NavigationMarker || ma instanceof SocialMarker)) {
//            if (ma.isActive() && (ma.getDistance() / 1000f < radius)) {

            // To increase performance don't recalculate position vector
            // for every marker on every draw call, instead do this only
            // after onLocationChanged and after downloading new marker
            // if (!frozen)
            // ma.update(curFix);
            if (!frozen)
                ma.calcPaint(cam, addX, addY);
            ma.draw(dw);
        }
//        }

        // Get next event
        UIEvent evt = null;
        synchronized (uiEvents) {
            if (uiEvents.size() > 0) {
                evt = uiEvents.get(0);
                uiEvents.remove(0);
            }
        }
        if (evt != null) {
            switch (evt.type) {
                case UIEvent.KEY:
                    handleKeyEvent((KeyEvent) evt);
                    break;
                case UIEvent.CLICK:
                    handleClickEvent((ClickEvent) evt);
                    break;
            }
        }
        state.nextLStatus = AppState.PROCESSING;
    }

    /**
     * Part of draw function, loads the layer.
     */
    private void loadDrawLayer() {
        if (appContext.getStartUrl().length() > 0) {
            requestData(appContext.getStartUrl());
            isLauncherStarted = true;
        } else {
            double lat = curFix.getLatitude(), lon = curFix.getLongitude(), alt = curFix
                    .getAltitude();
            state.nextLStatus = AppState.PROCESSING;
            appContext.getDataSourceManager().requestDataFromAllActiveDataSource(lat, lon, alt, radius);
        }

        // if no datasources are activated
        if (state.nextLStatus == AppState.NOT_STARTED)
            state.nextLStatus = AppState.DONE;
    }

    private List<Marker> downloadDrawResults(DownloadManager dm, DownloadResult dRes) {
        List<Marker> markers = new ArrayList<Marker>();
        while ((dRes = dm.getNextResult()) != null) {
            if (dRes.isError() && retry < 3) {
                retry++;
                appContext.getDownloadManager().submitJob(
                        dRes.getErrorRequest());
                // Notification
                // Toast.makeText(mixContext, dRes.errorMsg,
                // Toast.LENGTH_SHORT).show();
            }

            if (!dRes.isError()) {
                if (dRes.getMarkers() != null) {
                    //jLayer = (DataHandler) dRes.obj;
                    Log.i(AppView.TAG, "Adding Markers");
                    markers.addAll(dRes.getMarkers());

                    // Notification
                    Toast.makeText(
                            appContext,
                            appContext.getResources().getString(
                                    R.string.download_received)
                                    + " " + dRes.getDataSource().getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return markers;
    }

    private void handleKeyEvent(KeyEvent evt) {
        /** Adjust marker position with keypad */
        final float CONST = 10f;
        switch (evt.keyCode) {
            case KEYCODE_DPAD_LEFT:
                addX -= CONST;
                break;
            case KEYCODE_DPAD_RIGHT:
                addX += CONST;
                break;
            case KEYCODE_DPAD_DOWN:
                addY += CONST;
                break;
            case KEYCODE_DPAD_UP:
                addY -= CONST;
                break;
            case KEYCODE_DPAD_CENTER:
                frozen = !frozen;
                break;
            case KEYCODE_CAMERA:
                frozen = !frozen;
                break; // freeze the overlay with the camera button
            default: //if key is set, then ignore event
                break;
        }
    }

    boolean handleClickEvent(ClickEvent evt) {
        int selectedId = 0;
        boolean evtHandled = false;

        // Handle event
        if (state.nextLStatus == AppState.DONE) {
            // the following will traverse the markers in ascending order (by
            // distance) the first marker that
            // matches triggers the event.
            //TODO handle collection of markers. (what if user wants the one at the back)
            for (int i = 0; i < dataHandler.getMarkerCount() && !evtHandled; i++) {
                Marker pm = dataHandler.getMarker(i);

                evtHandled = pm.fClick(evt.x, evt.y, appContext, state);
                if (evtHandled)
                    selectedId = i;
            }

            if (evtHandled) {
                Intent intent = new Intent(getContext(), MenuItemActivity.class);
                intent.putExtra("test", selectedId);
                getContext().startActivity(intent);
            }

        }
        return evtHandled;
    }

    public void clickEvent(float x, float y) {
        synchronized (uiEvents) {
            uiEvents.add(new ClickEvent(x, y));
        }
    }

    public void keyEvent(int keyCode) {
        synchronized (uiEvents) {
            uiEvents.add(new KeyEvent(keyCode));
        }
    }

    public void clearEvents() {
        synchronized (uiEvents) {
            uiEvents.clear();
        }
    }

    public void cancelRefreshTimer() {
        if (refresh != null) {
            refresh.cancel();
        }
    }

    public void refresh() {
        state.nextLStatus = AppState.NOT_STARTED;
    }

    private void callRefreshToast() {
        appContext.getActualMixView().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(
                        appContext,
                        appContext.getResources()
                                .getString(R.string.refreshing),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}

class UIEvent {
    public static final int CLICK = 0;
    public static final int KEY = 1;

    public int type;
}

class ClickEvent extends UIEvent {
    public float x, y;

    public ClickEvent(float x, float y) {
        this.type = CLICK;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

class KeyEvent extends UIEvent {
    public int keyCode;

    public KeyEvent(int keyCode) {
        this.type = KEY;
        this.keyCode = keyCode;
    }

    @Override
    public String toString() {
        return "(" + keyCode + ")";
    }
}