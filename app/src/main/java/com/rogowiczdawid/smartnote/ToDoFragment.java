package com.rogowiczdawid.smartnote;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;


public class ToDoFragment extends Fragment implements View.OnClickListener {

    public boolean title_bar_down = true;
    GestureDetectorCompat detector;
    ArrayList<String> user_list;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.to_do_list_fragment, container, false);

        detector = new GestureDetectorCompat(getActivity(), new ToDoFragment.MyGestureDetector());

        //restoring savedInstanceState
        user_list = new ArrayList<>();
        if (savedInstanceState != null) {
            user_list = savedInstanceState.getStringArrayList("user_list");
            if (user_list != null) {
                int count = user_list.size();
                for (int i = 0; i < count; i++) {
                    createLayout(user_list.get(i));
                }
            }
        }


        Button add_button = (Button) rootView.findViewById(R.id.add_button);
        add_button.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Allow title to be changed after long click
        final EditText title = (EditText) rootView.findViewById(R.id.title_bar);
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                title.setFocusable(true);
                title.requestFocusFromTouch();

                InputMethodManager inp = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inp.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);
                return true;
            }
        });
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    title.setFocusable(false);
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("user_list", user_list);

        EditText title = (EditText) rootView.findViewById(R.id.title_bar);
        outState.putString("title", String.valueOf(title.getText()));
    }

    ////////////////ADDING ITEMS TO LIST///////////////////
    public void createLayout(final String text) {

        //Adding new horizontal Layout with RelativeLayout inside
        final LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new DrawerLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setWeightSum(8);

        //Adding TextView with scrollView to new Layout
        final TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(20);
        textView.setText(text);

        HorizontalScrollView scrollView = new HorizontalScrollView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 6);
        params.gravity = Gravity.CENTER_VERTICAL;
        scrollView.setLayoutParams(params);
        scrollView.addView(textView);

        layout.addView(scrollView);

        //Adding CheckBox to layout
        final CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });
        layout.addView(checkBox);

        //Create Button
        Button buttonDel = new Button(getActivity());
        buttonDel.setText(R.string.delete_button);
        buttonDel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));


        //Create line break
        final View line = new View(getActivity());
        line.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(getResources().getColor(R.color.black));

        //Create listener and add Button to Layout
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout container = (LinearLayout) rootView.findViewById(R.id.mainContainer);
                container.removeView(layout);
                container.removeView(line);
                //delete item from list
                int index = user_list.indexOf(text);
                user_list.remove(index);
            }
        });
        layout.addView(buttonDel);

        //Showing Layout to screen
        LinearLayout container = (LinearLayout) rootView.findViewById(R.id.mainContainer);
        container.addView(layout);

        //add line break after Layout
        container.addView(line);
    }

    ///////////////////GESTURE DETECTION////////////////////
//            @Override
//            public boolean dispatchTouchEvent(MotionEvent ev) {
//
//                boolean handled = super.dispatchTouchEvent(ev);
//                handled = detector.onTouchEvent(ev);
//                return handled;
//            }
//
//            @Override
//            public boolean onTouchEvent(MotionEvent event) {
//                this.detector.onTouchEvent(event);
//                return super.onTouchEvent(event);
//            }

    @Override
    public void onClick(View view) {
        AutoCompleteTextView editTextView = (AutoCompleteTextView) rootView.findViewById(R.id.getTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!Objects.equals(String.valueOf(editTextView.getText()), "")) {
                String text = String.valueOf(editTextView.getText());
                editTextView.setText("");
                createLayout(text);
                user_list.add(text);

                //Change title to first added item
                EditText edit = (EditText) rootView.findViewById(R.id.title_bar);
                if (String.valueOf(edit.getText()).equals("Title")) {
                    edit.setText(text);
                }
            }
        }
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            Log.i("MainActivity", "onDown");

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            final EditText title = (EditText) rootView.findViewById(R.id.title_bar);

            Log.i("MainActivity", "onFling");

            if (title_bar_down && velocityY < -7000) {
                title.animate().translationY(title.getHeight() * (-1)).setDuration(195).start();
                title_bar_down = false;

            } else if (!title_bar_down && velocityY > 7000) {
                title.animate().translationY(title.getHeight() * 5).setDuration(225).start();
                title_bar_down = true;

            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }
}
