package com.moxun.tagcloudlib.view;

import android.graphics.Color;
import android.view.View;

/**
 * Copyright © 2016 moxun
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

public class Tag {

    private int popularity;  //this is the importance/popularity of the Tag
    private float locX, locY, locZ; //the center of the 3D Tag
    private float loc2DX, loc2DY;
    private float scale;
    private float alpha;
    private float[] argb;
    private static final int DEFAULT_POPULARITY = 5;
    private View mView;


    public Tag() {
        this(0f, 0f, 0f, 1.0f, 0);
    }

    public Tag(int popularity) {
        this(0f, 0f, 0f, 1.0f, popularity);
    }

    public Tag(float locX, float locY, float locZ) {
        this(locX, locY, locZ, 1.0f, DEFAULT_POPULARITY);
    }

    public Tag(float locX, float locY, float locZ, float scale) {
        this(locX, locY, locZ, scale, DEFAULT_POPULARITY);
    }

    public Tag(float locX, float locY, float locZ, float scale, int popularity) {
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;

        this.loc2DX = 0;
        this.loc2DY = 0;

        this.argb = new float[]{1.0f, 0.5f, 0.5f, 0.5f};

        this.scale = scale;
        this.popularity = popularity;
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

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public float getAlpha() {
        return alpha;
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

    public void setColorByArray(float[] rgb) {
        if (rgb != null) {
            System.arraycopy(rgb, 0, this.argb, this.argb.length - rgb.length, rgb.length);
        }
    }

    public int getColor() {
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (int) (this.argb[i] * 0xff);
        }
        return Color.argb(result[0], result[1], result[2], result[3]);
    }
}
