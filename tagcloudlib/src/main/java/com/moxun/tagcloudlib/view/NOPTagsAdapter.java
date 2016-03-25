package com.moxun.tagcloudlib.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Default adapter and do nothing
 * Created by moxun on 16/3/25.
 */
/*package*/ class NOPTagsAdapter extends TagsAdapter {
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

    @Override
    public int getPopularity(int position) {
        return 0;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }
}
