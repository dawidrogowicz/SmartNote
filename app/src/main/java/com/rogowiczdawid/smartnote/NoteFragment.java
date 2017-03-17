package com.rogowiczdawid.smartnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

public class NoteFragment extends Fragment {
    View rootView;
    EditText title;
    MultiAutoCompleteTextView editText;
    boolean title_bar_down = true;
    private String text_val;
    private String title_val = "Title";
    private MyFragmentListener myFragmentListener;


    public static NoteFragment newInstance(String title, String text) {

        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putString("TEXT", text);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.note_fragment, container, false);
        title = (EditText) rootView.findViewById(R.id.note_title_bar);
        editText = (MultiAutoCompleteTextView) rootView.findViewById(R.id.note_text);

        if (getArguments() != null) {
            title_val = getArguments().getString("TITLE");
            text_val = getArguments().getString("TEXT");

            title.setText(title_val);
            editText.setText(text_val);
        }

        //setting up listener for the title
        myFragmentListener = new MyFragmentListener(getActivity()) {
            @Override
            public void fling(float v1) {
                if (title_bar_down && v1 < -7000) {
                    title.animate().translationY(title.getHeight() * (-1)).setDuration(195).start();
                    title_bar_down = false;
                } else if (!title_bar_down && v1 > 7000) {
                    title.animate().translationY(0).setDuration(225).start();
                    title_bar_down = true;
                }
            }
        };

        ((MainActivity) getActivity()).addMyOnTouchListener(new MainActivity.MyOnTouchListener() {
            @Override
            public void onTouch(MotionEvent ev) {
                myFragmentListener.onTouch(ev);
            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                title_val = String.valueOf(title.getText());
            }
        });

        //change default title to first typed word if it wasn't changed before
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText.getText() != null) {
                    text_val = String.valueOf(editText.getText());
                    title_val = String.valueOf(title.getText());
                    if (title_val.equals("Title")) {
                        int index = text_val.length();
                        char last = text_val.charAt(index - 1);
                        if ((text_val.length() > 5) && (last == ' ')) {
                            title_val = text_val.substring(0, index);
                            title.setText(title_val);
                        }
                    }
                }
            }
        });

        return rootView;
    }

    public String getTitleValue() {
        return title_val;
    }

    public String getTextVal() {
        return text_val;
    }
}
