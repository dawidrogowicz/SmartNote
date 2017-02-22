package com.rogowiczdawid.smartnote;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GalleryFragment extends Fragment {
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_fragment, container, false);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("something", "something");
        super.onSaveInstanceState(outState);
    }
}
