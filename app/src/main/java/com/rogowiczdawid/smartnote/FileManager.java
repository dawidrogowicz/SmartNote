package com.rogowiczdawid.smartnote;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public final class FileManager {

    public static ArrayList<Note> getNotes(Context context) {
        ArrayList<String> note_list = new ArrayList<>();
        ArrayList<Note> notes = new ArrayList<>();

        //Search for files that ends with "Note"
        File dir = context.getFilesDir();
        for (String name : dir.list()) {
            if (name.endsWith("Note")) {
                note_list.add(name);
            }
        }

        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;

        //Add objects to the ArrayList
        for (int i = 0; i < note_list.size(); i++) {
            try {
                fileInputStream = context.openFileInput(note_list.get(i));
                objectInputStream = new ObjectInputStream(fileInputStream);
                Note nt = (Note) objectInputStream.readObject();
                notes.add(nt);
                objectInputStream.close();
                fileInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        return notes;
    }

    public static boolean onSaveNote(Note note, Context context) {
        String title = note.getTitle();

        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;

        //Save as object and end filename with "Note"
        try {
            fileOutputStream = context.openFileOutput(title + "Note", Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(note);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("ERROR", e.toString());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR", e.toString());
            return false;
        }

        return true;
    }

}
