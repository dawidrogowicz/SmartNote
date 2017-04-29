package com.rogowiczdawid.smartnote;

import java.io.Serializable;
import java.util.ArrayList;

public class Note implements Serializable {

    private String title;
    private String text;
    private long creationDateTime = 0;
    private long editiondateTime;
    private ArrayList<String> user_list;
    private ArrayList<Boolean> checkbox_state_list;

    Note(String title_arg, String text_arg) {
        title = title_arg;
        text = text_arg;

        if (creationDateTime == 0) creationDateTime = System.currentTimeMillis();
        editiondateTime = System.currentTimeMillis();
    }

    Note(String title_arg, ArrayList<String> user_list_arg, ArrayList<Boolean> checkbox_state_list_arg) {
        title = title_arg;
        user_list = user_list_arg;
        checkbox_state_list = checkbox_state_list_arg;
        text = "";

        if (creationDateTime == 0) creationDateTime = System.currentTimeMillis();
        editiondateTime = System.currentTimeMillis();

        int items_added = 0;

        for (int i = 0; i < user_list.size(); i++) {
            if (items_added > 3)
                break;
            if (checkbox_state_list_arg.get(i))
                continue;
            text += "-" + user_list.get(i);
            if (i < user_list.size() - 1)
                if (!checkbox_state_list_arg.get(i + 1))
                    text += "\n";
            items_added++;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public long getEditiondateTime() {
        return editiondateTime;
    }

    ArrayList<String> getUserList() {
        return user_list;
    }

    ArrayList<Boolean> getCheckboxStateList() {
        return checkbox_state_list;
    }
}
