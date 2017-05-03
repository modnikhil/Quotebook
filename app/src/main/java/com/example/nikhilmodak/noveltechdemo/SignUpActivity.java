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

/**
 * This activity is where new users can finish up the registration
 * process for the app.
 * @source https://developer.android.com/guide/topics/ui/menus.html
 * @source https://firebase.google.com/docs/auth/
 * @source https://firebase.google.com/docs/database/android/read-and-write
 */
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

    /**
     * This method is called when the activity is first
     * launched. It calls functions that initialize the Firebase
     * fields and the UI fields.
     * @param savedInstanceState the last saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        receiveExtras();
        initializeUIFields();
        initializeFirebaseDBFields();
    }

    /**
     * This methos receives the extras sent in from
     * the log in screen i.e. email and userID.
     */
    private void receiveExtras() {
        email = getIntent().getStringExtra("Email");
        userID = getIntent().getStringExtra("UserID");
    }

    /**
     * This method simply initializes all the UI fields
     * in the activity and gives the submit button and
     * cancel button an onClick method. When the submit
     * is clicked, it should create the new user. When cancel
     * is clicked, the login screen is prompted again.
     */
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
     */
    private void initializeFirebaseDBFields() {
        database = FirebaseDatabase.getInstance();
        mUsers = database.getReference(DatabaseKeyConstants.USERS);
    }

    /**
     * This method adds a new user to the user branch
     * of the database so the user can later be queried
     * for furthur operations. The groups activity is then
     * called.
     */
    private void createNewUser() {
        String username = mUsernameText.getText().toString();
        User newUser = new User(username, email, userID);

        DatabaseReference childRef = mUsers.child(userID);
        childRef.setValue(newUser);

        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * This method cancels any progress made and returns
     * the user to the previous activity.
     */
    private void cancelActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        setTitle(R.string.create);
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
