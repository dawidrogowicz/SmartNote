package com.rogowiczdawid.smartnote;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Note implements Serializable {

    private String title;
    private String text;
    private String date;
    private ArrayList<String> user_list;
    private ArrayList<Boolean> checkbox_state_list;

    Note(String title_arg, String text_arg) {
        title = title_arg;
        text = text_arg;

        DateFormat dateFormat = DateFormat.getDateInstance();
        date = dateFormat.format(new Date());
    }

    Note(String title_arg, ArrayList<String> user_list_arg, ArrayList<Boolean> checkbox_state_list_arg) {
        title = title_arg;
        user_list = user_list_arg;
        checkbox_state_list = checkbox_state_list_arg;
        text = "";

        DateFormat dateFormat = DateFormat.getDateInstance();
        date = dateFormat.format(new Date());

        for (int i = 0; i < user_list.size(); i++) {
            if (i > 3) break;
            text += user_list.get(i).concat(" ");
        }
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    String getDate() {
        return date;
    }

    ArrayList<String> getUserList() {
        return user_list;
    }

    ArrayList<Boolean> getCheckboxStateList() {
        return checkbox_state_list;
    }
}
