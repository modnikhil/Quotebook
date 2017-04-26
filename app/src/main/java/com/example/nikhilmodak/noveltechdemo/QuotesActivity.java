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

    private FirebaseDatabase database;
    private DatabaseReference mDirectoryRef;
    private FirebaseUser user;

    private RecyclerView mQuotesRecyclerView;
    private FirebaseRecyclerAdapter<Quote, QuoteViewHolder> mAdapter;
    private MenuItem mDynamicMenuItem;

    private String groupID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        initializeUIFields();
        initializeFirebaseDBFields();
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
        groupID = getIntent().getStringExtra("ID");
        mDirectoryRef = database.getReference("Groups").child(groupID);
    }

    /**
     * This method simply initializes all the UI fields
     * in the activity and gives the submit button an onClick
     * method. When it it clicked, it should update the database
     * with the values enetered into the edit texts.
     */
    private void initializeUIFields() {
        mQuotesRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        mQuotesRecyclerView.setHasFixedSize(true);
        mQuotesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
            }

            @Override
            protected Quote parseSnapshot(DataSnapshot snapshot) {
                return super.parseSnapshot(snapshot);
            }
        };

        mQuotesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.quotes);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mDynamicMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_group:
                Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
                intent.putExtra("groupID", groupID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getApplicationContext().startActivity(intent);
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
