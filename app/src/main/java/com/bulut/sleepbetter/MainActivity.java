package com.bulut.sleepbetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bulut.sleepbetter.Item;
import com.bulut.sleepbetter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

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



    private void loadquest() {
        mUserId = mFirebaseUser.getUid();

        // Set up ListView
        setContentView(R.layout.activity_main_questionnaire);

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
            }
        });

    }

    private void loadOld(){


        mUserId = mFirebaseUser.getUid();

        // Set up ListView
        final ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);

        // Add items via the Button and EditText at the bottom of the view.
        final EditText text = (EditText) findViewById(R.id.todoText);
        final Button button = (Button) findViewById(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Item item = new Item(text.getText().toString());
                Item item = new Item(text.getText().toString(), "tst");
                mDatabase.child("users").child(mUserId).child("items").push().setValue(item);
                text.setText("");
            }
        });

        // Use Firebase to populate the list.
        mDatabase.child("users").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add((String) dataSnapshot.child("title").getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.remove((String) dataSnapshot.child("title").getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Delete items when clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDatabase.child("users").child(mUserId).child("items")
                        .orderByChild("title")
                        .equalTo((String) listView.getItemAtPosition(position))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                    firstChild.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {

            //loadOld();
            loadquest();
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
