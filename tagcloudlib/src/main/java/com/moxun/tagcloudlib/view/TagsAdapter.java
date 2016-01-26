package com.moxun.tagcloudlib.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

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

public abstract class TagsAdapter {
    private OnDataSetChangeListener onDataSetChangeListener;

    public abstract int getCount();
    public abstract View getView(Context context, int position, ViewGroup parent);
    public abstract Object getItem(int position);
    public abstract int getPopularity(int position);
    public abstract void onThemeColorChanged(View view,int themeColor);

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
