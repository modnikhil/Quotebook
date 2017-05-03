package com.example.nikhilmodak.noveltechdemo;

import android.content.Intent;
import android.graphics.Typeface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuotesActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mGroup;
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
            }
        };

        database = FirebaseDatabase.getInstance();
        groupID = getIntent().getStringExtra("ID");
        mGroup = database.getReference(DatabaseKeyConstants.GROUPS).child(groupID);
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
                R.layout.quote_list_item, QuoteViewHolder.class, mGroup) {
            @Override
            protected void populateViewHolder(final QuoteViewHolder viewHolder,
                                              final Quote model, int position) {

                mQuote = mGroup.child(model.getQuoteID());
                mQuoteLikes = mQuote.child(DatabaseKeyConstants.LIKES);

                if (userLikeExists(model)) {
                    viewHolder.mImageView.setImageResource(R.drawable.favorited_heart);
                }
                else {
                    viewHolder.mImageView.setImageResource(R.drawable.hollow_heart);
                }

                viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mQuote = mGroup.child(model.getQuoteID());
                        mQuoteLikes = mQuote.child(DatabaseKeyConstants.LIKES);
                        handleLike(model, user.getUid());
                        viewHolder.mFavorites.setText(String.valueOf(model.getLikes().size()));
                        if (userLikeExists(model)) {
                            viewHolder.mImageView.setImageResource(R.drawable.favorited_heart);
                        }
                        else {
                            viewHolder.mImageView.setImageResource(R.drawable.hollow_heart);
                        }
                    }
                });

                viewHolder.mTextView.setTypeface(null, Typeface.ITALIC);
                viewHolder.mTextView.setText("\""+ model.getQuote() + "\"");
                viewHolder.mFavorites.setText(String.valueOf(model.getLikes().size()));
                viewHolder.mSpeakerTextView.setText("-" + model.getSpeaker());

            }
        };

        mQuotesRecyclerView.setAdapter(mAdapter);
    }

    public void handleLike(Quote model, String userID) {
        if (model.getLikes().isEmpty()) {
            mQuoteLikes.child(userID).setValue(userID);
            model.getLikes().put(userID, userID);
        }
        else if (userLikeExists(model)) {
            mQuoteLikes.child(userID).removeValue();
            model.getLikes().remove(userID);
        }
        else {
            mQuoteLikes.child(userID).setValue(userID);
            model.getLikes().put(userID, userID);
        }
    }

    private boolean userLikeExists(Quote model) {
        return (model.getLikes().get(user.getUid()) != null &&
                model.getLikes().get(user.getUid()).equalsIgnoreCase(user.getUid()));
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
        TextView mSpeakerTextView;
        ImageView mImageView;
        TextView mFavorites;

        public QuoteViewHolder(View view) {
            super(view);
            mSpeakerTextView = (TextView) view.findViewById(R.id.speakerTextView);
            mTextView = (TextView) view.findViewById(R.id.titleTextView);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
            mFavorites = (TextView) view.findViewById(R.id.ratingTextView);
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
        mAddMember.setIcon(R.drawable.ic_person_add_white_48dp);
        mAddMember.setVisible(true);
        return true;
    }
}
