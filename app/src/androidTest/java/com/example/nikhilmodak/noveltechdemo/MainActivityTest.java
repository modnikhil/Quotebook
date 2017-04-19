package com.example.nikhilmodak.noveltechdemo;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */
public class MainActivityTest {

    private final CountDownLatch writeSignal = new CountDownLatch(1);

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef  = database.getReference(DATABASE_NAME);;
    private static final String DATABASE_NAME = "noveltechdemo-f15e1";

    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void sampleTest() throws Exception {
        DatabaseReference usersRef = myRef.child("Users");
        Map<String, User> users = new HashMap<String, User>();
        User user = new User("Nikhil", "mod.nikhil@gmail.com");
        //users.put("Nikhil Personal", new User("Nikhil", "mod.nikhil@gmail.com"));
        //users.put("Nikhil School", new User("Nikhil", "nmodak2@illinois.edu"));



        usersRef.setValue(user).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        writeSignal.countDown();
                    }
                });


        usersRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dbSnap = dataSnapshot.child("Nikhil Personal");
                String tempuser = dbSnap.getValue(User.class).getName();
                assertEquals("Nikhil",
                        tempuser);
                writeSignal.countDown();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //myRef.setValue(users);
        writeSignal.await(10, TimeUnit.SECONDS);

    }
}