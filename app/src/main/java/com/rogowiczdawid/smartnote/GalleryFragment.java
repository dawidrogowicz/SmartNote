package com.rogowiczdawid.smartnote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    View rootView;
    ListView mainContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_fragment, container, false);
        mainContainer = (ListView) rootView.findViewById(R.id.gallery_container);

        updateList();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void updateList() {
        ArrayList<Note> notes = FileManager.getNotes(getActivity());

        if (notes != null && notes.size() > 0)
            mainContainer.setAdapter(new ListAdapter(getContext(), R.layout.list_item, notes));
        else Toast.makeText(getContext(), "There's no saved notes", Toast.LENGTH_SHORT).show();

    }
}
