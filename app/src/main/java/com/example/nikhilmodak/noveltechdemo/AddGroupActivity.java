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

/**
 * This activity is where users can add new groups. All
 * they have to do is enter a name for the group and it is
 * created. Additional members can be added later.
 * @source https://developer.android.com/guide/topics/ui/menus.html
 * @source https://firebase.google.com/docs/auth/
 * @source https://firebase.google.com/docs/database/android/read-and-write
 */
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

    /**
     * This method is called when the activity is first
     * launched. It calls functions that initialize the Firebase
     * fields and the UI fields.
     * @param savedInstanceState the last saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        initializeUIFields();
        initializeFirebaseDBFields();
    }


    /**
     * This method simply initializes all the UI fields
     * in the activity and gives the submit button and
     * cancel button an onClick method. When the submit
     * is clicked, it should create the new group. When cancel
     * is clicked, the groups screen is prompted again.
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


    /**
     * This method adds a new group to the directory branch
     * of the database so the group can later be queried
     * for further operations. The groups activity is then
     * called.
     * @param group the group to add to the branch
     */
    private void createGroup(Group group) {
        mDirectoryRef.push().setValue(group);
        mUserGroups.push().setValue(group);

        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * This method cancels any progress made and returns
     * the user to the previous activity.
     */
    private void cancelActivity() {
        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * This method initializes the Firebase fields
     * needed to read and write entities to the database.
     * It also adds a listener to mAuth requested to
     * check for when mAuth is updated.
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
        mDirectoryRef = database.getReference(DatabaseKeyConstants.DIRECTORY);
        mGroups = database.getReference(DatabaseKeyConstants.GROUPS);
        mUserGroups = database.getReference(DatabaseKeyConstants.USERS)
                .child(user.getUid()).child(DatabaseKeyConstants.GROUPS);
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
        setTitle(R.string.add_group);
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
