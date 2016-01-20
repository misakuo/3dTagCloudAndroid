package com.moxun.tagcloud;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.moxun.tagcloudlib.view.Tag;
import com.moxun.tagcloudlib.view.TagCloudView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TagCloudView tagCloudView = new TagCloudView(this, 600, 600, createTags());
        tagCloudView.requestFocus();
        tagCloudView.setFocusableInTouchMode(true);
        tagCloudView.setBackgroundColor(Color.LTGRAY);

        LinearLayout layout = (LinearLayout) findViewById(R.id.container);
        layout.addView(tagCloudView);
    }

    private List<Tag> createTags() {
        List<Tag> tempList = new ArrayList<>();
        tempList.add(new Tag("乐乘", 7, "yuecheng"));
        tempList.add(new Tag("嘉灵", 5, "chentong.ct"));
        tempList.add(new Tag("阿大", 6, "ada"));
        tempList.add(new Tag("左穆", 3, "zuomu.zdd"));
        tempList.add(new Tag("天翎", 5, "tianling.lk"));
        tempList.add(new Tag("墨循", 6, "moxun.ljf"));
        tempList.add(new Tag("红发", 7, "hongfa.yy"));
        tempList.add(new Tag("莫凌", 5, "chunbo.lcb"));
        tempList.add(new Tag("星驰", 3, "xingchi.mxc"));
        tempList.add(new Tag("栖邀", 5, "qiyao.kxp"));
        tempList.add(new Tag("左御", 5, "zuoyu.ht"));
        tempList.add(new Tag("栖邀", 5, "qiyao.kxp"));
        tempList.add(new Tag("左御", 5, "zuoyu.ht"));
        return tempList;
    }
}
