package com.example.nikhilmodak.noveltechdemo;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private TextView mTextView;
    private EditText mKeyEditText;
    private EditText mValueEditText;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebaseDBFields();
        initializeUIFields();
    }

    private void initializeFirebaseDBFields() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("noveltechdemo-f15e1");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post = dataSnapshot.getValue(String.class);
                System.out.println(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void initializeUIFields() {
        mTextView = (TextView) findViewById(R.id.testTxt);
        mKeyEditText = (EditText) findViewById(R.id.KeyText);
        mValueEditText = (EditText) findViewById(R.id.ValueText);
        mSubmitButton = (Button) findViewById(R.id.submitBtn);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeyValuePair();
            }
        });
    }

    private void setKeyValuePair() {
        String key = mKeyEditText.getText().toString();
        String value = mValueEditText.getText().toString();
        myRef = database.getReference(key);
        myRef.setValue(value);
        mTextView.setText(key);
    }

}
