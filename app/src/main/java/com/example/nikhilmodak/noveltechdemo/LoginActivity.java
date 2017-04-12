package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private EditText mEmailEditText;
    private EditText mPswdEditText;
    private Button mLoginButton;
    private Button mSignUpButton;

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
     * to catch changes in the logging of users.
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
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
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
                createAccount(email, password);
            }
        });

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
                signIn(email, password);
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
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Tag", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "An account with this email already exists",
                                    Toast.LENGTH_SHORT).show();
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
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Fail",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            getApplicationContext().startActivity(intent);
                        }
                    }
                });
    }
}
