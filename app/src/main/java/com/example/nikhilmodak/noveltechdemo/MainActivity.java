package com.example.nikhilmodak.noveltechdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

/**
 * This is the main activity where users can add entries
 * to the database and view what they entered through a
 * text view. The only way they can come to this activity is
 * if they were authenticated.
 *
 * @source https://firebase.google.com/docs/database/android/read-and-write
 * @author Nikhil Modak
 */
public class MainActivity extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private TextView mTextView;
    private EditText mKeyEditText;
    private EditText mValueEditText;
    private Button mSubmitButton;

    private static final String DATABASE_NAME = "noveltechdemo-f15e1";

    /**
     * This is the onCreate method that is executed when the
     * activity first starts. It initializes the Firebase fields
     * along with the UI fields like the views and buttons.
     * @param savedInstanceState the last saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebaseDBFields();
        initializeUIFields();
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
        myRef = database.getReference(DATABASE_NAME);

        myRef.addValueEventListener(new ValueEventListener() {
            /**
             * Reads and listens for changes to the entire contents of a path
             * @param dataSnapshot the snapshot of the data that was changed
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post = dataSnapshot.getValue(String.class);
                System.out.println(post);
            }

            /**
             * Called when a request is unable to be made in a database.
             * @param databaseError the type of error that was caused in the database
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getCode());
            }
        });
    }

    /**
     * This method simply initializes all the UI fields
     * in the activity and gives the submit button an onClick
     * method. When it it clicked, it should update the database
     * with the values enetered into the edit texts.
     */
    private void initializeUIFields() {
        mTextView = (TextView) findViewById(R.id.testTxt);
        mKeyEditText = (EditText) findViewById(R.id.KeyText);
        mValueEditText = (EditText) findViewById(R.id.ValueText);
        mSubmitButton = (Button) findViewById(R.id.submitBtn);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Gives the submit button a method to set values to
             * the Firebase database.
             * @param v the current View
             */
            @Override
            public void onClick(View v) {
                setKeyValuePair();
            }
        });
    }

    /**
     * This method takes the key that was entered in the key
     * edit text and the value that was entered in the value
     * edit text and enters them into the database. If the key
     * already exists, its value is replaced with the new one.
     * The text view's text is also updated with the entered key.
     */
    private void setKeyValuePair() {
        String key = mKeyEditText.getText().toString();
        String value = mValueEditText.getText().toString();
        myRef = database.getReference(key);
        myRef.setValue(value);
        mTextView.setText(key);
    }

}
