package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * This is the main activity where users can add entries
 * to the database and view what they entered through a
 * text view. The only way they can come to this activity is
 * if they were authenticated.
 *
 * @source https://firebase.google.com/docs/database/android/read-and-write
 * @author Nikhil Modak
 */
public class GroupsActivity extends AppCompatActivity {


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private DatabaseReference mDirectoryRef = database.getReference("Directory");
    private DatabaseReference mGroups = database.getReference("Groups");
    private FirebaseUser user;

    private RecyclerView mGroupsRecyclerView;
    private FirebaseRecyclerAdapter<Group, GroupViewHolder> mAdapter;

    private MenuItem mDynamicMenuItem;
    private TextView mTextView;
    private EditText mKeyEditText;
    private EditText mValueEditText;
    private Button mSubmitButton;


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

        mGroupsRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        mGroupsRecyclerView.setHasFixedSize(true);
        mGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        user = FirebaseAuth.getInstance().getCurrentUser();

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
        myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            /**
             * Reads and listens for changes to the entire contents of a path
             * @param dataSnapshot the snapshot of the data that was changed
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String post = dataSnapshot.getValue(String.class);
                //System.out.println(post);
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


      /*  mSubmitButton.setOnClickListener(new View.OnClickListener() {
            *//**
             * Gives the submit button a method to set values to
             * the Firebase database.
             * @param v the current View
             *//*
            @Override
            public void onClick(View v) {
                setKeyValuePair();
            }
        });*/
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
        myRef = database.getReference();
        String uid = user.getUid();

        DatabaseReference emailRef = myRef.child("Users").child(uid);
        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User tempUser = dataSnapshot.getValue(User.class);
                mTextView.setText(tempUser.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAdapter = new FirebaseRecyclerAdapter<Group, GroupViewHolder>(Group.class,
                android.R.layout.simple_list_item_1, GroupViewHolder.class, mDirectoryRef) {
            @Override
            protected void populateViewHolder(GroupViewHolder viewHolder,
                                              final Group model, int position) {
                viewHolder.mTextView.setText(model.getName());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getApplicationContext(), QuotesActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            protected Group parseSnapshot(DataSnapshot snapshot) {
                //Log.d("mDirectoryRef", "parseSnapshot: " + snapshot.toString());
                return super.parseSnapshot(snapshot);
            }
        };

        mGroupsRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        setTitle(R.string.groups);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        // Get dynamic menu item
        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Here is just a good place to update item
        mDynamicMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_group:
                String key = mGroups.push().getKey();
                Group tempGroup = new Group("sample", key, null, null);
                Quote quote1 = new Quote("JRGLKGJTRLGJ", "Geoff");
                Quote quote2 = new Quote("HELLO", "Jeff");
                mDirectoryRef.push().setValue(tempGroup);
                DatabaseReference mQuotes = database.getReference("Quotes");
                mQuotes.push().setValue(quote1);
                mQuotes.push().setValue(quote2);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDynamicMenuItem.setVisible(false);
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public GroupViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(android.R.id.text1);
        }
    }

}
