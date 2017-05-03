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

/**
 * This activity is where all the quotes from the group
 * are shown regardless of who the current user is. Each quote
 * will display how many likes it has and who said it.
 * @source https://developer.android.com/guide/topics/ui/menus.html
 * @source https://firebase.google.com/docs/auth/
 * @source https://firebase.google.com/docs/database/android/read-and-write
 */
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


    /**
     * This method is called when the activity is first
     * launched. It calls functions that initialize the Firebase
     * fields and the UI fields.
     * @param savedInstanceState the last saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        initializeUIFields();
        initializeFirebaseDBFields();
    }

    /**
     * On the activity starting, create the recycler view's adapter
     * such that each item represents a quote in the mGroup database
     * reference. Each item will be updated with each quote's appropriate
     * quote, speaker, and like counts.
     */
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
                setRatingImage(viewHolder, model);
                setRatingOnClick(viewHolder, model);
                setQuoteTexts(viewHolder, model);

            }
        };

        mQuotesRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This is the onStop method called when the
     * activity is stopped. It does everything the method
     * normally does in addition to cleaning up the recycler
     * adapter.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    /**
     * This method initializes the Firebase fields
     * needed to read and write entities to the database.
     * It also adds a listener to mAuth requested to
     * check for when mAuth is updated.
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
     * in the activity, specificallly the activity's recycler view.
     */
    private void initializeUIFields() {
        mQuotesRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        mQuotesRecyclerView.setHasFixedSize(true);
        mQuotesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This method just sets the textviews to the appropriate
     * value of the corresponding model quote.
     * @param viewHolder the view of the Quote to edit
     * @param model The quote in particular to base outputs off of
     */
    private void setQuoteTexts(QuoteViewHolder viewHolder, Quote model) {
        viewHolder.mTextView.setTypeface(null, Typeface.ITALIC);
        viewHolder.mTextView.setText("\""+ model.getQuote() + "\"");
        viewHolder.mFavorites.setText(String.valueOf(model.getLikes().size()));
        viewHolder.mSpeakerTextView.setText("-" + model.getSpeaker());
    }

    /**
     * Sets the image to whole heart if the user hasn't liked
     * the quote. Sets image to hollow if user has liked.
     * @param viewHolder the viewHolder of the Quote
     * @param model The Quote object to base outputs off of
     */
    private void setRatingImage(QuoteViewHolder viewHolder, Quote model) {
        if (userLikeExists(model)) {
            viewHolder.mImageView.setImageResource(R.drawable.favorited_heart);
        }
        else {
            viewHolder.mImageView.setImageResource(R.drawable.hollow_heart);
        }
    }

    /**
     * This method sets an OnClick method to each heart of
     * each quote in a group. If a heart is clicked, depending
     * on whether they have liked the quote before or not,
     * the heart will become hollow or whole. The database will
     * also update accordingly.
     * @param viewHolder the view holder of the recycler view list
     * @param model the Quote to base outputs off of
     */
    private void setRatingOnClick(final QuoteViewHolder viewHolder, final Quote model) {
        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuote = mGroup.child(model.getQuoteID());
                mQuoteLikes = mQuote.child(DatabaseKeyConstants.LIKES);
                handleLike(model, user.getUid());

                viewHolder.mFavorites.setText(String.valueOf(model.getLikes().size()));
                setRatingImage(viewHolder, model);
            }
        });
    }

    /**
     * This method manipulates the database to add the current
     * user id to the list of likes of the quote or remove it
     * depending on whether the user has liked the quote or not
     * before.
     * @param model The quote's likes to check
     * @param userID the current user ID to check for
     */
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

    /**
     * Returns whether the user's ID exists in the list of the
     * quote's likes.
     * @param model The quote who's likes to search
     * @return if the user has liked the quote
     */
    private boolean userLikeExists(Quote model) {
        return (model.getLikes().get(user.getUid()) != null &&
                model.getLikes().get(user.getUid()).equalsIgnoreCase(user.getUid()));
    }

    /**
     * This class represents the View Holder for the RecyclerView
     * that represents all the quote items in the group.
     */
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

    /**
     * On creating the menu bar, set the title of it
     * to the appropriate activity, inflate the menu,
     * and populate it with the appropriate icons.
     * @param menu the menu bar to populate
     * @return whether the menu was created or not
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.quotes);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        mAddMember = menu.findItem(R.id.action_add_member);
        return true;
    }

    /**
     * On preparing the action bar, do what is normally
     * done to prepare the bar but also set the visibility
     * of the add item to true and the misc. icon to true;
     * Also change the misc. icon to the add member icon.
     * @param menu the menu to prepare
     * @return if the menu was prepared or not
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mDynamicMenuItem.setVisible(true);
        mAddMember.setIcon(R.drawable.ic_person_add_white_48dp);
        mAddMember.setVisible(true);
        return true;
    }

    /**
     * This method decides what happens depending on what icon
     * is selected. If the add quote button is pressed, go to
     * the add quote activity. If the add member icon is pressed,
     * go to the add member activity, else handle the method
     * normally.
     * @param item the item that was selected
     * @return if the item completed a task or not
     */
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
                return super.onOptionsItemSelected(item);
        }
    }
}
