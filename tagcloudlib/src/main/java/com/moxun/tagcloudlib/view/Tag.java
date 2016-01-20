package com.moxun.tagcloudlib.view;

import android.graphics.Color;
import android.util.Log;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 *
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

/*
 * Tag class:
 * For now tags are just cubes. Later they will be replaced by real texts!
 */
public class Tag implements Comparable<Tag> {

    private String text;
    private Object obj;
    private int popularity;  //this is the importance/popularity of the Tag
    private int textSize;
    private float locX, locY, locZ; //the center of the 3D Tag
    private float loc2DX, loc2DY;
    private float scale;
    private float alpha;
    private int color;
    private float[] argb;
    private static final int DEFAULT_POPULARITY = 1;
    private int paramNo; //parameter that holds the setting for this Tag

    public Tag() {
        this("", 0f, 0f, 0f, 1.0f, 0, "");
    }

    public Tag(String text, int popularity) {
        this(text, 0f, 0f, 0f, 1.0f, popularity, "");
    }

    public Tag(String text, int popularity, Object obj) {
        this(text, 0f, 0f, 0f, 1.0f, popularity, obj);
    }

    public Tag(String text, float locX, float locY, float locZ) {
        this(text, locX, locY, locZ, 1.0f, DEFAULT_POPULARITY, "");
    }

    public Tag(String text, float locX, float locY, float locZ, float scale) {
        this(text, locX, locY, locZ, scale, DEFAULT_POPULARITY, "");
    }

    public Tag(String text, float locX, float locY, float locZ, float scale, int popularity,
               Object obj) {
        this.text = text;
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;

        this.loc2DX = 0;
        this.loc2DY = 0;

        this.alpha = 1.0f;

        this.argb = new float[]{1.0f, 0.5f, 0.5f, 0.5f};

        this.scale = scale;
        this.popularity = popularity;
        this.obj = obj;
    }

    @Override
    public int compareTo(Tag another) {
        return (int) (another.locZ - locZ);
    }

    public float getLocX() {
        return locX;
    }

    public void setLocX(float locX) {
        this.locX = locX;
    }

    public float getLocY() {
        return locY;
    }

    public void setLocY(float locY) {
        this.locY = locY;
    }

    public float getLocZ() {
        return locZ;
    }

    public void setLocZ(float locZ) {
        this.locZ = locZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this.argb[0] = alpha;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public float getLoc2DX() {
        return loc2DX;
    }

    public void setLoc2DX(float loc2dx) {
        loc2DX = loc2dx;
    }

    public float getLoc2DY() {
        return loc2DY;
    }

    public void setLoc2DY(float loc2dy) {
        loc2DY = loc2dy;
    }

    public int getParamNo() {
        return paramNo;
    }

    public void setParamNo(int paramNo) {
        this.paramNo = paramNo;
    }

    public Object getObject() {
        return obj;
    }

    public void setColorByArray(float[] rgb) {
        if (rgb != null) {
            System.arraycopy(rgb, 0, this.argb, this.argb.length - rgb.length , rgb.length);
        }
    }

    public int getColor() {
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (int) (this.argb[i] * 256);
        }
        return Color.argb(result[0], result[1], result[2], result[3]);
    }
}
