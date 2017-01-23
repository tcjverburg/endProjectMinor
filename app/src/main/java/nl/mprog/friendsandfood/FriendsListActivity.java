package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gebruiker on 11-1-2017.
 */

public class FriendsListActivity extends BaseActivity implements View.OnClickListener{

    private ListView listView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ValueEventListener listener;

    DatabaseReference mRefFriends;
    ArrayList<String> mFriendsCompleteNames = new ArrayList<>();
    ArrayList<String> mFriendsCompleteIDs = new ArrayList<>();
    DatabaseReference mRefReviews;
    ArrayList<String> allReviewTimes = new ArrayList<String>();
    HashMap<String,HashMap> allReviewsHash = new HashMap<String, HashMap>();
    ArrayList<String> friendWriterNames = new ArrayList<String>();
    DatabaseReference  mRefCheckins =  database.getReference("checkin");





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        findViewById(R.id.restaurants_nav).setOnClickListener(this);
        findViewById(R.id.own_reviews_nav).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listViewFriends);


        String profile = Profile.getCurrentProfile().getId();
        mRefFriends = database.getReference("users").child(profile).child("friends");


        mRefFriends.addValueEventListener(new ValueEventListener() {


            //Database listener which fires when the database changes and counts reviews.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String friendName = child.getValue().toString();
                    String friendID = child.getKey();
                    mFriendsCompleteNames.add(friendName);
                    mFriendsCompleteIDs.add(friendID);


                }
                findReviews(mFriendsCompleteIDs);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void findReviews(final ArrayList<String> friends){

        mRefReviews =  database.getReference("reviews");

        listener = mRefReviews.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long currentTime = System.currentTimeMillis();

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>) child.getValue();

                    for (int i = 0; i < friends.size(); i++) {
                        String friend_id = friends.get(i).toString();
                        if (reviewHashFirebase.get("Writer").equals(friend_id)){
                            long differenceTime = (currentTime - Long.parseLong(String.valueOf(reviewHashFirebase.get("Time"))))/60000;
                            int hours = (int) differenceTime / 60; //since both are ints, you get an int
                            int minutes = (int) differenceTime % 60;
                            friendWriterNames.add(mFriendsCompleteNames.get(i) + " wrote a review of " + reviewHashFirebase.get("RestaurantName") + " " + hours + "hours and  " + minutes + " minutes ago.");
                            allReviewTimes.add(reviewHashFirebase.get("Time"));
                            allReviewsHash.put(reviewHashFirebase.get("Time"), reviewHashFirebase);
                        }
                    }
                    ListAdapter adapter = adapter(friendWriterNames);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void findCheckin(final ArrayList<String> friends){

        mRefCheckins.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.
            ArrayList<String> friendCheckIn = new ArrayList<String>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    HashMap<String, String> reviewHashFirebase = (HashMap<String, String>) child.getValue();


                    for (int i = 0; i < friends.size(); i++) {
                        String friend_id = friends.get(i);
                        String user = reviewHashFirebase.get("User");
                        if (user != null) {
                            if (user.equals(friend_id)) {
                                friendWriterNames.add(mFriendsCompleteNames.get(i) + " wrote a review of " + reviewHashFirebase.get("RestaurantName"));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}
    public ListAdapter adapter(ArrayList<String> arrayList){
        //Returns arrayAdapter for list view.
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
    }

    @Override
    public void onClick(View v) {
        //On click method for the navigation bar and other buttons.
        int i = v.getId();
        if (i == R.id.own_reviews_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), YourReviewsActivity.class);
            startActivity(getNameScreen);
            finish();
        }
        else if (i == R.id.restaurants_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), NearRestaurantActivity.class);
            startActivity(getNameScreen);
            finish();
        }
    }


}
