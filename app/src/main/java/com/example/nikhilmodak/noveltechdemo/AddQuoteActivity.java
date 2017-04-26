package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddQuoteActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mDirectoryRef;

    private MenuItem mDynamicMenuItem;
    private EditText mQuoteEditText;
    private EditText mSpeakerEditText;
    private Button mSubmitButton;
    private Button mCancelButton;

    private String groupID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        initializeUIFields();
        initializeFirebaseDBFields();
    }

    /**
     * This method simply initializes all the UI fields
     * in the activity and gives the login button and
     * sign up button an onClick method. When the login
     * is clicked, it should check to see if the account exists
     * in the database. When sign up is clicked, a new account
     * is created.
     */
    private void initializeUIFields() {
        mQuoteEditText = (EditText) findViewById(R.id.quoteEditText);
        mSpeakerEditText = (EditText) findViewById(R.id.speakerEditText);
        mSubmitButton = (Button) findViewById(R.id.submitQuoteButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method called when the sign up button is clicked.
             * Passes the current email and password into createAccount()
             * @param v the current View
             */
            @Override
            public void onClick(View v) {
                String quoteText = mQuoteEditText.getText().toString();
                String speaker = mSpeakerEditText.getText().toString();
                Quote newQuote = new Quote(quoteText, speaker);
                createQuote(newQuote);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method called when the login button is clicked.
             * Passes the current email and password into signIn()
             * @param v the current View
             */
            @Override
            public void onClick(View v) {
                cancelActivity();
            }
        });
    }

    /**
     * This method initializes the Firebase fields
     * needed to read and write entities to the database.
     * It also adds a listener to the reference requested to
     * check for when the reference is updated (e.g. keys are
     * inserted, updated, removed).
     */
    private void initializeFirebaseDBFields() {
        database = FirebaseDatabase.getInstance();
        groupID = getIntent().getStringExtra("groupID");
        System.out.println(groupID);
        mDirectoryRef = database.getReference("Groups").child(groupID);
    }


    private void createQuote(Quote quote) {
        mDirectoryRef.push().setValue(quote);

        Intent intent = new Intent(getApplicationContext(), QuotesActivity.class);
        intent.putExtra("ID", groupID);
        startActivity(intent);
    }

    private void cancelActivity() {
        Intent intent = new Intent(getApplicationContext(), QuotesActivity.class);
        intent.putExtra("ID", groupID);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        setTitle(R.string.add_quote);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        // Get dynamic menu item
        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mDynamicMenuItem.setVisible(false);
        return true;
    }

}
