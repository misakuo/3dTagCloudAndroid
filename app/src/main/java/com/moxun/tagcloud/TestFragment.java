package com.moxun.tagcloud;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moxun.tagcloudlib.view.TagCloudView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment {
    private View rootView;
    private TagCloudView fragmentTagcloud;

    public TestFragment() {
        // Required empty public constructor
    }

    public static TestFragment newInstance() {
        TestFragment fragment = new TestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_test, container, false);
        instantiationViews();
        TextTagsAdapter adapter = new TextTagsAdapter(new String[20]);
        fragmentTagcloud.setAdapter(adapter);
        return rootView;
    }

    private void instantiationViews() {
        fragmentTagcloud = (TagCloudView) rootView.findViewById(R.id.fragment_tagcloud);
    }
}
