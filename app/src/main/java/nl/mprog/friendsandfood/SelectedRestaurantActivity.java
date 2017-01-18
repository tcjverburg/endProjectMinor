package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gebruiker on 12-1-2017.
 */

public class SelectedRestaurantActivity extends AppCompatActivity implements View.OnClickListener{
    ListView listView;
    String restaurantID;
    String restaurantName;
    DatabaseReference mRefFriends;
    DatabaseReference mRefReviews;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<String> mFriendsCompleteNames = new ArrayList<>();
    ArrayList<String> mFriendsID = new ArrayList<>();
    ArrayList<String> allReviewIDs = new ArrayList<String>();
    HashMap<String,HashMap> allReviewsHash = new HashMap<String, HashMap>();
    ValueEventListener listener;
    RatingBar ratingBar;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_restaurant);


        Intent intent = getIntent();
        restaurantName = intent.getStringExtra("restaurantName");
        restaurantID = intent.getStringExtra("restaurantID");
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);


        TextView name = (TextView) findViewById(R.id.selected_restaurant_name);
        findViewById(R.id.submit).setOnClickListener(this);

        name.setText(restaurantName);
        listView = (ListView) findViewById(R.id.listViewReviewFriends);

        FacebookSdk.sdkInitialize(getApplicationContext());
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
                    mFriendsID.add(friendID);
                    findReviews(mFriendsID);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        clickSelectReviewFriend();
    }

    public void findReviews(final ArrayList<String> friends){

        mRefReviews =  database.getReference("reviews");


        listener = mRefReviews.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.
            ArrayList<String> friendWriterNames = new ArrayList<String>();
            int totalscore = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>) child.getValue();
                    for (int i = 0; i < friends.size(); i++) {
                        String friend_id = friends.get(i).toString();if (reviewHashFirebase.get("Writer").equals(friend_id) & reviewHashFirebase.get("RestaurantID").equals(restaurantID)){
                                friendWriterNames.add(mFriendsCompleteNames.get(i));
                                allReviewIDs.add(reviewHashFirebase.get("ReviewID"));
                                allReviewsHash.put(reviewHashFirebase.get("ReviewID"), reviewHashFirebase);
                                Log.d("lala1", friendWriterNames.toString());
                                String rating = String.valueOf(reviewHashFirebase.get("Rating"));
                                Log.d("lalascore", rating);
                                //Log.d("lala2", String.valueOf(rating));
                                totalscore += Integer.valueOf(rating);
                        }
                    }
                    if (friendWriterNames.size() != 0){
                        float score = totalscore/(friendWriterNames.size());
                        ratingBar.setRating(score);
                        Log.d("lala2", String.valueOf(score));
                    }
                    ListAdapter adapter = adapter(friendWriterNames);
                    listView.setAdapter(adapter);

                    //Map<String, String> reviewInfo = new HashMap<>();
                    //String value = child.getValue().toString();
                    //String key = child.getKey();
                    //reviewInfo.put(key, value);
                    //findReviews(mFriends);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        }

    public ListAdapter adapter(ArrayList<String> arrayList){
        //Returns arrayAdapter for list view.
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
    }



    public void clickSelectReviewFriend() {
        //Starts SearchResultActivity after clicking a previous search term in the list view.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String nameWriter = String.valueOf(adapterView.getItemAtPosition(position));
                String reviewID = allReviewIDs.get(position);
                HashMap selectedReviewHash = allReviewsHash.get(reviewID);
                Intent getNameScreen = new Intent(getApplicationContext(),ReadReviewActivity.class);
                getNameScreen.putExtra("reviewHash", selectedReviewHash);
                getNameScreen.putExtra("nameWriter", nameWriter);
                startActivity(getNameScreen);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        //On click method for the navigation bar and other buttons.
        int i = v.getId();
        if (i == R.id.submit) {
            Intent intent = new Intent(SelectedRestaurantActivity.this,WriteReviewActivity.class);
            intent.putExtra("restaurantID", restaurantID);
            intent.putExtra("restaurantName", restaurantName);
            startActivity(intent);
            mRefReviews.removeEventListener(listener);
            finish();
        }
    }
}
