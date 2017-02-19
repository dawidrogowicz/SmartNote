package com.rogowiczdawid.smartnote;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    public boolean title_bar_down = true;
    GestureDetectorCompat detector;
    ArrayList<String> user_list;

    @Override
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

        detector = new GestureDetectorCompat(this, new MyGestureDetector());

        //restoring savedInstanceState
        user_list = new ArrayList<>();
        if (savedInstanceState != null) {
            user_list = savedInstanceState.getStringArrayList("user_list");
            if (user_list != null) {
                int count = user_list.size();
                for (int i = 0; i < count; i++) {
                    createLayout(user_list.get(i));
                }
            }
        }

        //Allow title to be changed after long click
        final EditText title = (EditText) findViewById(R.id.title_bar);
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                title.setFocusable(true);
                title.requestFocusFromTouch();
                InputMethodManager inp = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inp.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);
                return true;
            }
        });
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    title.setFocusable(false);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("user_list", user_list);

        EditText title = (EditText) findViewById(R.id.title_bar);
        outState.putString("title", String.valueOf(title.getText()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ////////////////ADDING ITEMS TO LIST///////////////////
    public void createLayout(final String text) {

        //Adding new horizontal Layout with RelativeLayout inside
        final LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new DrawerLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setWeightSum(8);

        //Adding TextView with scrollView to new Layout
        final TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(20);
        textView.setText(text);

        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 6);
        params.gravity = Gravity.CENTER_VERTICAL;
        scrollView.setLayoutParams(params);
        scrollView.addView(textView);

        layout.addView(scrollView);

        //Adding CheckBox to layout
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });
        layout.addView(checkBox);

        //Create Button
        Button buttonDel = new Button(this);
        buttonDel.setText(R.string.delete_button);
        buttonDel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));


        //Create line break
        final View line = new View(this);
        line.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(getResources().getColor(R.color.black));

        //Create listener and add Button to Layout
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout container = (LinearLayout) findViewById(R.id.mainContainer);
                container.removeView(layout);
                container.removeView(line);
                //delete item from list
                int index = user_list.indexOf(text);
                user_list.remove(index);
            }
        });
        layout.addView(buttonDel);

        //Showing Layout to screen
        LinearLayout container = (LinearLayout) findViewById(R.id.mainContainer);
        container.addView(layout);

        //add line break after Layout
        container.addView(line);
    }

    public void onAdd(View view) {
        AutoCompleteTextView editTextView = (AutoCompleteTextView) findViewById(R.id.getTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!Objects.equals(String.valueOf(editTextView.getText()), "")) {
                String text = String.valueOf(editTextView.getText());
                editTextView.setText("");
                createLayout(text);
                user_list.add(text);
                //Change title to first added item

                EditText edit = (EditText) findViewById(R.id.title_bar);
                if (String.valueOf(edit.getText()).equals("Title")) {
                    edit.setText(text);
                }
            }
        }
    }


    ///////////////////GESTURE DETECTION////////////////////
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean handled = super.dispatchTouchEvent(ev);
        handled = detector.onTouchEvent(ev);
        return handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            final EditText title = (EditText) findViewById(R.id.title_bar);

            if (title_bar_down && velocityY<-7000) {
                title.animate().translationY(title.getHeight()*(-1)).setDuration(195).start();
                title_bar_down = false;

            } else if(!title_bar_down && velocityY>7000){
                title.animate().translationY(0).setDuration(225).start();
                title_bar_down = true;

            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }



    }
}