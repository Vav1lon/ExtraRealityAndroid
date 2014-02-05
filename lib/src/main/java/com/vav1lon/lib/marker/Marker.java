package com.vav1lon.lib.marker;

import android.location.Location;

import com.vav1lon.lib.MixContextInterface;
import com.vav1lon.lib.MixStateInterface;
import com.vav1lon.lib.gui.Label;
import com.vav1lon.lib.gui.PaintScreen;
import com.vav1lon.lib.marker.draw.ParcelableProperty;
import com.vav1lon.lib.marker.draw.PrimitiveProperty;
import com.vav1lon.lib.render.Camera;
import com.vav1lon.lib.render.MixVector;

public interface Marker extends Comparable<Marker> {

    String getTitle();

    String getURL();

    double getLatitude();

    double getLongitude();

    double getAltitude();

    MixVector getLocationVector();

    void update(Location curGPSFix);

    void calcPaint(Camera viewCam, float addX, float addY);

    void draw(PaintScreen dw);

    double getDistance();

    void setDistance(double distance);

    String getID();

    void setID(String iD);

    boolean isActive();

    void setActive(boolean active);

    int getColour();

    public void setTxtLab(Label txtLab);

    Label getTxtLab();

    public boolean fClick(float x, float y, MixContextInterface ctx, MixStateInterface state);

    int getMaxObjects();

    void setExtras(String name, ParcelableProperty parcelableProperty);

    void setExtras(String name, PrimitiveProperty primitiveProperty);

}