package com.moxun.tagcloudlib.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by moxun on 16/1/19.
 */
public abstract class TagsAdapter {
    private OnDataSetChangeListener onDataSetChangeListener;

    public abstract int getCount();
    public abstract View getView(Context context, int position, ViewGroup parent);
    public abstract Object getItem(int position);
    public abstract int getPopularity(int position);

    public final void notifyDataSetChanged() {
        onDataSetChangeListener.onChange();
    }

    protected interface OnDataSetChangeListener{
        void onChange();
    }

    protected void setOnDataSetChangeListener(OnDataSetChangeListener listener) {
        onDataSetChangeListener = listener;
    }
}
