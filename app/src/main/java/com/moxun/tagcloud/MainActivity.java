package com.moxun.tagcloud;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.moxun.tagcloudlib.view.TagCloudView;

public class MainActivity extends AppCompatActivity {

    private final String TAG=MainActivity.class.getSimpleName();
    private TagCloudView tagCloudView;
    private TextTagsAdapter textTagsAdapter;
    private ViewTagsAdapter viewTagsAdapter;
    private VectorTagsAdapter vectorTagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagCloudView = (TagCloudView) findViewById(R.id.tag_cloud);

        textTagsAdapter = new TextTagsAdapter(new String[10]);
        viewTagsAdapter = new ViewTagsAdapter();
        vectorTagsAdapter = new VectorTagsAdapter();

        tagCloudView.setAdapter(textTagsAdapter);
        tagCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, final View view, int position) {


                Toast.makeText(getBaseContext(),"postion:"+position+" clicked",Toast.LENGTH_SHORT).show();


                tagCloudView.stop();
                final float originScaleX=view.getScaleX();
                final float originScaleY=view.getScaleY();

                final float originAlpha=view.getAlpha();

                AnimationSet animationSet=new AnimationSet(true);
                ScaleAnimation scaleUpAnimation=new ScaleAnimation(originScaleX,originScaleX,originScaleY,originScaleY, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

                Log.e(TAG,"originScaleX:"+originScaleX+"  originScaleY:"+originScaleY);
                scaleUpAnimation.setDuration(300);
                scaleUpAnimation.setFillEnabled(false);
                animationSet.addAnimation(scaleUpAnimation);


                ScaleAnimation scaleDownAnimation=new ScaleAnimation(1.5f,originScaleX,1.5f,originScaleY,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

                scaleDownAnimation.setFillAfter(true);
                scaleDownAnimation.setDuration(150);

                scaleDownAnimation.setStartOffset(150);
                //animationSet.addAnimation(scaleDownAnimation);

                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {


                        view.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(animationSet);


                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        tagCloudView.start();

                    }
                },200);


            }
        });

        findViewById(R.id.tag_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagCloudView.setAdapter(textTagsAdapter);
            }
        });

        findViewById(R.id.tag_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagCloudView.setAdapter(viewTagsAdapter);
            }
        });

        findViewById(R.id.tag_vector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagCloudView.setAdapter(vectorTagsAdapter);
            }
        });

        findViewById(R.id.test_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FragmentTestActivity.class));
            }
        });
    }
}
