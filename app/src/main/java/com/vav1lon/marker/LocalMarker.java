package com.vav1lon.marker;

import android.graphics.Bitmap;
import android.location.Location;

import com.vav1lon.lib.MixContextInterface;
import com.vav1lon.lib.MixStateInterface;
import com.vav1lon.lib.MixUtils;
import com.vav1lon.lib.gui.Label;
import com.vav1lon.lib.gui.PaintScreen;
import com.vav1lon.lib.gui.ScreenLine;
import com.vav1lon.lib.gui.TextObj;
import com.vav1lon.lib.marker.Marker;
import com.vav1lon.lib.marker.draw.ParcelableProperty;
import com.vav1lon.lib.marker.draw.PrimitiveProperty;
import com.vav1lon.lib.reality.PhysicalPlace;
import com.vav1lon.lib.render.Camera;
import com.vav1lon.lib.render.MixVector;

import java.net.URLDecoder;
import java.text.DecimalFormat;

public abstract class LocalMarker implements Marker {

    private String ID;
    protected String title;
    protected boolean underline = false;
    private String URL;
    protected PhysicalPlace mGeoLoc;
    protected double distance;
    private int colour;

    private boolean active;

    protected boolean isVisible;
    public MixVector cMarker = new MixVector();
    protected MixVector signMarker = new MixVector();

    protected MixVector locationVector = new MixVector();
    private MixVector origin = new MixVector(0, 0, 0);
    private MixVector upV = new MixVector(0, 1, 0);
    private ScreenLine pPt = new ScreenLine();

    public Label txtLab = new Label();
    protected TextObj textBlock;

    public LocalMarker(String id, String title, double latitude, double longitude, double altitude, String link, int type, int colour) {
        super();

        this.active = false;
        this.title = title;
        this.mGeoLoc = new PhysicalPlace(latitude, longitude, altitude);
        if (link != null && link.length() > 0) {
            URL = "webpage:" + URLDecoder.decode(link);
            this.underline = true;
        }
        this.colour = colour;

        this.ID = id + "##" + type + "##" + title;
    }


    public String getTitle() {
        return title;
    }

    public String getURL() {
        return URL;
    }

    public double getLatitude() {
        return mGeoLoc.getLatitude();
    }

    public double getLongitude() {
        return mGeoLoc.getLongitude();
    }

    public double getAltitude() {
        return mGeoLoc.getAltitude();
    }

    public MixVector getLocationVector() {
        return locationVector;
    }

    private void cCMarker(MixVector originalPoint, Camera viewCam, float addX, float addY) {

        // Temp properties
        MixVector tmpa = new MixVector(originalPoint);
        MixVector tmpc = new MixVector(upV);
        tmpa.add(locationVector); //3
        tmpc.add(locationVector); //3
        tmpa.sub(viewCam.lco); //4
        tmpc.sub(viewCam.lco); //4
        tmpa.prod(viewCam.transform); //5
        tmpc.prod(viewCam.transform); //5

        MixVector tmpb = new MixVector();
        viewCam.projectPoint(tmpa, tmpb, addX, addY); //6
        cMarker.set(tmpb); //7
        viewCam.projectPoint(tmpc, tmpb, addX, addY); //6
        signMarker.set(tmpb); //7
    }

    private void calcV(Camera viewCam) {
        isVisible = false;
//		isLookingAt = false;
//		deltaCenter = Float.MAX_VALUE;

        if (cMarker.z < -1f) {
            isVisible = true;
        }
    }

    public void update(Location curGPSFix) {
        // An elevation of 0.0 probably means that the elevation of the
        // POI is not known and should be set to the users GPS height
        // Note: this could be improved with calls to
        // http://www.geonames.org/export/web-services.html#astergdem
        // to estimate the correct height with DEM models like SRTM, AGDEM or GTOPO30
        if (mGeoLoc.getAltitude() == 0.0)
            mGeoLoc.setAltitude(curGPSFix.getAltitude());

        // compute the relative position vector from user position to POI location
        PhysicalPlace.convLocToVec(curGPSFix, mGeoLoc, locationVector);
    }

    public void calcPaint(Camera viewCam, float addX, float addY) {
        cCMarker(origin, viewCam, addX, addY);
        calcV(viewCam);
    }

