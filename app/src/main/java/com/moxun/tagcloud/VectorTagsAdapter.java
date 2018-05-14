package com.moxun.tagcloud;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moxun.tagcloudlib.view.TagsAdapter;

/**
 * Created by moxun on 16/3/11.
 */
public class VectorTagsAdapter extends TagsAdapter {
    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.tag_item_vector, parent, false);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getPopularity(int position) {
        Log.e("","Popularity" + position % 5);
        return position % 5;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor, float alpha) {
        ImageView imageView = (ImageView) view.findViewById(R.id.vector_img);
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(themeColor,
                PorterDuff.Mode.SRC_ATOP);
        if (imageView == null) {
            return;
        }
        imageView.getDrawable().setColorFilter(porterDuffColorFilter);
    }
}
