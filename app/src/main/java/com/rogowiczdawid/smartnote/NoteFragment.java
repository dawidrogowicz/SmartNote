package com.rogowiczdawid.smartnote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

public class NoteFragment extends Fragment {
    View rootView;

    //////////Variables with data for saving///////////
    //1 corresponds to "To do note" 0 to "Note"
    private static final int FRAGMENT_TYPE = 0;
    private String title_val = "Title";
    String text_val;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.note_fragment, container, false);

        final EditText title = (EditText) rootView.findViewById(R.id.note_title_bar);

        final MultiAutoCompleteTextView editText = (MultiAutoCompleteTextView) rootView.findViewById(R.id.note_text);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText title = (EditText) rootView.findViewById(R.id.note_title_bar);
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
                    title_val = String.valueOf(title.getText());
                }
            }
        });
    }


    public int getFragmentType() {
        return FRAGMENT_TYPE;
    }

    public String getTitleValue() {
        return title_val;
    }

    public String getTextValue() {
        return text_val;
    }
}
