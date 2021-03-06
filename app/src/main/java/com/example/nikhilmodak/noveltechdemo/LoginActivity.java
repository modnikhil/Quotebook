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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This activity represents the first Activity the user
 * will see upon opening up the app. It is the login page where
 * the user will need to enter their email and password into
 * the text fields to validate their membership. Or, if they choose,
 * they can sign up for a membership.
 *
 * @source https://firebase.google.com/docs/auth/android/password-auth
 * @author Nikhil Modak
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDirectory = database.getReference("Directory");
    private DatabaseReference mGroups = database.getReference("Groups");

    private EditText mEmailEditText;
    private EditText mPswdEditText;
    private Button mLoginButton;
    private Button mSignUpButton;
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
        setContentView(R.layout.activity_login);

        initializeFirebaseFields();
        initializeUIFields();
    }

    /**
     * This is the onStart method called when the
     * activity is started. It does everything the method
     * normally does in addition to adding a listener to the
     * mAuth field to look out for authentication actions.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * This is the onStop method called when the
     * activity is stopped. It does everything the method
     * normally does in addition to removing a listener from the
     * mAuth field if it contains one.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * This method initializes the Firebase fields
     * needed to authorize users to the database.
     * It also initializes the listener to an AuthStateListener
     * to catch changes in the logging of users. If the user is
     * already logged in, go straight to the groups activity.
     */
    private void initializeFirebaseFields() {
        mAuth = FirebaseAuth.getInstance();
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
                    Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
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
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPswdEditText = (EditText) findViewById(R.id.pswdEditText);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mSignUpButton = (Button) findViewById(R.id.signButton);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method called when the login button is clicked.
             * Passes the current email and password into signIn()
             * @param v the current View
             */
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString();
                String password = mPswdEditText.getText().toString();
                if (email.length() == 0 || password.length() == 0) {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_field_required,
                            Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_invalid_password,
                            Toast.LENGTH_SHORT).show();
                }
                else if (!email.contains("@")) {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_invalid_email,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    signIn(email, password);
                }
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method called when the sign up button is clicked.
             * Passes the current email and password into createAccount()
             * @param v the current View
             */
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString();
                String password = mPswdEditText.getText().toString();
                if (email.length() == 0 || password.length() == 0) {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_field_required,
                            Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_invalid_password,
                            Toast.LENGTH_SHORT).show();
                }
                else if (!email.contains("@")) {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_invalid_email,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    createAccount(email, password);
                }
            }
        });


    }


    /**
     * The method called to sign in an old user into the application.
     * If sign in fails, display a message to the user. If sign in
     * succeeds the auth state listener will be notified and logic to handle
     * the signed in user can be handled in the listener. Any additional
     * information attached to the user object can be passed through
     * to the next activity.
     * @param email the email the user inputted
     * @param password the password the user inputted
     */
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    /**
                     * This method is called when a task related to logging in
                     * is completed. If the log in is successful, the user will
                     * be taken to the GroupsActivity activity.
                     * @param task the auth task that was done.
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.incorrect_email_password,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    /**
     * The method called to create a new account in the Firebase
     * database. If sign in fails, display a message to the user.
     * If sign in succeeds the auth state listener will be notified
     * and logic to handle the signed in user can be handled in
     * the listener.
     * @param email the email the user inputted
     * @param password the password the user inputted
     */
    private void createAccount(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    /**
                     * This method is called when a task related to signing up
                     * is completed. Depending on whether or not the sign up request
                     * was successful, a toast will show up.
                     * @param task the auth task that was done.
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    R.string.error_invalid_email,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            intent.putExtra("Email", email);
                            intent.putExtra("UserID", user.getUid());
                            startActivity(intent);
                        }
                    }
                });
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
        setTitle(R.string.app_name);
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
