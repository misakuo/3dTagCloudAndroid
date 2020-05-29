package com.moxun.tagcloudlib.view;

/**
 * Copyright © 2016 moxun
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagCloud {

    private List<Tag> mTagList;
    private int mRadius;
    private static final int DEFAULT_RADIUS = 3;
    private static final float[] DEFAULT_COLOR_DARK = {0.886f, 0.725f, 0.188f, 1f};
    private static final float[] DEFAULT_COLOR_LIGHT = {0.3f, 0.3f, 0.3f, 1f};
    private float[] mLightColor;
    private float[] mDarkColor;
    private float mSinX, mCosX, mSinY, mCosY, mSinZ, mCosZ;
    private float mInertiaZ = 0;
    private float mInertiaX = 0;
    private float mInertiaY = 0;

    private int mMinPopularity, mMaxPopularity;
    private boolean mRebuildOnUpdate = true;

    public TagCloud() {
        this(DEFAULT_RADIUS);
    }

    public TagCloud(int radius) {
        this(new ArrayList<Tag>(), radius);
    }

    public TagCloud(List<Tag> tags) {
        this(tags, DEFAULT_RADIUS);
    }

    public TagCloud(List<Tag> tags, int radius) {
        this(tags, radius, DEFAULT_COLOR_DARK, DEFAULT_COLOR_LIGHT);
    }

    public TagCloud(List<Tag> tags, int radius, float[] tagColor1, float[] tagColor2) {
        this.mTagList = tags;
        this.mRadius = radius;
        this.mLightColor = tagColor1;
        this.mDarkColor = tagColor2;
    }

    public void create(boolean rebuild) {
        this.mRebuildOnUpdate = rebuild;
        positionAll(mRebuildOnUpdate);
        calculatePopularity();
        recalculateAngle();
        updateAll();
    }

    public void clear() {
        mTagList.clear();
    }

    public List<Tag> getTagList() {
        return mTagList;
    }

    public Tag get(int position) {
        return mTagList.get(position);
    }

    public void reset() {
        create(mRebuildOnUpdate);
    }

    public void update() {
        if (Math.abs(mInertiaX) > 0.1f || Math.abs(mInertiaY) > 0.1f) {
            recalculateAngle();
            updateAll();
        }
    }

    private void initTag(Tag tag) {
        float percentage = getPercentage(tag);
        float[] argb = getColorFromGradient(percentage);
        tag.setColorComponent(argb);
    }

    private float getPercentage(Tag tag) {
        int p = tag.getPopularity();
        return (mMinPopularity == mMaxPopularity) ? 1.0f : ((float) p - mMinPopularity) / ((float) mMaxPopularity
            - mMinPopularity);
    }

    public void add(Tag newTag) {
        initTag(newTag);

        position(newTag);
        mTagList.add(newTag);
        updateAll();
    }

    private void position(Tag newTag) {
        double phi = Math.random() * (Math.PI);
        double theta = Math.random() * (2 * Math.PI);

        newTag.setSpatialX((int) (mRadius * Math.cos(theta) * Math.sin(phi)));
        newTag.setSpatialY((int) (mRadius * Math.sin(theta) * Math.sin(phi)));
        newTag.setSpatialZ((int) (mRadius * Math.cos(phi)));
    }

    private void positionAll(boolean rebuild) {
        double phi = 0;
        double theta = 0;
        int max = mTagList.size();
        //distribute: (disrtEven is used to specify whether distribute random or even
        for (int i = 1; i < max + 1; i++) {
            if (rebuild) {
                phi = Math.acos(-1.0 + (2.0 * i - 1.0) / max);
                theta = Math.sqrt(max * Math.PI) * phi;
            } else {
                phi = Math.random() * (Math.PI);
                theta = Math.random() * (2 * Math.PI);
            }

            //coordinate conversion:
            mTagList.get(i - 1).setSpatialX((int) ((mRadius * Math.cos(theta) * Math.sin(phi))
            ));
            mTagList.get(i - 1).setSpatialY((int) (mRadius * Math.sin(theta) * Math.sin(phi)));
            mTagList.get(i - 1).setSpatialZ((int) (mRadius * Math.cos(phi)));
        }
    }

    private float maxDelta, minDelta;

    private void updateAll() {

        //update transparency/scale for all tags:
        for (int j = 0; j < mTagList.size(); j++) {
            Tag tag = mTagList.get(j);
            float x = tag.getSpatialX();
            float y = tag.getSpatialY();
            float z = tag.getSpatialZ();

            //There exists two options for this part:
            // multiply positions by a x-rotation matrix
            float rx1 = x;
            float ry1 = y * mCosX + z * -mSinX;
            float rz1 = y * mSinX + z * mCosX;
            // multiply new positions by a y-rotation matrix
            float rx2 = rx1 * mCosY + rz1 * mSinY;
            float ry2 = ry1;
            float rz2 = rx1 * -mSinY + rz1 * mCosY;
            // multiply new positions by a z-rotation matrix
            float rx3 = rx2 * mCosZ + ry2 * -mSinZ;
            float ry3 = rx2 * mSinZ + ry2 * mCosZ;
            float rz3 = rz2;
            // set arrays to new positions
            tag.setSpatialX(rx3);
            tag.setSpatialY(ry3);
            tag.setSpatialZ(rz3);

            // add perspective
            int diameter = 2 * mRadius;
            float per = diameter / 1.0f / (diameter + rz3);
            // let's set position, scale, alpha for the tag;
            tag.setFlatX((int) (rx3 * per));
            tag.setFlatY((int) (ry3 * per));
            tag.setScale(per);

            // calculate alpha value
            float delta = diameter + rz3;
            maxDelta = Math.max(maxDelta, delta);
            minDelta = Math.min(minDelta, delta);
            float alpha = (delta - minDelta) / (maxDelta - minDelta);
            tag.setAlpha(1 - alpha);
        }
        sortTagByScale();
    }

    private float[] getColorFromGradient(float percentage) {
        float[] rgba = new float[4];
        rgba[0] = 1f;
        rgba[1] = (percentage * (mDarkColor[0])) + ((1f - percentage) * (mLightColor[0]));
        rgba[2] = (percentage * (mDarkColor[1])) + ((1f - percentage) * (mLightColor[1]));
        rgba[3] = (percentage * (mDarkColor[2])) + ((1f - percentage) * (mLightColor[2]));
        return rgba;
    }

    private void recalculateAngle() {
        double degToRad = (Math.PI / 180);
        mSinX = (float) Math.sin(mInertiaX * degToRad);
        mCosX = (float) Math.cos(mInertiaX * degToRad);
        mSinY = (float) Math.sin(mInertiaY * degToRad);
        mCosY = (float) Math.cos(mInertiaY * degToRad);
        mSinZ = (float) Math.sin(mInertiaZ * degToRad);
        mCosZ = (float) Math.cos(mInertiaZ * degToRad);
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    public void setTagColorLight(float[] tagColor) {
        this.mLightColor = tagColor;
    }

    public void setTagColorDark(float[] tagColorDark) {
        this.mDarkColor = tagColorDark;
    }

    public void setInertia(float x, float y) {
        this.mInertiaX = x;
        this.mInertiaY = y;
    }

    public void sortTagByScale() {
        Collections.sort(mTagList);
    }

    private void calculatePopularity() {
        for (int i = 0; i < mTagList.size(); i++) {
            Tag tag = mTagList.get(i);
            int popularity = tag.getPopularity();
            mMaxPopularity = Math.max(mMaxPopularity, popularity);
            mMinPopularity = Math.min(mMinPopularity, popularity);
        }

        for (Tag tag : mTagList) {
            initTag(tag);
        }
    }
}
