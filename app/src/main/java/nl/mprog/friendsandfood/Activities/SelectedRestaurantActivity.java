package nl.mprog.friendsandfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.mprog.friendsandfood.Adapters.CustomAdapterRatingBar;
import nl.mprog.friendsandfood.Classes.Review;
import nl.mprog.friendsandfood.R;

/**
 * Created by Tom Verburg on 12-1-2017.
 *
 * In this activity, the user is presented will all the infomation about a selected restaurant.
 * Here, the user can see the name, rating by friends, reviews by friends and friends who have
 * checked in. The user themselves can navigate to WriteReviewActivity by clicking the WriteReview
 * button and check in/out. Furthermore, the user can click on a review of a friend and navigate
 * to ReadReviewActivity and read the review a friend has written.
 */

public class SelectedRestaurantActivity extends BaseActivity implements View.OnClickListener{
    private ListView listViewCheckIn;
    private ListView listViewRatingBar;
    private List<Review> reviewList;

    private String restaurantID;
    private String restaurantName;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ArrayList<String> mFriendsCompleteNames = new ArrayList<>();
    private ArrayList<String> mFriendsID = new ArrayList<>();
    private ArrayList<String> allReviewIDs = new ArrayList<>();
    private HashMap<String, HashMap<String, String>> allReviewsHash = new HashMap<>();

    private RatingBar ratingBar;
    private ToggleButton toggle;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_restaurant);

        //Intent from previous activity.
        Intent intent = getIntent();
        restaurantName = intent.getStringExtra("restaurantName");
        restaurantID = intent.getStringExtra("restaurantID");

        //The Views.
        ratingBar = (RatingBar)findViewById(R.id.rating_bar);
        listViewRatingBar = (ListView)findViewById(R.id.list_view_review_friends);
        TextView name = (TextView) findViewById(R.id.selected_restaurant_name);
        findViewById(R.id.submit).setOnClickListener(this);
        name.setText(restaurantName);

        getUserFriendsValueEventListener();
        clickSelectReviewFriend();
        setToggleButton();

    }

    /** This method saves the check in status of the user to Firebase*/
    public void setToggleButton(){
        final DatabaseReference mRefCheckins =  database.getReference("checkin").child(restaurantID);
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String time = String.valueOf(System.currentTimeMillis());
                    mRefCheckins.child(getProfile()).setValue(createHashToggle(time));
                } else {
                    mRefCheckins.child(getProfile()).removeValue();
                }
            }
        });
    }

    /** Saves all the data in a map so it can be saved in one go, this way the listener doesn't
     * retrieve incomplete data. */
    public Map<String, Object> createHashToggle(String time){
        Map<String, Object> activityInfo = new HashMap<>();
        activityInfo.put("Time", time);
        activityInfo.put("RestaurantName", restaurantName);
        activityInfo.put("RestaurantID", restaurantID);
        activityInfo.put("User", getProfile());
        return activityInfo;
    }

    /** First EventListener which gets all the friends of the user and calls addFriendInformation */
    public void getUserFriendsValueEventListener(){
        DatabaseReference mRefFriends = database.getReference("users").child(getProfile()).child("friends");
        mRefFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String friendName = child.getValue().toString();
                    String friendID = child.getKey();
                    addFriendInformation(friendName, friendID);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** Saves data of friends in ArrayLists. */
    public void addFriendInformation(String friendName, String friendID){
        mFriendsCompleteNames.add(friendName);
        mFriendsID.add(friendID);
        findReviews(mFriendsID);
        findFriendsCheckin(mFriendsID);
    }

    /** Finds all the reviews of friends from Firebase. */
    public void findReviews(final ArrayList<String> friends){
        DatabaseReference mRefReviews =  database.getReference("reviews");
        mRefReviews.addValueEventListener(new ValueEventListener() {
            ArrayList<String> friendReviewWriterNames = new ArrayList<String>();
            //The score is for the ratingbar.
            float totalscore = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewList = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>) child.getValue();
                    for (int i = 0; i < friends.size(); i++) {
                        String friend_id = friends.get(i);
                        if (reviewHashFirebase.get("Writer").equals(friend_id) &
                                reviewHashFirebase.get("RestaurantID").equals(restaurantID)){
                                friendReviewWriterNames = addFriendReviewInformation(friendReviewWriterNames,
                                        reviewHashFirebase, i);
                                totalscore += Float.valueOf(String.valueOf(reviewHashFirebase.get("Rating")));
                        }
                    }
                    if (friendReviewWriterNames.size() != 0){
                        ratingBar.setRating(totalscore / (friendReviewWriterNames.size()));
                    }
                    customAdapter(reviewList);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** Gets all the review information of the friends of the user*/
    public ArrayList<String> addFriendReviewInformation(ArrayList<String> friendWriterNames,
                                                        HashMap<String, String> reviewHashFirebase,
                                                        int i){
        friendWriterNames.add(mFriendsCompleteNames.get(i));
        allReviewIDs.add(reviewHashFirebase.get("ReviewID"));
        allReviewsHash.put(reviewHashFirebase.get("ReviewID"), reviewHashFirebase);
        reviewList.add(new Review(mFriendsCompleteNames.get(i),
                reviewHashFirebase.get("ReviewID"),
                Float.valueOf(String.valueOf(reviewHashFirebase.get("Rating")))));
        return friendWriterNames;
    }

    /** Finds all the check ins of friends from Firebase. */
    public void findFriendsCheckin(final ArrayList<String> friends){
        DatabaseReference mRefCheckins =  database.getReference("checkin").child(restaurantID);
        mRefCheckins.addValueEventListener(new ValueEventListener() {
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
                            else if (user.equals(getProfile())) {
                                toggle.setChecked(true);
                            }
                        }
                    }
                    listViewCheckIn = (ListView) findViewById(R.id.list_view_check_in);
                    ListAdapter adapter = getAdapter(friendCheckIn);
                    listViewCheckIn.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}

    /** Sets custom adapter for the second ListView. */
    public void customAdapter(List<Review> list){
        CustomAdapterRatingBar customAdapter = new CustomAdapterRatingBar(getApplicationContext(), list);
        listViewRatingBar.setAdapter(customAdapter);
    }

    /** Starts ReadReviewActivity after clicking a review in the list view.*/
    public void clickSelectReviewFriend() {
        listViewRatingBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Review review = (Review) adapterView.getAdapter().getItem(position);
                String nameWriter = review.getTitle();
                String reviewID = allReviewIDs.get(position);
                HashMap<String, String> selectedReviewHash = allReviewsHash.get(reviewID);
                Intent getNameScreen = new Intent(getApplicationContext(),ReadReviewActivity.class);
                getNameScreen.putExtra("reviewHash", selectedReviewHash);
                getNameScreen.putExtra("nameWriter", nameWriter);
                startActivity(getNameScreen);
            }
        });
    }

    /** On click method for the navigation bar and other buttons.*/
    @Override
    public void onClick(View v) {
        //On click method for the navigation bar and other buttons.
        int i = v.getId();
        if (i == R.id.submit) {
            Intent intent = new Intent(SelectedRestaurantActivity.this,WriteReviewActivity.class);
            intent.putExtra("restaurantID", restaurantID);
            intent.putExtra("restaurantName", restaurantName);
            startActivity(intent);
        }
    }
}
