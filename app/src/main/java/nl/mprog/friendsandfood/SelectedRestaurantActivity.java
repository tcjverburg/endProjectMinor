package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gebruiker on 12-1-2017.
 */

public class SelectedRestaurantActivity extends BaseActivity implements View.OnClickListener{
    ListView listViewCheckIn;
    ListView listViewRatingBar;
    CustomAdapterRatingBar customAdapter;
    List<Review> reviewList;

    String restaurantID;
    String restaurantName;

    DatabaseReference mRefFriends;
    DatabaseReference mRefReviews;
    DatabaseReference mRefCheckins;
    DatabaseReference mRefActivity;
    FirebaseDatabase database;
    ArrayList<String> mFriendsCompleteNames = new ArrayList<>();
    ArrayList<String> mFriendsID = new ArrayList<>();
    ArrayList<String> allReviewIDs = new ArrayList<String>();
    HashMap<String,HashMap> allReviewsHash = new HashMap<String, HashMap>();
    ValueEventListener listener;
    RatingBar ratingBar;
    final String profile = Profile.getCurrentProfile().getId();
    ToggleButton toggle;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_restaurant);

        Intent intent = getIntent();
        restaurantName = intent.getStringExtra("restaurantName");
        restaurantID = intent.getStringExtra("restaurantID");
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        listViewRatingBar = (ListView)findViewById(R.id.listViewReviewFriends);

        TextView name = (TextView) findViewById(R.id.selected_restaurant_name);
        findViewById(R.id.submit).setOnClickListener(this);

        name.setText(restaurantName);
        listViewCheckIn = (ListView) findViewById(R.id.listViewCheckIn);


        database = FirebaseDatabase.getInstance();
        mRefFriends = database.getReference("users").child(profile).child("friends");
        mRefCheckins =  database.getReference("checkin").child(restaurantID);
        mRefActivity = database.getReference("users").child(profile).child("activity");

        mainValueEventListener();
        clickSelectReviewFriend();
        setToggleButton();

    }

    public void setToggleButton(){
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String time = String.valueOf(System.currentTimeMillis());
                    Map<String, Object> activityInfo = new HashMap<>();
                    activityInfo.put("Time", time);
                    activityInfo.put("RestaurantName", restaurantName);
                    activityInfo.put("RestaurantID", restaurantID);
                    activityInfo.put("User", profile);
                    mRefCheckins.child(profile).setValue(activityInfo);
                } else {
                    mRefCheckins.child(profile).removeValue();
                }
            }
        });
    }

    public void mainValueEventListener(){
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
                    findCheckin(mFriendsID);
                }
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
            ArrayList<String> friendWriterNames = new ArrayList<String>();
            int totalscore = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewList = new ArrayList<>();

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>) child.getValue();

                    for (int i = 0; i < friends.size(); i++) {
                        String friend_id = friends.get(i).toString();
                        if (reviewHashFirebase.get("Writer").equals(friend_id) & reviewHashFirebase.get("RestaurantID").equals(restaurantID)){
                                friendWriterNames.add(mFriendsCompleteNames.get(i));
                                allReviewIDs.add(reviewHashFirebase.get("ReviewID"));
                                allReviewsHash.put(reviewHashFirebase.get("ReviewID"), reviewHashFirebase);
                                String rating = String.valueOf(reviewHashFirebase.get("Rating"));
                                Float ratingFloat = Float.valueOf(rating);
                                totalscore += ratingFloat;
                                reviewList.add(new Review(mFriendsCompleteNames.get(i), reviewHashFirebase.get("ReviewID"), ratingFloat));
                        }
                    }
                    if (friendWriterNames.size() != 0){
                        float score = totalscore/(friendWriterNames.size());
                        ratingBar.setRating(score);
                    }
                    customAdapter(reviewList);
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

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> friendCheckIn = new ArrayList<String>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    HashMap<String, HashMap> checkinHashFirebase = (HashMap<String, HashMap>) child.getValue();

                    for (int z = 0; z < friends.size(); z++) {
                        String friend_id = friends.get(z);

                        if (checkinHashFirebase.get("User") != null ) {
                            String user = String.valueOf(checkinHashFirebase.get("User"));

                            if (user.equals(friend_id)) {
                                friendCheckIn.add(mFriendsCompleteNames.get(z));
                            }
                            else if (user.equals(profile)) {
                                toggle.setChecked(true);
                            }
                        }
                    }
                    ListAdapter adapter = adapter(friendCheckIn);
                    listViewCheckIn.setAdapter(adapter);
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

    public void customAdapter(List<Review> list){
        //In aparte functie knallen
        customAdapter = new CustomAdapterRatingBar(getApplicationContext(), list);
        listViewRatingBar.setAdapter(customAdapter);
    }



    public void clickSelectReviewFriend() {
        //Starts SearchResultActivity after clicking a previous search term in the list view.
        listViewRatingBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Review review = (Review) adapterView.getAdapter().getItem(position);
                String nameWriter = review.getWriter();
                String reviewID = allReviewIDs.get(position);
                HashMap selectedReviewHash = allReviewsHash.get(reviewID);
                Intent getNameScreen = new Intent(getApplicationContext(),ReadReviewActivity.class);
                getNameScreen.putExtra("reviewHash", selectedReviewHash);
                getNameScreen.putExtra("nameWriter", nameWriter);
                startActivity(getNameScreen);
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
        }
    }
}
