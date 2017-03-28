package com.rogowiczdawid.smartnote;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.rogowiczdawid.smartnote.Fragments.GalleryFragment;
import com.rogowiczdawid.smartnote.Fragments.NoteFragment;
import com.rogowiczdawid.smartnote.Fragments.SettingsFragment;
import com.rogowiczdawid.smartnote.Fragments.ToDoFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public final static String TAG = "MyApp_TAG";
    final static String NOTE = "NOTE_FRAGMENT";
    final static String TODO = "TO_DO_FRAGMENT";
    final static String GALLERY = "GALLERY_FRAGMENT";
    final static String SETTINGS = "SETTINGS_FRAGMENT";
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;
    private static final int RC_SIGN_IN = 9001;
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

    /////////Application life cycle///////////
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

        ////////Google API////////
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
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

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    ///////////////////Click handlers////////////////
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_delete: {

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

            }
            case R.id.action_save: {
                NoteFragment noteFragment = (NoteFragment) getFragmentManager().findFragmentByTag(NOTE);
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

                } else if (noteFragment != null && noteFragment.isVisible()) {
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
            case R.id.action_notification: {
                //Get dateTime with dialogs and it will trigger next actions
                getDateTime();
                break;
            }
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
            case R.id.nav_send:
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822")
                        .putExtra(Intent.EXTRA_EMAIL, new String[]{"rogowiczdawid.develop@gmail.com"});

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Email type")
                        .setMessage("Do you wish to send a bug report or just a regular message?")
                        .setNegativeButton("Bug Report", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SimpleNote/BugReport")
                                        .putExtra(Intent.EXTRA_TEXT,
                                                "Describe your problem:\n\n" +
                                                        "Your Android version:\n\n" +
                                                        "If you have any additional information type it here:\n");
                                startActivity(emailIntent);
                            }
                        })
                        .setPositiveButton("Just email", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SimpleNote/UserMail");
                                startActivity(emailIntent);
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
                break;
            case R.id.nav_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain")
                        .putExtra(Intent.EXTRA_SUBJECT, "Share app with friends!")
                        .putExtra(Intent.EXTRA_TEXT, "Hey, I'm using this note-taking app:\n" +
                                "https://github.com/drsgi8/SmartNote/\n" +
                                "Check it out you, may like it!");
                startActivity(Intent.createChooser(sharingIntent, "Share via:"));

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

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /////////////Other//////////
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    private void getDateTime() {

        //Determine which fragment is currently in use
        ToDoFragment toDoFragment = (ToDoFragment) getFragmentManager().findFragmentByTag(TODO);
        NoteFragment noteFragment = (NoteFragment) getFragmentManager().findFragmentByTag(NOTE);
        String text = "";
        final String title;
        int i = 0;
        final int notificationId;

        if (toDoFragment != null && toDoFragment.isVisible()) {
            for (String s : toDoFragment.getUserList()) {
                if (i > 3) break;
                text += s.concat(" ");
                i++;
            }
            title = toDoFragment.getTitleValue();
            notificationId = 666;
        } else if (noteFragment != null && noteFragment.isVisible()) {
            text = noteFragment.getTextVal();
            if (text.length() > 15) {
                text = text.substring(0, 15);
            }
            title = noteFragment.getTitleValue();
            notificationId = 999;
        } else return;
        final String finalText = text;

        //Get date and time from user
        final int[] dateTime = new int[5];
        Calendar c = Calendar.getInstance();
        dateTime[0] = c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.MONTH);
        dateTime[2] = c.get(Calendar.DAY_OF_MONTH);
        dateTime[3] = 16;

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                dateTime[3] = i;
                dateTime[4] = i1;
                createNotification(dateTime, title, finalText, notificationId);
            }
        }, dateTime[3], dateTime[4], true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateTime[0] = i;
                dateTime[1] = i1;
                dateTime[2] = i2;
                timePickerDialog.show();
            }
        }, dateTime[0], dateTime[1], dateTime[2]);

        datePickerDialog.show();
    }

    private void createNotification(int[] arr, String title, String text, int notificationId) {

        //Setting calendar to date and time user have chosen in dialog
        Calendar calendar = Calendar.getInstance();
        calendar.set(arr[0], arr[1], arr[2], arr[3], arr[4]);

        //Create broadcast
        Intent resultIntent = new Intent(this, Receiver.class);
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("text", text);
        resultIntent.putExtra("id", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set alarm that will send broadcast
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(SettingsFragment.alarm_type, calendar.getTimeInMillis(), pendingIntent);
    }

    //////////////Google API//////////////
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Couldn't connect to your account", Toast.LENGTH_SHORT).show();

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(MainActivity.this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to resolve, message user appropriately");
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), MainActivity.this, 0).show();
        }
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
        } else if (requestCode == RESOLVE_CONNECTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public interface MyOnTouchListener {
        void onTouch(MotionEvent ev);
    }
}
