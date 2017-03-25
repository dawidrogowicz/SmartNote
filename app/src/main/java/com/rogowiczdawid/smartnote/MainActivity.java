package com.rogowiczdawid.smartnote;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.rogowiczdawid.smartnote.Fragments.GalleryFragment;
import com.rogowiczdawid.smartnote.Fragments.NoteFragment;
import com.rogowiczdawid.smartnote.Fragments.SettingsFragment;
import com.rogowiczdawid.smartnote.Fragments.ToDoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    public final static String TAG = "MyApp_TAG";
    final static String NOTE = "NOTE_FRAGMENT";
    final static String TODO = "TO_DO_FRAGMENT";
    final static String GALLERY = "GALLERY_FRAGMENT";
    final static String SETTINGS = "SETTINGS_FRAGMENT";
    final static int RC_SIGN_IN = 9001;
    static GoogleApiClient mGoogleApiClient;
    List<MyOnTouchListener> listeners;
    private boolean drawer_group_primary = true;

    public static <T extends Fragment> void replaceFragment(T fragment, String tag, FragmentTransaction transaction) {
        transaction.replace(R.id.main_frame, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                }
        );
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utilities.setTheme(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SettingsFragment.write_to_external = preferences.getBoolean("pref_storage_dir", false);

        setContentView(R.layout.activity_main);

        //Navigation, Toolbar etc
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        final View navigationHeader = navigationView.getHeaderView(0);
        navigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
                Account[] accounts = manager.getAccounts();
                for (Account acct : accounts) {
                    navigationView.getMenu().add(acct.name);
                }

                if (drawer_group_primary) {
                    navigationView.getMenu().setGroupVisible(R.id.drawer_primary_group, false);
                    navigationView.getMenu().setGroupVisible(R.id.drawer_secondary_group, true);
                } else {
                    navigationView.getMenu().setGroupVisible(R.id.drawer_primary_group, true);
                    navigationView.getMenu().setGroupVisible(R.id.drawer_secondary_group, false);
                }
                drawer_group_primary = !drawer_group_primary;
            }
        });

        ////////Google SignIn///////
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        //Initialise custom listeners
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        //Add first Fragment to container
        if (findViewById(R.id.content_main) != null) {
            if (savedInstanceState != null) {
                return;
            }
            GalleryFragment firstFragment = new GalleryFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.main_frame, firstFragment).commit();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Click handlers
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {

            //Check if button was pressed in the right fragment
            final ToDoFragment toDoFragment = (ToDoFragment) getFragmentManager().findFragmentByTag(TODO);
            final NoteFragment noteFragment = (NoteFragment) getFragmentManager().findFragmentByTag(NOTE);

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
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
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

            ToDoFragment toDoFragment = (ToDoFragment) getFragmentManager().findFragmentByTag(TODO);
            if (toDoFragment != null && toDoFragment.isVisible()) {
                if (!toDoFragment.getTitleValue().equals("Title")) {
                    if (Utilities.onSaveNote(new Note(toDoFragment.getTitleValue(), toDoFragment.getUserList(), toDoFragment.getCheckbox_state_list()), getApplicationContext()))
                        Toast.makeText(this, R.string.saved_todo, Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(this, R.string.wrong_todo, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else Toast.makeText(this, R.string.set_title, Toast.LENGTH_SHORT).show();

            }

            NoteFragment noteFragment = (NoteFragment) getFragmentManager().findFragmentByTag(NOTE);
            if (noteFragment != null && noteFragment.isVisible()) {
                if (!noteFragment.getTitleValue().equals("Title")) {
                    if (Utilities.onSaveNote(new Note(noteFragment.getTitleValue(), noteFragment.getTextVal()), getApplicationContext()))
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
                replaceFragment(new GalleryFragment(), GALLERY, getFragmentManager().beginTransaction());
                break;
            case R.id.nav_todo:
                replaceFragment(new ToDoFragment(), TODO, getFragmentManager().beginTransaction());
                break;
            case R.id.nav_note:
                replaceFragment(new NoteFragment(), NOTE, getFragmentManager().beginTransaction());
                break;
            case R.id.nav_settings:
                replaceFragment(new SettingsFragment(), SETTINGS, getFragmentManager().beginTransaction());
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_sign_in) signIn();
        if (id == R.id.nav_sign_out) signOut();
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

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //////Google API//////
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Couldn't connect to your account", Toast.LENGTH_SHORT).show();
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        TextView name = (TextView) findViewById(R.id.nav_user_name);
                        name.setText(R.string.username);
                        TextView mail = (TextView) findViewById(R.id.nav_user_mail);
                        mail.setText(R.string.sample_mail);
                        ImageView img = (ImageView) findViewById(R.id.nav_user_img);
                    }
                }
        );
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handle sign in result:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                TextView name = (TextView) findViewById(R.id.nav_user_name);
                name.setText(acct.getDisplayName());
                TextView mail = (TextView) findViewById(R.id.nav_user_mail);
                mail.setText(acct.getEmail());
                ImageView img = (ImageView) findViewById(R.id.nav_user_img);
                img.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    public interface MyOnTouchListener {
        void onTouch(MotionEvent ev);
    }
}

