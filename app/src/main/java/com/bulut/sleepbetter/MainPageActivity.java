package com.bulut.sleepbetter;


import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.bulut.sleepbetter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.bulut.sleepbetter.database.YouTubeSqlDb;
import com.bulut.sleepbetter.fragments.FavoritesFragment;
import com.bulut.sleepbetter.fragments.PlaylistsFragment;
import com.bulut.sleepbetter.fragments.RecentlyWatchedFragment;
import com.bulut.sleepbetter.fragments.SearchFragment;
import com.bulut.sleepbetter.interfaces.OnFavoritesSelected;
import com.bulut.sleepbetter.interfaces.OnItemSelected;
import com.bulut.sleepbetter.model.ItemType;
import com.bulut.sleepbetter.model.YouTubeVideo;
import com.bulut.sleepbetter.utils.Config;
import com.bulut.sleepbetter.utils.NetworkConf;
import com.bulut.sleepbetter.youtube.SuggestionsLoader;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.bulut.sleepbetter.R.layout.activity_sleep;
import static com.bulut.sleepbetter.youtube.YouTubeSingleton.getCredential;

public class MainPageActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
{

    protected EditText passwordEditText;
    protected EditText emailEditText;
    protected Button sleepbetterButton;
    protected Button questionnaireButton;
    protected Button exportResultsButton;
    protected Button logoutButton;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    private static final int PERMISSIONS = 1;
    private static final String PREF_BACKGROUND_COLOR = "BACKGROUND_COLOR";
    private static final String PREF_TEXT_COLOR = "TEXT_COLOR";
    public static final String PREF_ACCOUNT_NAME = "accountName";

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private int initialColor = 0xffff0040;
    private int initialColors[] = new int[2];

    private SearchFragment searchFragment;
    private RecentlyWatchedFragment recentlyPlayedFragment;
    private FavoritesFragment favoritesFragment;

    private NetworkConf networkConf;


    private final String TAG = "SLEEP BETTER MAIN";
    TextView ques_1, ques_2, ques_3, ques_4, ques_5, ques_6;
    EditText answerOneEditText;
    RadioGroup answerTwoRadioGroup;

    EditText answerFourEditText;
    RadioGroup answerFiveRadioGroup;


