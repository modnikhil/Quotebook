package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuotesActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mDirectoryRef;
    private DatabaseReference mQuote;
    private DatabaseReference mQuoteLikes;
    private FirebaseUser user;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView mQuotesRecyclerView;
    private FirebaseRecyclerAdapter<Quote, QuoteViewHolder> mAdapter;
    private MenuItem mDynamicMenuItem;
    private MenuItem mAddMember;

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
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
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
                    Log.d(getString(R.string.auth),
                            getString(R.string.signed_in_listener) + user.getUid());
                } else {
                    // User is signed out
                    Log.d(getString(R.string.auth), getString(R.string.signed_out_listener));
                }
            }
        };

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
        //System.out.println(user.getUid());
//        initializeFirebaseDBFields();

        mAdapter = new FirebaseRecyclerAdapter<Quote, QuoteViewHolder>(Quote.class,
                R.layout.quote_list_item, QuoteViewHolder.class, mDirectoryRef) {
            @Override
            protected void populateViewHolder(final QuoteViewHolder viewHolder,
                                              final Quote model, int position) {

                mQuote = mDirectoryRef.child(model.getQuoteID());
                mQuoteLikes = mQuote.child("likes");
                if (model.getLikes().get(user.getUid()) != null && model.getLikes().get(user.getUid()).equalsIgnoreCase(user.getUid())) {
                    viewHolder.mImageView.setImageResource(R.drawable.favorited_heart);
                }
                else {
                    viewHolder.mImageView.setImageResource(R.drawable.hollow_heart);
                }

                viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mQuote = mDirectoryRef.child(model.getQuoteID());
                        mQuoteLikes = mQuote.child("likes");
                        handleLike(model, user.getUid());
                        viewHolder.mFavorites.setText(String.valueOf(model.getLikes().size()));
                        if (model.getLikes().get(user.getUid()) != null && model.getLikes().get(user.getUid()).equalsIgnoreCase(user.getUid())) {
                            viewHolder.mImageView.setImageResource(R.drawable.favorited_heart);
                        }
                        else {
                            viewHolder.mImageView.setImageResource(R.drawable.hollow_heart);
                        }
                    }
                });


                viewHolder.mTextView.setText(model.getQuote());
                viewHolder.mFavorites.setText(String.valueOf(model.getLikes().size()));


            }

            @Override
            protected Quote parseSnapshot(DataSnapshot snapshot) {
                return super.parseSnapshot(snapshot);
            }
        };

        mQuotesRecyclerView.setAdapter(mAdapter);
    }

    public void handleLike(Quote model, String userID) {
        if (model.getLikes().isEmpty()) {
            mQuoteLikes.child(userID).setValue(userID);
            model.getLikes().put(userID, userID);
            return;
        }

        if (model.getLikes().get(user.getUid()) != null && model.getLikes().get(user.getUid()).equalsIgnoreCase(user.getUid())) {
            mQuoteLikes.child(userID).removeValue();
            model.getLikes().remove(userID);
        }
        else {
            mQuoteLikes.child(userID).setValue(userID);
            model.getLikes().put(userID, userID);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.quotes);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        mAddMember = menu.findItem(R.id.action_add_member);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mDynamicMenuItem.setVisible(true);
        mAddMember.setVisible(true);
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
            case  R.id.action_add_member:
                intent = new Intent(getApplicationContext(), AddMemberActivity.class);
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
        ImageView mImageView;
        TextView mFavorites;

        public QuoteViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.titleTextView);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
            mFavorites = (TextView) view.findViewById(R.id.ratingTextView);
        }
    }
}