    private boolean isClickValid(float x, float y) {

        //if the marker is not active (i.e. not shown in AR view) we don't have to check it for clicks
        if (!isActive() && !this.isVisible)
            return false;

        float currentAngle = MixUtils.getAngle(cMarker.x, cMarker.y,
                signMarker.x, signMarker.y);
        //TODO adapt the following to the variable radius!
        pPt.x = x - signMarker.x;
        pPt.y = y - signMarker.y;
        pPt.rotate((float) Math.toRadians(-(currentAngle + 90)));
        pPt.x += txtLab.getX();
        pPt.y += txtLab.getY();

        float objX = txtLab.getX() - txtLab.getWidth() / 2;
        float objY = txtLab.getY() - txtLab.getHeight() / 2;
        float objW = txtLab.getWidth();
        float objH = txtLab.getHeight();

        if (pPt.x > objX && pPt.x < objX + objW && pPt.y > objY
                && pPt.y < objY + objH) {
            return true;
        } else {
            return false;
        }
    }

    public void draw(PaintScreen dw) {
        drawCircle(dw);
        drawTextBlock(dw);
    }

    public void drawCircle(PaintScreen dw) {

        if (isVisible) {
            //float maxHeight = Math.round(dw.getHeight() / 10f) + 1;
            float maxHeight = dw.getHeight();
            dw.setStrokeWidth(maxHeight / 100f);
            dw.setFill(false);
            //dw.setColor(DataSource.getColor(type));

            //draw circle with radius depending on distance
            //0.44 is approx. vertical fov in radians
            double angle = 2.0 * Math.atan2(10, distance);
            double radius = Math.max(Math.min(angle / 0.44 * maxHeight, maxHeight), maxHeight / 25f);
            //double radius = angle/0.44d * (double)maxHeight;

            dw.paintCircle(cMarker.x, cMarker.y, (float) radius);
        }
    }

    public void drawTextBlock(PaintScreen dw) {
        //TODO: grandezza cerchi e trasparenza
        float maxHeight = Math.round(dw.getHeight() / 10f) + 1;

        //TODO: change textblock only when distance changes
        String textStr = "";

        double d = distance;
        DecimalFormat df = new DecimalFormat("@#");
        if (d < 1000.0) {
            textStr = title + " (" + df.format(d) + "m)";
        } else {
            d = d / 1000.0;
            textStr = title + " (" + df.format(d) + "km)";
        }

        textBlock = new TextObj(textStr, Math.round(maxHeight / 2f) + 1,
                250, dw, underline);

        if (isVisible) {

            //dw.setColor(DataSource.getColor(type));

            float currentAngle = MixUtils.getAngle(cMarker.x, cMarker.y, signMarker.x, signMarker.y);

            txtLab.prepare(textBlock);

            dw.setStrokeWidth(1f);
            dw.setFill(true);
            dw.paintObj(txtLab, signMarker.x - txtLab.getWidth()
                    / 2, signMarker.y + maxHeight, currentAngle + 90, 1);
        }

    }

    public boolean fClick(float x, float y, MixContextInterface ctx, MixStateInterface state) {
        boolean evtHandled = false;

        if (isClickValid(x, y)) {
            evtHandled = state.handleEvent(ctx, URL);
        }
        return evtHandled;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public int compareTo(Marker another) {

        Marker leftPm = this;
        Marker rightPm = another;

        return Double.compare(leftPm.getDistance(), rightPm.getDistance());

    }

    @Override
    public boolean equals(Object marker) {
        return this.ID.equals(((Marker) marker).getID());
    }

    @Override
    public int hashCode() {
        return this.ID.hashCode();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    abstract public int getMaxObjects();

    public void setImage(Bitmap image) {
    }

    public Bitmap getImage() {
        return null;
    }

    public int getColour() {
        return colour;
    }

    @Override
    public void setTxtLab(Label txtLab) {
        this.txtLab = txtLab;
    }

    @Override
    public Label getTxtLab() {
        return txtLab;
    }

    public void setExtras(String name, PrimitiveProperty primitiveProperty) {
        //nothing to add
    }

    public void setExtras(String name, ParcelableProperty parcelableProperty) {
        //nothing to add
    }
}
