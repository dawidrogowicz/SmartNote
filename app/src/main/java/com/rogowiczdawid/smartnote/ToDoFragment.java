package com.rogowiczdawid.smartnote;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

    boolean title_bar_down = true;
    View rootView;
    EditText title;
    private MyFragmentListener myFragmentListener;
    private ArrayList<String> user_list;
    private ArrayList<Boolean> checkbox_state_list;
    private String title_val = "Title";

    public static ToDoFragment newInstance(String title, ArrayList<String> list, ArrayList<Boolean> checkbox_list) {

        boolean[] checkbox_array = new boolean[checkbox_list.size()];
        for (int i = 0; i < checkbox_list.size(); i++) {
            checkbox_array[i] = checkbox_list.get(i);
        }

        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putStringArrayList("LIST", list);
        args.putBooleanArray("CHECKBOX_LIST", checkbox_array);

        ToDoFragment fragment = new ToDoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.to_do_list_fragment, container, false);
        title = (EditText) rootView.findViewById(R.id.title_bar);
        user_list = new ArrayList<>();
        checkbox_state_list = new ArrayList<>();

        //restoring savedInstanceState
        if (savedInstanceState != null) {
            ArrayList<String> temp_user_list = savedInstanceState.getStringArrayList("user_list");
            boolean[] temp_checkbox_state_array = savedInstanceState.getBooleanArray("checkbox_state_array");

            if (temp_user_list != null && temp_checkbox_state_array != null) {
                int count = temp_user_list.size();
                for (int i = 0; i < count; i++) {
                    createLayout(temp_user_list.get(i), temp_checkbox_state_array[i]);
                }
            }
        }

        if (getArguments() != null) {
            if (user_list.size() < 1) {
                title_val = getArguments().getString("TITLE");
                title.setText(title_val);
                boolean[] temp_checkbox_state_array = getArguments().getBooleanArray("CHECKBOX_LIST");
                ArrayList<String> temp_user_list = getArguments().getStringArrayList("LIST");

                if (temp_user_list != null && temp_checkbox_state_array != null) {
                    if (temp_user_list.size() > 0) {
                        for (int i = 0; i < temp_user_list.size(); i++) {
                            createLayout(temp_user_list.get(i), temp_checkbox_state_array[i]);
                        }
                    }
                }
            }
        }

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

        //Setting up custom listeners
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

        //Listener for "add" button
        Button add_button = (Button) rootView.findViewById(R.id.add_button);
        add_button.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean[] checkbox_state_array = new boolean[checkbox_state_list.size()];
        for (int i = 0; i < checkbox_state_list.size(); i++) {
            checkbox_state_array[i] = checkbox_state_list.get(i);
        }
        outState.putStringArrayList("user_list", user_list);
        outState.putBooleanArray("checkbox_state_array", checkbox_state_array);
        outState.putString("title", title_val);
    }

    ////////////////ADDING ITEMS TO LIST///////////////////
    public void createLayout(final String text, final boolean checkbox_state) {
        user_list.add(text);
        checkbox_state_list.add(checkbox_state);

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
        if (checkbox_state) {
            checkBox.setChecked(true);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            checkBox.setChecked(false);
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    checkbox_state_list.set(user_list.indexOf(text), true);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    checkbox_state_list.set(user_list.indexOf(text), false);
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
                int i = user_list.indexOf(text);
                user_list.remove(i);
                checkbox_state_list.remove(i);
            }
        });
        layout.addView(buttonDel);

        //Showing Layout to screen
        LinearLayout container = (LinearLayout) rootView.findViewById(R.id.mainContainer);
        container.addView(layout);

        //add line break after Layout
        container.addView(line);
    }

    @Override
    public void onClick(View view) {
        AutoCompleteTextView editTextView = (AutoCompleteTextView) rootView.findViewById(R.id.getTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!Objects.equals(String.valueOf(editTextView.getText()), "")) {
                String text = String.valueOf(editTextView.getText());
                editTextView.setText("");
                createLayout(text, false);

                //Change title to first added item
                if (String.valueOf(title.getText()).equals("Title")) {
                    title.setText(text);
                    title_val = text;
                }
            }
        }
    }

    public String getTitleValue() {
        return title_val;
    }

    public ArrayList<String> getUserList() {
        return user_list;
    }

    public ArrayList<Boolean> getCheckbox_state_list() {
        return checkbox_state_list;
    }
}