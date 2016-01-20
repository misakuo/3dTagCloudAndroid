package com.moxun.tagcloudlib.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by moxun on 16/1/19.
 */
public class TextTagsAdapter extends TagsAdapter{

    private List<String> dataSet = new ArrayList<>();

    public TextTagsAdapter(@NonNull String... data) {
        dataSet.clear();
        Collections.addAll(dataSet,data);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        return null;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}