package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddGroupActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mDirectoryRef;
    private DatabaseReference mGroups;
    private DatabaseReference mUserGroups;
    private FirebaseUser user;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private MenuItem mDynamicMenuItem;
    private MenuItem mAddMember;
    private EditText mNameEditText;
    private Button mSubmitButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

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
        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mSubmitButton = (Button) findViewById(R.id.submitGroupButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method called when the sign up button is clicked.
             * Passes the current email and password into createAccount()
             * @param v the current View
             */
            @Override
            public void onClick(View v) {
                String nameText = mNameEditText.getText().toString();
                String key = mGroups.push().getKey();
                Group newGroup = new Group(nameText, key);
                createGroup(newGroup);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelActivity();
            }
        });
    }


    private void createGroup(Group group) {
        mDirectoryRef.push().setValue(group);
        mUserGroups.push().setValue(group);

        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cancelActivity() {
        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    /**
     * This method initializes the Firebase fields
     * needed to read and write entities to the database.
     * It also adds a listener to the reference requested to
     * check for when the reference is updated (e.g. keys are
     * inserted, updated, removed).
     */
    private void initializeFirebaseDBFields() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            /**
             * This method is prompted when a user logs in or logs out.
             * It adds an observer for auth state changes.
             * @param firebaseAuth the entry point of the Firebase SDK
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        database = FirebaseDatabase.getInstance();
        mDirectoryRef = database.getReference("Directory");
        mGroups = database.getReference("Groups");
        mUserGroups = database.getReference("Users").child(user.getUid()).child("Groups");
    }


    /**
     * On creating the menu bar, set the title of it
     * to the appropriate activity, inflate the menu,
     * and populate it with the appropriate
     * @param menu the menu bar to populate
     * @return whether the menu was created or not
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        setTitle(R.string.add_group);
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
