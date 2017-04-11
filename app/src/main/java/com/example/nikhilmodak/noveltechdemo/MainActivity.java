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

    private String TAG = "Log";
    private TextView mTextView;
    private EditText mKeyEditText;
    private EditText mValueEditText;
    private Button mSubmitButton;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        mTextView = (TextView) findViewById(R.id.testTxt);
        mKeyEditText = (EditText) findViewById(R.id.KeyText);
        mValueEditText = (EditText) findViewById(R.id.ValueText);
        mSubmitButton = (Button) findViewById(R.id.submitBtn);
        //mTextView.setText(myRef.getKey());

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = mKeyEditText.getText().toString();
                String value = mValueEditText.getText().toString();
                myRef = database.getReference(key);
                myRef.setValue(value);
                mTextView.setText(key);
            }
        });



        //final ObjectExample temp = new ObjectExample("Banana", 0, "a banana");
        //myRef.setValue(temp);




    }
}
