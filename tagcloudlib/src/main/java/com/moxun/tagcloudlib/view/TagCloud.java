package com.moxun.tagcloudlib.view;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TagCloud {

    private List<Tag> tagCloud;
    private int radius;
    private static final int DEFAULT_RADIUS = 3;
    private static final float[] DEFAULT_COLOR_DARK = {0.886f, 0.725f, 0.188f, 1f};
    private static final float[] DEFAULT_COLOR_LIGHT = {0.3f, 0.3f, 0.3f, 1f};
    private float[] tagColorLight;  //text color 1(rgb Alpha)
    private float[] tagColorDark; //text color 2 (rgb Alpha)
    private float sin_mAngleX, cos_mAngleX, sin_mAngleY, cos_mAngleY, sin_mAngleZ, cos_mAngleZ;
    private float mAngleZ = 0;
    private float mAngleX = 0;
    private float mAngleY = 0;
    //private int size = 0;
    private int smallest, largest; //used to find spectrum for tag colors
    private boolean distrEven = true; //default is to distribute tags evenly on the Cloud

    public TagCloud() {
        this(DEFAULT_RADIUS);
    }

    public TagCloud(int radius) {
        this(new ArrayList<Tag>(), radius);
    }

    public TagCloud(List<Tag> tags) {
        this(tags, DEFAULT_RADIUS);
    }

    //Constructor just copies the existing tags in its List
    public TagCloud(List<Tag> tags, int radius) {
        this(tags, radius, DEFAULT_COLOR_DARK, DEFAULT_COLOR_LIGHT);
    }

    public TagCloud(List<Tag> tags, int radius, float[] tagColor1, float[] tagColor2) {
        this.tagCloud = tags;    //Java does the initialization and deep copying
        this.radius = radius;
        this.tagColorLight = tagColor1;
        this.tagColorDark = tagColor2;
    }

    //create method calculates the correct initial location of each tag
    public void create(boolean distrEven) {
        this.distrEven = distrEven;
        //calculate and set the location of each Tag
        positionAll(distrEven);
        sineCosine(mAngleX, mAngleY, mAngleZ);
        updateAll();
        //Now, let's calculate and set the color for each tag:
        //first loop through all tags to find the smallest and largest populariteies
        //largest popularity gets tcolor2, smallest gets tcolor1, the rest in between
        smallest = 9999;
        largest = 0;
        for (int i = 0; i < tagCloud.size(); i++) {
            int j = tagCloud.get(i).getPopularity();
            largest = Math.max(largest, j);
            smallest = Math.min(smallest, j);
        }
        //figuring out and assigning the colors/ textsize
        Tag tempTag;
        for (int i = 0; i < tagCloud.size(); i++) {
            tempTag = tagCloud.get(i);

            initTag(tempTag);
        }
    }

    public void clear() {
        tagCloud.clear();
    }

    public List<Tag> getTagList() {
        return tagCloud;
    }

    public void setTagList(List<Tag> list) {
        tagCloud = list;
    }

    public Tag get(int position) {
        return tagCloud.get(position);
    }

    public Tag getTop() {
        int i = tagCloud.size() - 1;
        return get(i);
    }

    public int indexOf(Tag tag) {
        return tagCloud.indexOf(tag);
    }

    public void reset() {
        create(distrEven);
    }

    //updates the transparency/scale of all elements
    public void update() {
        // if mAngleX and mAngleY under threshold, skip motion calculations for performance
        if (Math.abs(mAngleX) > .1 || Math.abs(mAngleY) > .1) {
            sineCosine(mAngleX, mAngleY, mAngleZ);
            updateAll();
        }
    }

    private void initTag(Tag tag) {
        float percentage = getPercentage(tag);
        float[] argb = getColorFromGradient(percentage);
        tag.setColorByArray(argb);
    }

    private float getPercentage(Tag tag) {
        int p = tag.getPopularity();
        return (smallest == largest) ? 1.0f : ((float) p - smallest) / ((float) largest - smallest);
    }

    //if a single tag needed to be added
    public void add(Tag newTag) {

        initTag(newTag);

        position(distrEven, newTag);
        //now add the new tag to the tagCloud
        tagCloud.add(newTag);
        updateAll();
    }

    private void position(boolean distrEven, Tag newTag) {
        double phi = 0;
        double theta = 0;
        int max = tagCloud.size();
        //when adding a new tag, just place it at some random location
        //this is in fact why adding too many elements make TagCloud ugly
        //after many add, do one reset to rearrange all tags
        phi = Math.random() * (Math.PI);
        theta = Math.random() * (2 * Math.PI);
        //coordinate conversion:
        newTag.setLocX((int) (radius * Math.cos(theta) * Math.sin(phi)));
        newTag.setLocY((int) (radius * Math.sin(theta) * Math.sin(phi)));
        newTag.setLocZ((int) (radius * Math.cos(phi)));
    }

    private void positionAll(boolean distrEven) {
        double phi = 0;
        double theta = 0;
        int max = tagCloud.size();
        //distribute: (disrtEven is used to specify whether distribute random or even
        for (int i = 1; i < max + 1; i++) {
            if (distrEven) {
                phi = Math.acos(-1.0 + (2.0 * i - 1.0) / max);
                theta = Math.sqrt(max * Math.PI) * phi;
            } else {
                phi = Math.random() * (Math.PI);
                theta = Math.random() * (2 * Math.PI);
            }

            //coordinate conversion:
            tagCloud.get(i - 1).setLocX((int) ((radius * Math.cos(theta) * Math.sin(phi))
            ));
            tagCloud.get(i - 1).setLocY((int) (radius * Math.sin(theta) * Math.sin(phi)));
            tagCloud.get(i - 1).setLocZ((int) (radius * Math.cos(phi)));
        }
    }

    float maxDelta = Float.MIN_VALUE;
    float minDelta = Float.MAX_VALUE;

    private void updateAll() {

        //update transparency/scale for all tags:
        int max = tagCloud.size();
        for (int j = 0; j < max; j++) {
            //There exists two options for this part:
            // multiply positions by a x-rotation matrix
            float rx1 = (tagCloud.get(j).getLocX());
            float ry1 = (tagCloud.get(j).getLocY()) * cos_mAngleX +
                    tagCloud.get(j).getLocZ() * -sin_mAngleX;
            float rz1 = (tagCloud.get(j).getLocY()) * sin_mAngleX +
                    tagCloud.get(j).getLocZ() * cos_mAngleX;
            // multiply new positions by a y-rotation matrix
            float rx2 = rx1 * cos_mAngleY + rz1 * sin_mAngleY;
            float ry2 = ry1;
            float rz2 = rx1 * -sin_mAngleY + rz1 * cos_mAngleY;
            // multiply new positions by a z-rotation matrix
            float rx3 = rx2 * cos_mAngleZ + ry2 * -sin_mAngleZ;
            float ry3 = rx2 * sin_mAngleZ + ry2 * cos_mAngleZ;
            float rz3 = rz2;
            // set arrays to new positions
            tagCloud.get(j).setLocX(rx3);
            tagCloud.get(j).setLocY(ry3);
            tagCloud.get(j).setLocZ(rz3);

            // add perspective
            int diameter = 2 * radius;
            float per = diameter / 1.0f / (diameter + rz3);
            // let's set position, scale, alpha for the tag;
            tagCloud.get(j).setLoc2DX((int) (rx3 * per));
            tagCloud.get(j).setLoc2DY((int) (ry3 * per));
            tagCloud.get(j).setScale(per);

            // calculate alpha value
            float delta = diameter + rz3;
            maxDelta = Math.max(maxDelta, delta);
            minDelta = Math.min(minDelta, delta);
            float alpha = (delta - minDelta) / (maxDelta - minDelta);
            tagCloud.get(j).setAlpha(1 - alpha);
        }
        sortTagByScale();
    }

    private float[] getColorFromGradient(float percentage) {
        //perc: 1.0 full dark; 0.0 full light
        float[] rgba = new float[4];
        rgba[0] = 1f; //Alpha is 1.0 when init.
        rgba[1] = (percentage * (tagColorDark[0])) + ((1f - percentage) * (tagColorLight[0]));
        rgba[2] = (percentage * (tagColorDark[1])) + ((1f - percentage) * (tagColorLight[1]));
        rgba[3] = (percentage * (tagColorDark[2])) + ((1f - percentage) * (tagColorLight[2]));
        return rgba;
    }

    private void sineCosine(float mAngleX, float mAngleY, float mAngleZ) {
        double degToRad = (Math.PI / 180);
        sin_mAngleX = (float) Math.sin(mAngleX * degToRad);
        cos_mAngleX = (float) Math.cos(mAngleX * degToRad);
        sin_mAngleY = (float) Math.sin(mAngleY * degToRad);
        cos_mAngleY = (float) Math.cos(mAngleY * degToRad);
        sin_mAngleZ = (float) Math.sin(mAngleZ * degToRad);
        cos_mAngleZ = (float) Math.cos(mAngleZ * degToRad);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setTagColorLight(float[] tagColor) {
        this.tagColorLight = tagColor;
    }

    public void setTagColorDark(float[] tagColorDark) {
        this.tagColorDark = tagColorDark;
    }

    public void setAngleX(float mAngleX) {
        this.mAngleX = mAngleX;
    }

    public void setAngleY(float mAngleY) {
        this.mAngleY = mAngleY;
    }

    public void sortTagByScale() {
        Collections.sort(tagCloud, new TagComparator());
    }

    private static class TagComparator implements Comparator<Tag> {

        @Override
        public int compare(Tag o1, Tag o2) {
            return o1.getScale() > o2.getScale() ? 1 : -1;
        }
    }
}
