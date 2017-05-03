package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mUsers;

    private TextView mUsernameText;
    private Button mSubmitButton;
    private Button mCancelButton;

    private String email;
    private String userID;
    private MenuItem mDynamicMenuItem;
    private MenuItem mAddMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        receiveExtras();
        initializeUIFields();
        initializeFirebaseDBFields();
    }

    private void receiveExtras() {
        email = getIntent().getStringExtra("Email");
        userID = getIntent().getStringExtra("UserID");
    }

    private void initializeUIFields() {
        mUsernameText = (TextView) findViewById(R.id.usernameText);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Gives the submit button a method to set values to
             * the Firebase database.
             * @param v the current View
             */
            @Override
            public void onClick(View v) {
                createNewUser();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Gives the submit button a method to set values to
             * the Firebase database.
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
        mUsers = database.getReference(DatabaseKeyConstants.USERS);
    }

    private void createNewUser() {
        String username = mUsernameText.getText().toString();
        User newUser = new User(username, email, userID);

        DatabaseReference childRef = mUsers.child(userID);
        childRef.setValue(newUser);

        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void cancelActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        setTitle(R.string.create);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        // Get dynamic menu item
        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        mAddMember = menu.findItem(R.id.action_add_member);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mDynamicMenuItem.setVisible(false);
        mAddMember.setVisible(false);
        return true;
    }

}
