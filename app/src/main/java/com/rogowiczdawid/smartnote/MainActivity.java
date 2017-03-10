package com.rogowiczdawid.smartnote;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String NOTE = "NOTE_FRAGMENT";
    final static String TODO = "TO_DO_FRAGMENT";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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


            return true;
        } else if (id == R.id.action_save) {

            ToDoFragment toDoFragment = (ToDoFragment) getSupportFragmentManager().findFragmentByTag(TODO);
            if (toDoFragment != null && toDoFragment.isVisible()) {
                if (FileManager.onSaveNote(new Note(toDoFragment.getTitleValue(), toDoFragment.getUserList()), this)) {
                    Toast toast = Toast.makeText(this, "File saved :3 - ToDo", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(this, "Something went wrong ;c - ToDo", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
            }

            NoteFragment noteFragment = (NoteFragment) getSupportFragmentManager().findFragmentByTag(NOTE);
            if (noteFragment != null && noteFragment.isVisible()) {
                if (FileManager.onSaveNote(new Note(noteFragment.getTitleValue(), noteFragment.getTextVal()), this)) {
                    Toast toast = Toast.makeText(this, "File saved :3 - Note", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(this, "Something went wrong ;c - Note", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_note) {
            NoteFragment noteFragment = new NoteFragment();
            noteFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame, noteFragment, NOTE);
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (id == R.id.nav_gallery) {
            GalleryFragment galleryFragment = new GalleryFragment();
            galleryFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame, galleryFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (id == R.id.nav_todo) {
            ToDoFragment toDoFragment = new ToDoFragment();
            toDoFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame, toDoFragment, TODO);
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
