package com.moxun.tagcloud;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.moxun.tagcloudlib.view.TagsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by moxun on 16/1/19.
 */
public class TextTagsAdapter extends TagsAdapter {

    private final String TAG=TextTagsAdapter.class.getSimpleName();

    private List<String> dataSet = new ArrayList<>();

    public TextTagsAdapter(@NonNull String... data) {
        dataSet.clear();
        Collections.addAll(dataSet, data);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public View getView(final Context context, final int position, ViewGroup parent) {
        final TextView tv = new TextView(context);
        tv.setTextSize(11);
        tv.setPadding(10,10,10,10);
        tv.setText("OK失望 v 吧No." + position);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);

        return tv;
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor, float alpha) {
//        view.setBackgroundColor(themeColor);

        alpha*=alpha;
        view.setAlpha(alpha<0.15f?0.0f:alpha);
    }
}
