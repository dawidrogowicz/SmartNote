package com.rogowiczdawid.smartnote;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


class ListAdapter extends ArrayAdapter<Note> {

    private final static String NOTE = "NOTE_FRAGMENT";
    private final static String TODO = "TO_DO_FRAGMENT";

    ListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Note> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);

        final Note note = getItem(position);

        if (note != null) {
            TextView title = (TextView) convertView.findViewById(R.id.item_title);
            TextView text = (TextView) convertView.findViewById(R.id.item_text);

            title.setText(note.getTitle());
            text.setText(note.getText());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment;
                    String FRAGMENT_TEMPORARY_TAG;

                    if (note.getUserList() == null) {
                        fragment = NoteFragment.newInstance(note.getTitle(), note.getText());
                        FRAGMENT_TEMPORARY_TAG = NOTE;
                    } else {
                        fragment = ToDoFragment.newInstance(note.getTitle(), note.getUserList(), note.getCheckboxStateList());
                        FRAGMENT_TEMPORARY_TAG = TODO;
                    }


                    FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame, fragment, FRAGMENT_TEMPORARY_TAG);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            });
        }

        return convertView;
    }
}