    String[] question = {
            "How many hours do you usually sleep during a weekday night prior to work?",
            "When do you confront sleep problems; before sleeping, while sleeping, while waking up, or all of them? ?",
            "Do you take any sedatives, sleeping medications or alcohol to help you sleep?",
            "On a scale of 1-10, how would you describe your sleep quality? (1: very bad, 10:very good)",
            "If x= 20 , y = 10 , then Value of x + 20y is"};


    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(PERMISSIONS)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
                String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
                if (accountName != null) {
                    getCredential().setSelectedAccountName(accountName);
                } else {
                    // Start a dialog from which the user can choose an account
                    startActivityForResult(
                            getCredential().newChooseAccountIntent(),
                            REQUEST_ACCOUNT_PICKER);
                }
            } else {
                // Request the GET_ACCOUNTS permission via a user dialog
                EasyPermissions.requestPermissions(
                        this,
                        "This app needs to access your Google account (via Contacts).",
                        REQUEST_PERMISSION_GET_ACCOUNTS,
                        Manifest.permission.GET_ACCOUNTS);
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.all_permissions_request),
                    PERMISSIONS, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied: ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCOUNT_PICKER) {
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(PREF_ACCOUNT_NAME, accountName);
                    editor.apply();
                    getCredential().setSelectedAccountName(accountName);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void runSleepBetter(String music, Long length){

        runMusic(music, length);


    }

    public void runMusic(String music, Long length) {
        if (!networkConf.isNetworkAvailable()) {
            networkConf.createNetErrorDialog();
            return;
        }

        List<YouTubeVideo> playlist = new ArrayList<YouTubeVideo>();
        String videoURL = "";
        if (music.equals("ROCK"))
            videoURL = getString(R.string.rock_list);
        if (music.equals("POP"))
            videoURL = getString(R.string.pop_list);
        if (music.equals("METAL"))
            videoURL = getString(R.string.metal_list);
        if (music.equals("CLASSICAL"))
            videoURL = getString(R.string.classical_list);
        if (music.equals("PIANO"))
            videoURL = getString(R.string.piano_list);
        if (music.equals("ELECTRONICAL"))
            videoURL = getString(R.string.electronical_list);

        YouTubeVideo videoToPlay = new YouTubeVideo(videoURL,
                                                    "SLEEP BETTER: " + music,
                                                    videoURL,
                "SLEEP BETTER: " + music,
                "SLEEP BETTER: " + music);

        Log.d(TAG, "About to play url " + videoURL);
        playlist.add(videoToPlay);
        Intent serviceIntent = new Intent(this, BackgroundAudioService.class);
        serviceIntent.setAction(BackgroundAudioService.ACTION_PLAY);
        serviceIntent.putExtra(Config.YOUTUBE_TYPE, ItemType.YOUTUBE_MEDIA_TYPE_VIDEO);
        serviceIntent.putExtra(Config.YOUTUBE_MEDIA_TYPE_VIDEO, videoToPlay);
        //serviceIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST_VIDEO_POS, 0);

        /*
        serviceIntent.putExtra(Config.YOUTUBE_TYPE, ItemType.YOUTUBE_MEDIA_TYPE_PLAYLIST);
        serviceIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST, (ArrayList) playlist);
        serviceIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST_VIDEO_POS, 0);
        */
        Log.d(TAG, "Starting play list now");
        startService(serviceIntent);

        try
        {
            Thread.sleep(length*60*1000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        Log.d(TAG, "Pausing play list now");

        Intent stopIntent = new Intent(this, BackgroundAudioService.class);
        stopIntent.setAction(BackgroundAudioService.ACTION_STOP);
        stopIntent.putExtra(Config.YOUTUBE_TYPE, ItemType.YOUTUBE_MEDIA_TYPE_PLAYLIST);
        stopIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST, (ArrayList) playlist);
        stopIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST_VIDEO_POS, 0);
        startService(stopIntent);
        Log.d(TAG, "Should be paused by now");


        Log.d(TAG, "Starting sleep tracking now");
        //startSleepTracking();
        //startRecordGather();
        startSleepTracking();
    }

    private void startSleepTracking(){
        Intent intent = new Intent();
        intent.setAction("com.urbandroid.sleep.alarmclock.START_SLEEP_TRACK");
        intent.putExtra("START_AIRPLANE", true);
        intent.setClassName("com.urbandroid.sleep", "com.urbandroid.sleep.alarmclock.StartTrackReceiver");
        intent.setPackage("com.urbandroid.sleep");
        sendBroadcast(intent);

    }

    private void startRecordGather(){
        Intent intent = new Intent();
        intent.setAction("com.urbandroid.sleep.REQUEST_SYNC");
        intent.putExtra("START_AIRPLANE", true);
        //intent.setClassName("com.urbandroid.sleep", "com.urbandroid.sleep.alarmclock.StartTrackReceiver");
        intent.setPackage("com.urbandroid.sleep");
        sendBroadcast(intent);

    }

    private void loadquest() {
        mUserId = mFirebaseUser.getUid();

        // Set up ListView
        setContentView(R.layout.activity_main_questionnaire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        answerOneEditText = (EditText) findViewById(R.id.text_answer_1);
        answerTwoRadioGroup = (RadioGroup) findViewById(R.id.radio_group_answer_2);
        answerFourEditText = (EditText) findViewById(R.id.text_answer_4);
        answerFiveRadioGroup = (RadioGroup) findViewById(R.id.radio_group_answer_5);

        ques_1 = (TextView) findViewById(R.id.text_question_1);
        ques_2 = (TextView) findViewById(R.id.radio_question_2);
        ques_3 = (TextView) findViewById(R.id.checkbox_question_3);
        ques_4 = (TextView) findViewById(R.id.text_question_4);
        ques_5 = (TextView) findViewById(R.id.radio_question_5);
        ques_6 = (TextView) findViewById(R.id.text_question_6);

        // Add items via the Button and EditText at the bottom of the view.
        //final EditText text = (EditText) findViewById(R.id.todoText);
        final Button button = (Button) findViewById(R.id.addButtonQuest);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                QuestionnaireItem item = new QuestionnaireItem(
                        mUserId,
                        mUserId,
                        answerOneEditText.getText().toString(),
                        answerTwoRadioGroup.toString(),
                        ques_3.toString(),
                        answerFourEditText.getText().toString(),
                        answerFiveRadioGroup.toString(),
                        ques_6.toString()
                );
                /*
                QuestionnaireItem item = new QuestionnaireItem(
                        mUserId,
                        mUserId,
                        answerOneEditText.getText().toString());
                        */
                mDatabase.child("users").child(mUserId).child("items").push().setValue(item);
                reloadpage(false);
                //setContentView(R.layout.activity_main_page_no_ques);
                //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                //setSupportActionBar(toolbar);

            }
        });


    }

    private void loadsleeptime(){

        Spinner spinner1, spinner2;
        Button btnSubmit;

        setContentView(R.layout.activity_sleep);

        spinner1 = (Spinner) findViewById(R.id.spinner_music);
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener (){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        spinner2 = (Spinner) findViewById(R.id.spinner_minute);
        btnSubmit = (Button) findViewById(R.id.playmusicButton);
        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Spinner spinner2_in = (Spinner) findViewById(R.id.spinner_minute);
                Spinner spinner1_in = (Spinner) findViewById(R.id.spinner_music);
                String music = String.valueOf(spinner1_in.getSelectedItem());
                String str_length = String.valueOf(spinner2_in.getSelectedItem());
                Long length = Long.parseLong(str_length);
                Log.d(TAG, "Following items selected " +
                         String.valueOf(spinner1_in.getSelectedItem()) + " " +
                        String.valueOf(spinner2_in.getSelectedItem())
                       );
                runSleepBetter(music, length);
            }

        });

        }

    private void reloadpage(boolean addQuest){
        if (addQuest)
            setContentView(R.layout.activity_main_page);
        else
            setContentView(R.layout.activity_main_page_no_ques);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {
            if (addQuest){
                questionnaireButton = (Button) findViewById(R.id.questionnaireButton);
                questionnaireButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Loading questionnaire");
                        loadquest();
                    }
                });

            }

            sleepbetterButton = (Button) findViewById(R.id.sleepbetterButton);
            exportResultsButton = (Button) findViewById(R.id.exportResultsButton);
            logoutButton = (Button) findViewById(R.id.logoutButton);

            sleepbetterButton = (Button) findViewById(R.id.sleepbetterButton);
            sleepbetterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Loading sleep better");
                    loadsleepbetter();
                    //loadsleeptime();
                    //runSleepBetter();

                }
            });


            exportResultsButton = (Button) findViewById(R.id.exportResultsButton);
            exportResultsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Loading export results");
                    loadexportresults();


                }
            });

            logoutButton = (Button) findViewById(R.id.logoutButton);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Logging out");
                    logout();
                }
            });

        }

    }

    private void loadsleepbetter(){
            //setContentView(R.layout.activity_main_page);
            //reloadpage(false);
        loadsleeptime();

    }

    private void loadexportresults(){

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {
            reloadpage(false);


        }
    }

    private void logout(){

        mFirebaseAuth.signOut();
        loadLogInView();
        reloadpage(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        networkConf = new NetworkConf(this);
        requestPermissions();

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {
            questionnaireButton = (Button) findViewById(R.id.questionnaireButton);
            sleepbetterButton = (Button) findViewById(R.id.sleepbetterButton);
            exportResultsButton = (Button) findViewById(R.id.exportResultsButton);
            logoutButton = (Button) findViewById(R.id.logoutButton);

            questionnaireButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Intent intent = new Intent(MainPageActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    */
                    Log.d(TAG, "Loading questionnaire");
                    loadquest();
                }
            });

            sleepbetterButton = (Button) findViewById(R.id.sleepbetterButton);
            sleepbetterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Loading sleep better");
                    loadsleepbetter();
                    //runSleepBetter();

                }
            });


            exportResultsButton = (Button) findViewById(R.id.exportResultsButton);
            exportResultsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Loading export results");
                    loadexportresults();

                }
            });

            logoutButton = (Button) findViewById(R.id.logoutButton);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Logging out");
                    logout();
                }
            });

            //loadOld();
            //loadQuestionnaire();
        }

    }


    private void loadLogInView() {
        //Intent intent = new Intent(this, LogInActivity.class);
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }

}