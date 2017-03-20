package com.rogowiczdawid.smartnote;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String NOTE = "NOTE_FRAGMENT";
    final static String TODO = "TO_DO_FRAGMENT";
    final static String GALLERY = "GALLERY_FRAGMENT";
    final static String SETTINGS = "SETTINGS_FRAGMENT";
    List<MyOnTouchListener> listeners;

    public static <T extends Fragment> void replaceFragment(T fragment, String tag, FragmentTransaction transaction) {
        transaction.replace(R.id.main_frame, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.setTheme(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        //ADDING FIRST FRAGMENT TO CONTAINER
        if (findViewById(R.id.content_main) != null) {
            if (savedInstanceState != null) {
                return;
            }
            GalleryFragment firstFragment = new GalleryFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.main_frame, firstFragment).commit();

        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {

            //Check if button was pressed in the right fragment
            final ToDoFragment toDoFragment = (ToDoFragment) getSupportFragmentManager().findFragmentByTag(TODO);
            final NoteFragment noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentByTag(NOTE);

            if ((toDoFragment != null && toDoFragment.isVisible()) || (noteFragment != null && noteFragment.isVisible())) {

                //Create AlertDialog so the user won't accidentally delete file
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.delete_note);
                builder.setMessage(R.string.you_wont_be_able);
                builder.setNegativeButton(R.string.no, null);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Prepare transaction to gallery fragment after deleting file
                        GalleryFragment galleryFragment = new GalleryFragment();
                        galleryFragment.setArguments(getIntent().getExtras());
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_frame, galleryFragment);
                        transaction.addToBackStack(null);

                        if (toDoFragment != null && toDoFragment.isVisible()) {

                            if (Utilities.onDeleteNote(toDoFragment.getTitleValue(), getApplicationContext())) {
                                transaction.commit();
                                Toast.makeText(getApplicationContext(), getString(R.string.file_deleted), Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), getString(R.string.couldnt_delete), Toast.LENGTH_SHORT).show();
                        } else if (noteFragment != null && noteFragment.isVisible()) {

                            if (Utilities.onDeleteNote(noteFragment.getTitleValue(), getApplicationContext())) {
                                transaction.commit();
                                Toast.makeText(getApplicationContext(), getString(R.string.file_deleted), Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), getString(R.string.couldnt_delete), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.create().show();
            }

            return true;

        } else if (id == R.id.action_save) {

            ToDoFragment toDoFragment = (ToDoFragment) getSupportFragmentManager().findFragmentByTag(TODO);
            if (toDoFragment != null && toDoFragment.isVisible()) {
                if (!toDoFragment.getTitleValue().equals("Title")) {
                    if (Utilities.onSaveNote(new Note(toDoFragment.getTitleValue(), toDoFragment.getUserList(), toDoFragment.getCheckbox_state_list()), this))
                        Toast.makeText(this, R.string.saved_todo, Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(this, R.string.wrong_todo, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else Toast.makeText(this, R.string.set_title, Toast.LENGTH_SHORT).show();

            }

            NoteFragment noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentByTag(NOTE);
            if (noteFragment != null && noteFragment.isVisible()) {
                if (!noteFragment.getTitleValue().equals("Title")) {
                    if (Utilities.onSaveNote(new Note(noteFragment.getTitleValue(), noteFragment.getTextVal()), this))
                        Toast.makeText(this, R.string.saved_note, Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(this, R.string.wrong_note, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else Toast.makeText(this, R.string.set_title, Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_gallery:
                replaceFragment(new GalleryFragment(), GALLERY, getSupportFragmentManager().beginTransaction());
                break;
            case R.id.nav_todo:
                replaceFragment(new ToDoFragment(), TODO, getSupportFragmentManager().beginTransaction());
                break;
            case R.id.nav_note:
                replaceFragment(new NoteFragment(), NOTE, getSupportFragmentManager().beginTransaction());
                break;
            case R.id.nav_settings:
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new SettingsFragment(), SETTINGS).addToBackStack(null).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : listeners) {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void addMyOnTouchListener(MyOnTouchListener listener) {
        listeners.add(listener);
    }


    interface MyOnTouchListener {
        void onTouch(MotionEvent ev);
    }
}

