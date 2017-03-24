package com.rogowiczdawid.smartnote.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.rogowiczdawid.smartnote.ListAdapter;
import com.rogowiczdawid.smartnote.MainActivity;
import com.rogowiczdawid.smartnote.Note;
import com.rogowiczdawid.smartnote.R;
import com.rogowiczdawid.smartnote.Utilities;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    final static String NOTE = "NOTE_FRAGMENT";
    final static String TODO = "TO_DO_FRAGMENT";
    public static boolean fab_visible;
    View rootView;
    ListView mainContainer;
    FloatingActionButton fabMain;
    FloatingActionButton fabNote;
    FloatingActionButton fabTodo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_fragment, container, false);
        mainContainer = (ListView) rootView.findViewById(R.id.gallery_container);

        fabMain = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        fabNote = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButtonNote);
        fabTodo = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButtonTodo);
        fab_visible = false;

        updateList();

        //Display or hide additional floating buttons
        fabMain.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab_visible) {
                    hideFabMenu();
                    fab_visible = false;
                } else {
                    displayFabMenu();
                    fab_visible = true;
                }
            }
        });


        //Open new NoteFragment
        fabNote.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.replaceFragment(new NoteFragment(), NOTE, getActivity().getFragmentManager().beginTransaction());
                Toast.makeText(getActivity(), R.string.new_note, Toast.LENGTH_SHORT).show();
            }
        });

        //Open new ToDoFragment
        fabTodo.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.replaceFragment(new ToDoFragment(), TODO, getActivity().getFragmentManager().beginTransaction());
                Toast.makeText(getActivity(), R.string.new_todo, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public void updateList() {
        ArrayList<Note> notes = Utilities.getNotes(getActivity());

        if (notes != null && notes.size() > 0)
            mainContainer.setAdapter(new ListAdapter(getActivity(), R.layout.list_item, notes));
        else Toast.makeText(getActivity(), "There are no saved notes", Toast.LENGTH_SHORT).show();
    }

    public void displayFabMenu() {

        FrameLayout.LayoutParams paramsNote = (FrameLayout.LayoutParams) fabNote.getLayoutParams();
        FrameLayout.LayoutParams paramsTodo = (FrameLayout.LayoutParams) fabTodo.getLayoutParams();

        paramsNote.bottomMargin += (int) (fabMain.getHeight() * 1.2);
        paramsTodo.bottomMargin += (int) (fabMain.getHeight() * 1.2 + fabNote.getHeight() * 1.2);

        fabNote.setLayoutParams(paramsNote);
        fabTodo.setLayoutParams(paramsTodo);

        Animation show_fab = AnimationUtils.loadAnimation(getActivity(), R.anim.show_fab);

        fabNote.startAnimation(show_fab);
        fabNote.setClickable(true);
        fabTodo.startAnimation(show_fab);
        fabTodo.setClickable(true);

    }

    public void hideFabMenu() {

        FrameLayout.LayoutParams paramsNote = (FrameLayout.LayoutParams) fabNote.getLayoutParams();
        FrameLayout.LayoutParams paramsTodo = (FrameLayout.LayoutParams) fabTodo.getLayoutParams();

        paramsNote.bottomMargin -= (int) (fabMain.getHeight() * 1.2);
        paramsTodo.bottomMargin -= (int) (fabMain.getHeight() * 1.2 + fabNote.getHeight() * 1.2);

        fabNote.setLayoutParams(paramsNote);
        fabTodo.setLayoutParams(paramsTodo);

        Animation hide_fab = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_fab);

        fabNote.startAnimation(hide_fab);
        fabNote.setClickable(false);
        fabTodo.startAnimation(hide_fab);
        fabTodo.setClickable(false);

    }
}
