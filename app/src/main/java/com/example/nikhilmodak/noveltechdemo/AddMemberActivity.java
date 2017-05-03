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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddMemberActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mDirectory;
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
            }
        };

        database = FirebaseDatabase.getInstance();
        mGroups = database.getReference(DatabaseKeyConstants.GROUPS);
        groupID = getIntent().getStringExtra("groupID");
        mDirectory = mGroups.child(groupID);
        mUsers = database.getReference(DatabaseKeyConstants.USERS);
        database.getReference(DatabaseKeyConstants.DIRECTORY).addListenerForSingleValueEvent(new ValueEventListener() {
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
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    System.out.println(user.getEmail().equalsIgnoreCase(email));
                    if (user.getEmail().equalsIgnoreCase(email)) {
                        mUsers.child(user.getUserID()).child(DatabaseKeyConstants.GROUPS).push().setValue(group);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        cancelActivity();
    }

    private void cancelActivity() {
        Intent intent = new Intent(getApplicationContext(), QuotesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("ID", groupID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.add_member);
        getMenuInflater().inflate(R.menu.mymenu, menu);
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
