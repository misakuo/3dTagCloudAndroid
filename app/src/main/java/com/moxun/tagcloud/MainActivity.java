package com.moxun.tagcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.moxun.tagcloudlib.view.TagCloudView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
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


                Toast.makeText(getBaseContext(), "postion:" + position + " clicked", Toast.LENGTH_SHORT).show();


                tagCloudView.stop();


                startViewAnimation(view, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {


                        view.clearAnimation();

                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                tagCloudView.start();

                            }
                        }, 300);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


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

    private void startViewAnimation(View view, Animation.AnimationListener animationListener) {
        final float originScaleX = view.getScaleX();
        final float originScaleY = view.getScaleY();

        final float originAlpha = view.getAlpha();

        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleUpAnimation = new ScaleAnimation(1f, 1.5f, 1f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUpAnimation.setDuration(500);
        scaleUpAnimation.setInterpolator(new LinearInterpolator());
        scaleUpAnimation.setRepeatCount(1);
        scaleUpAnimation.setRepeatMode(Animation.REVERSE);
        animationSet.addAnimation(scaleUpAnimation);


        Log.e(TAG, "originScaleX:" + originScaleX + "  originScaleY:" + originScaleY);



        animationSet.setAnimationListener(animationListener);
        view.startAnimation(animationSet);


    }

    @Override
    protected void onStart() {
        super.onStart();
        tagCloudView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tagCloudView.startWithAnimation();

            }
        },0);
    }
}
