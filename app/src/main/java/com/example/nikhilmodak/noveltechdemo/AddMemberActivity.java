package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddMemberActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mDirectoryRef;
    private DatabaseReference mGroups;
    private DatabaseReference mUsers;
    private FirebaseUser user;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private MenuItem mDynamicMenuItem;
    private MenuItem mAddMember;
    private EditText mMemberEditText;
    private Button mSubmitButton;
    private Button mCancelButton;

    private String groupID;
    private Group group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
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
        mMemberEditText = (EditText) findViewById(R.id.memberEditText);
        mSubmitButton = (Button) findViewById(R.id.submitMemberButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method called when the sign up button is clicked.
             * Passes the current email and password into createAccount()
             * @param v the current View
             */
            @Override
            public void onClick(View v) {
                String member = mMemberEditText.getText().toString();

                addMember(member);
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
        mAuth = FirebaseAuth.getInstance();
        //user = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            /**
             * This method is prompted when a user logs in or logs out.
             * It adds an observer for auth state changes.
             * @param firebaseAuth the entry point of the Firebase SDK
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(getString(R.string.auth),
                            getString(R.string.signed_in_listener) + user.getUid());
                } else {
                    // User is signed out
                    Log.d(getString(R.string.auth), getString(R.string.signed_out_listener));
                }
            }
        };

        database = FirebaseDatabase.getInstance();
        mGroups = database.getReference("Groups");
        groupID = getIntent().getStringExtra("groupID");
        mDirectoryRef = mGroups.child(groupID);
        mUsers = database.getReference("Users");
        database.getReference("Directory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group tempGroup = snapshot.getValue(Group.class);
                    System.out.println(groupID);
                    System.out.println(tempGroup.getGroupID());
                    if (snapshot.getValue(Group.class).getGroupID().equalsIgnoreCase(groupID)) {
                        group = snapshot.getValue(Group.class);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addMember(final String email) {
        //mDirectoryRef.push().setValue(group);
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    System.out.println(user.getEmail().equalsIgnoreCase(email));
                    if (user.getEmail().equalsIgnoreCase(email)) {
                        mUsers.child(user.getUserID()).child("Groups").push().setValue(group);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        startActivity(intent);
    }

    private void cancelActivity() {
        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        startActivity(intent);
    }

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
        return true;
    }
}
