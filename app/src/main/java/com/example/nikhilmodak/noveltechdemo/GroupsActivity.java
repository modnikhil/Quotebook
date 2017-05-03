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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

    private FirebaseDatabase database;
    private DatabaseReference mUserGroups;
    private FirebaseUser user;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView mGroupsRecyclerView;
    private FirebaseRecyclerAdapter<Group, GroupViewHolder> mAdapter;
    private MenuItem mDynamicMenuItem;
    private MenuItem mAddMember;


    /**
     * This is the onCreate method that is executed when the
     * activity first starts. It initializes the Firebase fields
     * along with the UI fields like the views and buttons.
     * @param savedInstanceState the last saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        initializeUIFields();
        initializeFirebaseDBFields();
    }

    /**
     * On the activity starting, create the recycler view's adapter
     * such that each item represents a group in the mUserGroup database
     * reference. Each item will be updated with each group's appropriate
     * name and image.
     */
    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        mAdapter = new FirebaseRecyclerAdapter<Group, GroupViewHolder>(Group.class,
                R.layout.group_list_item , GroupViewHolder.class, mUserGroups) {
            @Override
            protected void populateViewHolder(GroupViewHolder viewHolder,
                                              final Group model, int position) {
                viewHolder.mTextView.setText(model.getName());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), QuotesActivity.class);
                        intent.putExtra("ID", model.getGroupID());
                        startActivity(intent);
                    }
                });
            }
        };

        mGroupsRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This is the onStop method called when the
     * activity is stopped. It does everything the method
     * normally does in addition to removing a listener from the
     * mAuth field if it contains one.
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
     * It also adds a listener to the mAuth to
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
        mUserGroups = database.getReference(DatabaseKeyConstants.USERS)
                .child(user.getUid()).child(DatabaseKeyConstants.GROUPS);
    }

    /**
     * This method simply initializes all the UI fields
     * in the activity, specifically the recycler view for
     * the group items.
     */
    private void initializeUIFields() {
        mGroupsRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        mGroupsRecyclerView.setHasFixedSize(true);
        mGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This class represents the View Holder for the RecyclerView
     * that represents all the group items for the user.
     */
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;

        public GroupViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.titleTextView);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
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
        setTitle(R.string.groups);
        getMenuInflater().inflate(R.menu.mymenu, menu);

        mDynamicMenuItem = menu.findItem(R.id.action_add_group);
        mAddMember = menu.findItem(R.id.action_add_member);
        return true;
    }

    /**
     * On preparing the action bar, do what is normally
     * done to prepare the bar but also set the visibility
     * of the add item to true and the misc. icon to true;
     * Also change the misc. icon to the log out icon.
     * @param menu the menu to prepare
     * @return if the menu was prepared or not
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mDynamicMenuItem.setVisible(true);
        mAddMember.setIcon(R.drawable.iconmonstr_log_out);
        mAddMember.setVisible(true);
        return true;
    }

    /**
     * This method decides what happens depending on what icon
     * is selected. If the add group button is pressed, go to
     * the add group activity. If the log out icon is pressed,
     * log out and go to the log in activity, else handle the method
     * normally.
     * @param item the item that was selected
     * @return if the item completed a task or not
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_add_group:
                intent = new Intent(getApplicationContext(), AddGroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                return true;
            case R.id.action_add_member:
                if (mAuthListener != null) {
                    mAuth.removeAuthStateListener(mAuthListener);
                }
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mAuth.signOut();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
