package com.moxun.tagcloudlib.view;
/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 *
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TagCloud {

    private List<Tag> tagCloud;
    private int radius;
    private static final int DEFAULT_RADIUS = 3;
    private static final int TEXT_SIZE_MAX = 30, TEXT_SIZE_MIN = 4;
    private static final float[] DEFAULT_COLOR1 = {0.886f, 0.725f, 0.188f, 1f};
    private static final float[] DEFAULT_COLOR2 = {0.3f, 0.3f, 0.3f, 1f};
    private float[] tagColor1;  //text color 1(rgb Alpha)
    private float[] tagColor2; //text color 2 (rgb Alpha)
    private int textSizeMax, textSizeMin;
    private float sin_mAngleX, cos_mAngleX, sin_mAngleY, cos_mAngleY, sin_mAngleZ, cos_mAngleZ;
    private float mAngleZ = 0;
    private float mAngleX = 0;
    private float mAngleY = 0;
    private int size = 0;
    private int smallest, largest; //used to find spectrum for tag colors
    private boolean distrEven = true; //default is to distribute tags evenly on the Cloud

    public TagCloud() {
        this(new ArrayList<Tag>());
    }

    public TagCloud(List<Tag> tags) {
        this(tags, DEFAULT_RADIUS);
    }

    //Constructor just copies the existing tags in its List
    public TagCloud(List<Tag> tags, int radius) {
        this(tags, radius, DEFAULT_COLOR1, DEFAULT_COLOR2, TEXT_SIZE_MIN, TEXT_SIZE_MAX);
    }

    public TagCloud(List<Tag> tags, int radius, int textSizeMin, int textSizeMax) {
        this(tags, radius, DEFAULT_COLOR1, DEFAULT_COLOR2, textSizeMin, textSizeMax);
    }

    public TagCloud(List<Tag> tags, int radius, float[] tagColor1, float[] tagColor2) {
        this(tags, radius, tagColor1, tagColor2, TEXT_SIZE_MIN, TEXT_SIZE_MAX);
    }

    public TagCloud(List<Tag> tags, int radius, float[] tagColor1, float[] tagColor2,
                    int textSizeMin, int textSizeMax) {
        this.tagCloud = tags;    //Java does the initialization and deep copying
        this.radius = radius;
        this.tagColor1 = tagColor1;
        this.tagColor2 = tagColor2;
        this.textSizeMax = textSizeMax;
        this.textSizeMin = textSizeMin;
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

        this.size = tagCloud.size();
    }

    public List<Tag> getTagList() {
        return tagCloud;
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
        float[] tempColor = getColorFromGradient(percentage); //(rgb Alpha)
        int tempTextSize = getTextSizeGradient(percentage);
        tag.setColorByArray(tempColor);
        tag.setTextSize(tempTextSize);
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
        this.size = tagCloud.size();
        updateAll();
    }

    //to replace an existing tag with a new one
    //it returns the location of the replacement, if not found=> returns -1
    public int Replace(Tag newTag, String oldTagText) {
        int result = -1;
        //let's go over all elements of tagCloud list and see if the oldTagText exists:
        for (int i = 0; i < tagCloud.size(); i++) {
            if (oldTagText.equalsIgnoreCase(tagCloud.get(i).getText())) {
                result = i;
                Tag tempTag = tagCloud.get(i);
                tempTag.setPopularity(newTag.getPopularity());
                tempTag.setText(newTag.getText());

                initTag(tempTag);

                newTag = tempTag;
                break;
            }
        }
        return result;
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
            tagCloud.get(j).setAlpha(per / 2);
        }
        depthSort();
    }

    ///now let's sort all tags in the tagCloud based on their z coordinate
    //this way, when they are finally drawn, upper tags will be drawn on top of lower tags
    private void depthSort() {
        Collections.sort(tagCloud);
    }

    private float[] getColorFromGradient(float perc) {
        float[] tempRGB = new float[4];
        tempRGB[0] = (perc * (tagColor1[0])) + ((1 - perc) * (tagColor2[0]));
        tempRGB[1] = (perc * (tagColor1[1])) + ((1 - perc) * (tagColor2[1]));
        tempRGB[2] = (perc * (tagColor1[2])) + ((1 - perc) * (tagColor2[2]));
        tempRGB[3] = 1;
        return tempRGB;
    }

    private int getTextSizeGradient(float perc) {
        int size;
        size = (int) (perc * textSizeMax + (1 - perc) * textSizeMin);
        return size;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public float[] getTagColor1() {
        return tagColor1;
    }

    public void setTagColor1(float[] tagColor) {
        this.tagColor1 = tagColor;
    }

    public float[] getTagColor2() {
        return tagColor2;
    }

    public void setTagColor2(float[] tagColor2) {
        this.tagColor2 = tagColor2;
    }

    public float getRvalue(float[] color) {
        if (color.length > 0)
            return color[0];
        else
            return 0;
    }

    public float getGvalue(float[] color) {
        if (color.length > 0)
            return color[1];
        else
            return 0;
    }

    public float getBvalue(float[] color) {
        if (color.length > 0)
            return color[2];
        else
            return 0;
    }

    public float getAlphaValue(float[] color) {
        if (color.length >= 4)
            return color[3];
        else
            return 0;
    }

    public float getAngleX() {
        return mAngleX;
    }

    public void setAngleX(float mAngleX) {
        this.mAngleX = mAngleX;
    }

    public float getAngleY() {
        return mAngleY;
    }

    public void setAngleY(float mAngleY) {
        this.mAngleY = mAngleY;
    }

    public int getSize() {
        return size;
    }


}
