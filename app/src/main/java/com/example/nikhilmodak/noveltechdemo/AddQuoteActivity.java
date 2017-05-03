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

/**
 * This activity is where users can add quotes to
 * the current group they are in. The user can input the text
 * of the quote and the speaker.
 * @source https://developer.android.com/guide/topics/ui/menus.html
 * @source https://firebase.google.com/docs/auth/
 * @source https://firebase.google.com/docs/database/android/read-and-write
 */
public class AddQuoteActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mGroup;

    private MenuItem mDynamicMenuItem;
    private MenuItem mAddMember;
    private EditText mQuoteEditText;
    private EditText mSpeakerEditText;
    private Button mSubmitButton;
    private Button mCancelButton;

    private String groupID;


    /**
     * This method is called when the activity is first
     * launched. It calls functions that initialize the Firebase
     * fields and the UI fields.
     * @param savedInstanceState the last saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        initializeUIFields();
        initializeFirebaseDBFields();
    }

    /**
     * This method simply initializes all the UI fields
     * in the activity and gives the submit button and
     * cancel button an onClick method. When the submit
     * is clicked, it should create the new quote. When cancel
     * is clicked, the quotes screen is prompted again.
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

                createQuote(quoteText, speaker);
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
     */
    private void initializeFirebaseDBFields() {
        database = FirebaseDatabase.getInstance();
        groupID = getIntent().getStringExtra("groupID");
        mGroup = database.getReference(DatabaseKeyConstants.GROUPS).child(groupID);
    }


    /**
     * This method adds a new quote to the groups branch
     * of the database so the quote can later be queried
     * for further operations. The quotes activity is then
     * called.
     * @param quoteText the text of the quote
     * @param speaker the speaker of the quote
     */
    private void createQuote(String quoteText, String speaker) {
        String key = mGroup.push().getKey();
        Quote newQuote = new Quote(quoteText, speaker, key);
        mGroup.child(key).setValue(newQuote);

        cancelActivity();
    }

    /**
     * This method cancels any progress made and returns
     * the user to the previous activity.
     */
    private void cancelActivity() {
        Intent intent = new Intent(getApplicationContext(), QuotesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("ID", groupID);
        startActivity(intent);
    }

    /**
     * On creating the menu bar, set the title of it
     * to the appropriate activity, inflate the menu,
     * and populate it with the appropriate icons.
     * @param menu the menu bar to populate
     * @return whether the menu was created or not
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        setTitle(R.string.add_quote);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        mAddMember = menu.findItem(R.id.action_add_member);

        return true;
    }

    /**
     * On preparing the action bar, do what is normally
     * done to prepare the bar but also set the visibility
     * of the add item to false and the misc. icon to false;
     * @param menu the menu to prepare
     * @return if the menu was prepared or not
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mDynamicMenuItem.setVisible(false);
        mAddMember.setVisible(false);
        return true;
    }

}
