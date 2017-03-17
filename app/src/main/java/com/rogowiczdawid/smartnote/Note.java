package com.rogowiczdawid.smartnote;

import java.io.Serializable;
import java.util.ArrayList;

class Note implements Serializable {

    private String title;
    private String text;
    private ArrayList<String> list;

    Note(String title_arg, String text_arg) {
        title = title_arg;
        text = text_arg;
    }

    Note(String title_arg, ArrayList<String> list_arg) {
        title = title_arg;
        list = list_arg;
        text = "";

        for (int i = 0; i < list.size(); i++) {
            if (i > 3) break;
            text += list.get(i).concat(" ");
        }
    }


    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    ArrayList<String> getList() {
        return list;
    }
}
