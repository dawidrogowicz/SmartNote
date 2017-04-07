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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    TextView fabNoteText;
    TextView fabTodoText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_fragment, container, false);
        mainContainer = (ListView) rootView.findViewById(R.id.gallery_container);

        fabMain = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        fabNote = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButtonNote);
        fabTodo = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButtonTodo);
        fabNoteText = (TextView) rootView.findViewById(R.id.fab_text_note);
        fabTodoText = (TextView) rootView.findViewById(R.id.fab_text_todo);
        fab_visible = false;

        fabNoteText.setBackgroundResource(R.drawable.fab_text);
        fabTodoText.setBackgroundResource(R.drawable.fab_text);

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
            }
        });

        //Open new ToDoFragment
        fabTodo.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.replaceFragment(new ToDoFragment(), TODO, getActivity().getFragmentManager().beginTransaction());
            }
        });

        return rootView;
    }

    public void updateList() {
        ArrayList<Note> notes = Utilities.getNotes(getActivity());

        if (notes != null && notes.size() > 0)
            mainContainer.setAdapter(new ListAdapter(getActivity(), R.layout.list_item, notes));
        else Toast.makeText(getActivity(), R.string.no_saved_notes, Toast.LENGTH_SHORT).show();
    }

    public void displayFabMenu() {

        RelativeLayout.LayoutParams paramsNote = (RelativeLayout.LayoutParams) fabNote.getLayoutParams();
        RelativeLayout.LayoutParams paramsTodo = (RelativeLayout.LayoutParams) fabTodo.getLayoutParams();

        paramsNote.bottomMargin += (int) (fabMain.getHeight() * 1.2);
        paramsTodo.bottomMargin += (int) (fabMain.getHeight() * 1.2 + fabNote.getHeight() * 1.2);

        fabNote.setLayoutParams(paramsNote);
        fabTodo.setLayoutParams(paramsTodo);

        Animation show_fab = AnimationUtils.loadAnimation(getActivity(), R.anim.show_fab);

        fabNote.startAnimation(show_fab);
        fabNote.setClickable(true);
        fabNoteText.setAnimation(show_fab);
        fabTodo.startAnimation(show_fab);
        fabTodo.setClickable(true);
        fabTodoText.setAnimation(show_fab);

    }

    public void hideFabMenu() {

        RelativeLayout.LayoutParams paramsNote = (RelativeLayout.LayoutParams) fabNote.getLayoutParams();
        RelativeLayout.LayoutParams paramsTodo = (RelativeLayout.LayoutParams) fabTodo.getLayoutParams();

        paramsNote.bottomMargin -= (int) (fabMain.getHeight() * 1.2);
        paramsTodo.bottomMargin -= (int) (fabMain.getHeight() * 1.2 + fabNote.getHeight() * 1.2);

        fabNote.setLayoutParams(paramsNote);
        fabTodo.setLayoutParams(paramsTodo);

        Animation hide_upper_fab = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_fab);
        Animation hide_lower_fab = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_lower_fab);

        fabNote.startAnimation(hide_lower_fab);
        fabNote.setClickable(false);
        fabNoteText.setAnimation(hide_lower_fab);
        fabTodo.startAnimation(hide_upper_fab);
        fabTodo.setClickable(false);
        fabTodoText.setAnimation(hide_upper_fab);

    }
}
