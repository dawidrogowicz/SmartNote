package com.rogowiczdawid.smartnote;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public final class FileManager {

    public static ArrayList<NoteFragment> getNotes(Context context) {
        ArrayList<String> note_list = new ArrayList<>();
        ArrayList<NoteFragment> fragments = new ArrayList<>();

        File dir = context.getFilesDir();
        for (String name : dir.list()) {
            if (name.endsWith("Note.bin")) {
                note_list.add(name);
            }
        }

        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;

        for (int i = 0; i < note_list.size(); i++) {
            try {
                fileInputStream = context.openFileInput(note_list.get(i));
                objectInputStream = new ObjectInputStream(fileInputStream);
                fragments.add((NoteFragment) objectInputStream.readObject());
                objectInputStream.close();
                fileInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        return fragments;
    }

    public static ArrayList<ToDoFragment> getToDos(Context context) {
        ArrayList<String> todo_list = new ArrayList<>();
        ArrayList<ToDoFragment> fragments = new ArrayList<>();

        File dir = context.getFilesDir();
        for (String name : dir.list()) {
            if (name.endsWith("ToDo.bin")) {
                todo_list.add(name);
            }
        }

        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;

        for (int i = 0; i < todo_list.size(); i++) {
            try {
                fileInputStream = context.openFileInput(todo_list.get(i));
                objectInputStream = new ObjectInputStream(fileInputStream);
                fragments.add((ToDoFragment) objectInputStream.readObject());
                objectInputStream.close();
                fileInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return fragments;
    }

    public static boolean onSaveNote(NoteFragment fragment, Context context) {
        String title = fragment.getTitleValue();

        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;

        try {
            fileOutputStream = context.openFileOutput(title + "Note", Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(fragment);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean onSaveToDo(ToDoFragment fragment, Context context) {
        String title = fragment.getTitleValue();

        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;

        try {
            fileOutputStream = context.openFileOutput(title + "ToDo", Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(fragment);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
