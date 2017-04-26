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

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef  = database.getReference();
    private static final String DATABASE_NAME = "noveltechdemo-f15e1";

    private TextView mUsernameText;
    private Button mSubmitButton;

    private String email;
    private String userID;
    private MenuItem mDynamicMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = getIntent().getStringExtra("Email");
        userID = getIntent().getStringExtra("UserID");

        initializeUIFields();
    }

    private void initializeUIFields() {
        mUsernameText = (TextView) findViewById(R.id.usernameText);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
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
    }

    private void createNewUser() {
        String username = mUsernameText.getText().toString();
        User newUser = new User(username, email, userID);

        DatabaseReference usersRef = myRef.child("Users");
        DatabaseReference childRef = usersRef.child(userID);
        childRef.setValue(newUser);

        Intent intent = new Intent(getApplicationContext(), GroupsActivity.class);
        getApplicationContext().startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        setTitle(R.string.create);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        // Get dynamic menu item
        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_add_group).setVisible(false);
        return true;
    }

}
