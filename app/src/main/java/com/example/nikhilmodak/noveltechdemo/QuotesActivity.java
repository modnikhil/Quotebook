package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuotesActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private DatabaseReference mDirectoryRef; ;
    private FirebaseUser user;

    private RecyclerView mQuotesRecyclerView;
    private FirebaseRecyclerAdapter<Quote, QuoteViewHolder> mAdapter;
    private MenuItem mDynamicMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        mQuotesRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        mQuotesRecyclerView.setHasFixedSize(true);
        mQuotesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        String groupID = getIntent().getStringExtra("ID");
        mDirectoryRef = database.getReference("Groups").child(groupID);
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


    @Override
    protected void onStart() {
        super.onStart();

        mAdapter = new FirebaseRecyclerAdapter<Quote, QuoteViewHolder>(Quote.class,
                android.R.layout.simple_list_item_1, QuoteViewHolder.class, mDirectoryRef) {
            @Override
            protected void populateViewHolder(QuoteViewHolder viewHolder,
                                              final Quote model, int position) {
                viewHolder.mTextView.setText(model.getQuote());

                /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getApplicationContext(), QuotesActivity.class);
                        startActivity(intent);
                    }
                });
                */
            }

            @Override
            protected Quote parseSnapshot(DataSnapshot snapshot) {
                //Log.d("mDirectoryRef", "parseSnapshot: " + snapshot.toString());
                return super.parseSnapshot(snapshot);
            }
        };

        mQuotesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        setTitle(R.string.quotes);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public QuoteViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(android.R.id.text1);
        }
    }



}
