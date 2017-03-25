package com.rogowiczdawid.smartnote;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.rogowiczdawid.smartnote.Fragments.SettingsFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public final class Utilities {

    private static final String FILE_CONST = "Note";

    public static ArrayList<Note> getNotes(Context context) {
        ArrayList<String> note_list = new ArrayList<>();
        ArrayList<Note> notes = new ArrayList<>();
        File dir = null;

        //Search for files that ends with "Note"-(file_const)
        if (SettingsFragment.write_to_external)
            if (isExternalStorageReadable()) dir = context.getExternalFilesDir(null);
            else
                Toast.makeText(context, "Unable to access external storage", Toast.LENGTH_SHORT).show();
        else dir = context.getFilesDir();

        if (dir != null) {
            for (String name : dir.list()) {
                if (name.endsWith(FILE_CONST)) {
                    note_list.add(name);
                }
            }

            FileInputStream fileInputStream;
            ObjectInputStream objectInputStream;

            //Add objects to the ArrayList
            for (int i = 0; i < note_list.size(); i++) {
                try {
                    fileInputStream = new FileInputStream(new File(dir, note_list.get(i)));
                    objectInputStream = new ObjectInputStream(fileInputStream);
                    Note nt = (Note) objectInputStream.readObject();
                    notes.add(nt);
                    objectInputStream.close();
                    fileInputStream.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }

        return notes;
    }

    static boolean moveNotes(boolean external) {

        return true;
    }

    public static boolean onSaveNote(Note note, Context context) {
        //Add constant string to file name
        String title = note.getTitle() + FILE_CONST;

        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;
        File file = null;

        //Set directory in external or internal storage
        if (SettingsFragment.write_to_external)
            if (isExternalStorageWritable())
                file = new File(context.getExternalFilesDir(null), title);
            else
                Toast.makeText(context, "Unable to access external storage", Toast.LENGTH_SHORT).show();
        else file = new File(context.getFilesDir(), title);

        if (file != null) {
            //Save as object and end filename with "Note"
            try {
                fileOutputStream = new FileOutputStream(file);
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
        }
        return true;
    }

    public static boolean onDeleteNote(String filename, Context context) {
        File dir = null;

        if (SettingsFragment.write_to_external)
            if (isExternalStorageWritable()) dir = context.getExternalFilesDir(null);
            else
                Toast.makeText(context, "Unable to access external storage", Toast.LENGTH_SHORT).show();

        else dir = context.getFilesDir();

        File f = new File(dir, filename + FILE_CONST);

        return f.delete();
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    static void setTheme(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String theme = preferences.getString(SettingsFragment.THEME_KEY, "");

        switch (theme) {
            case "AppTheme_NoActionBar":
                activity.setTheme(R.style.AppTheme_NoActionBar);
                break;
            case "PurpleTheme_NoActionBar":
                activity.setTheme(R.style.PurpleTheme_NoActionBar);
                break;
            case "BlueTheme_NoActionBar":
                activity.setTheme(R.style.BlueTheme_NoActionBar);
                break;
            case "LimeTheme_NoActionBar":
                activity.setTheme(R.style.LimeTheme_NoActionBar);
                break;
        }
    }
}
