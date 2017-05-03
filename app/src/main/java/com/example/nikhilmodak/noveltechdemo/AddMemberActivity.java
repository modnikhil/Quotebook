package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

/**
 * This activity is where users can add new members to their
 * group by entering the new members email.
 * @source https://developer.android.com/guide/topics/ui/menus.html
 * @source https://firebase.google.com/docs/auth/
 * @source https://firebase.google.com/docs/database/android/read-and-write
 */
public class AddMemberActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mDirectory;
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


    /**
     * This method is called when the activity is first
     * launched. It calls functions that initialize the Firebase
     * fields and the UI fields.
     * @param savedInstanceState the last saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        initializeUIFields();
        initializeFirebaseDBFields();
    }


    /**
     * This method simply initializes all the UI fields
     * in the activity and gives the submit button and
     * cancel button an onClick method. When the submit
     * is clicked, it should add the new member. When cancel
     * is clicked, the quotes screen is prompted again.
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
     * It also adds a listener to mAuth requested to
     * check for when mAuth is updated. It also finds
     * the group object represented by the passed groupID.
     */
    private void initializeFirebaseDBFields() {
        mAuth = FirebaseAuth.getInstance();
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
        mDirectory = database.getReference(DatabaseKeyConstants.DIRECTORY);
        mUsers = database.getReference(DatabaseKeyConstants.USERS);
        groupID = getIntent().getStringExtra("groupID");
        findGroupFromID();
    }

    /**
     * This method finds the corresponding group object
     * from the passed groupID from the extras.
     */
    private void findGroupFromID() {
        mDirectory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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

    /**
     * This method adds a new member to the group
     * by retreiving the current group object the
     * current user is in and then queries the user
     * branch to find the requested user and add the
     * group object to that user.
     * @param email the email of the requested user
     */
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
        setTitle(R.string.add_member);
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
