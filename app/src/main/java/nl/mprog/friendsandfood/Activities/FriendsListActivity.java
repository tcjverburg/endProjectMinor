package nl.mprog.friendsandfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.mprog.friendsandfood.R;

/**
 * Created by Tom Verburg on 11-1-2017.
 * Shows all the Activity of your Friends, whether they checked in at a restaurant or left a
 * review. Both these type of activities are put in a ListView and when the user clicks it, they
 * are either directed to the restaurant their friend checked in, or the review their friend wrote.
 */

public class FriendsListActivity extends BaseActivity implements View.OnClickListener{

    private ListView listView;

    private ArrayList<String> mFriendsCompleteNames = new ArrayList<>();
    private ArrayList<String> mFriendsCompleteIDs = new ArrayList<>();
    private ArrayList<String> friendWriterActivity = new ArrayList<>();
    private ArrayList<String> friendCheckInNames = new ArrayList<>();
    private ArrayList<String> allActivityIDs = new ArrayList<>();

    private HashMap<String, HashMap<String, String>> allActivityHash = new HashMap<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        //Views
        findViewById(R.id.restaurants_nav).setOnClickListener(this);
        findViewById(R.id.own_reviews_nav).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listViewFriends);

        getUserFriends();
        clickSelectActivityFriend();
        setColorButton();
    }

    /** Sets the color of the navigation button of current activity. */
    public void setColorButton(){
        ImageButton Nav = (ImageButton)findViewById(R.id.friends_nav);
        int myColor = getResources().getColor(R.color.colorButtonPressed);
        Nav.setBackgroundColor(myColor);
    }

    /** First EventListener which gets all the friends of the user and calls findFriendReviews. */
    public void getUserFriends(){
        DatabaseReference mRefFriends = database.getReference("users").child(getProfile()).child("friends");
        mRefFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String friendName = child.getValue().toString();
                    String friendID = child.getKey();
                    addFriendInformation(friendName, friendID);
                }
                findFriendReviews(mFriendsCompleteIDs);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** Saves data of friends in ArrayLists. */
    public void addFriendInformation(String friendName, String friendID){
        mFriendsCompleteNames.add(friendName);
        mFriendsCompleteIDs.add(friendID);
    }

    /** Finds all the reviews of friends from Firebase. */
    public void findFriendReviews(final ArrayList<String> friends){
        DatabaseReference mRefReviews = database.getReference("reviews");
        mRefReviews.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendWriterActivity = new ArrayList<>();
                allActivityIDs = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>) child.getValue();
                    for (int i = 0; i < friends.size(); i++) {
                        String friend_id = friends.get(i);
                        if (reviewHashFirebase.get("Writer").equals(friend_id)){
                            //Saves information of the review if it was written by a friend.
                            String userName = mFriendsCompleteNames.get(i);
                            String restName = reviewHashFirebase.get("RestaurantName");
                            friendWriterActivity.add(userName + getString(R.string.wrote_review_of) + restName);
                            allActivityIDs.add(reviewHashFirebase.get("ReviewID"));
                            allActivityHash.put(reviewHashFirebase.get("ReviewID"), reviewHashFirebase);
                        }
                    }
                }
                ListAdapter adapter = getAdapter(friendWriterActivity);
                listView.setAdapter(adapter);
                findFriendCheckIn(mFriendsCompleteIDs, friendWriterActivity);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** Finds all the current CheckIns of friends from Firebase. */
    public void findFriendCheckIn(final ArrayList<String> friends,
                                  final ArrayList<String> activity){
        DatabaseReference mRefCheckins =  database.getReference("checkin");
        mRefCheckins.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendCheckInNames = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    HashMap<String, HashMap> checkinHashFirebase = (HashMap<String, HashMap>) child.getValue();
                    Object[] keys = checkinHashFirebase.keySet().toArray();
                    for (Object key1 : keys) {
                        String key = (String) key1;
                        HashMap checkInInfoHash = checkinHashFirebase.get(key);
                        for (int z = 0; z < friends.size(); z++) {
                            String friend_id = friends.get(z);
                            if (key.equals(friend_id)& !checkTime(checkInInfoHash)) {
                                getFriendCheckinInfo(z, checkInInfoHash, key);
                            }
                        }
                        List<String> newList = new ArrayList<>(activity);
                        newList.addAll(friendCheckInNames);
                        ListAdapter adapter = getAdapter(newList);
                        listView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** Gets all the information of a check in of a friend. */
    public void getFriendCheckinInfo(int z, HashMap checkInInfoHash, String key){
        String userName = mFriendsCompleteNames.get(z);
        String restName = (String) checkInInfoHash.get("RestaurantName");
        friendCheckInNames.add(userName + getString(R.string.checked_in_at) + restName);
        allActivityIDs.add(key);
        allActivityHash.put(key, checkInInfoHash);
    }

    /** Checks how long ago the friend was checked in. If it is more than 24 hours ago, the check
     * in is deleted from Firebase and not shown in the activity feed of the user.*/
    public Boolean checkTime(HashMap checkInInfoHash){
        Long currentTime = System.currentTimeMillis();
        Float checkInTime = Float.valueOf(String.valueOf(checkInInfoHash.get("Time")));
        if (currentTime - checkInTime > 86400000){
            database.getReference("checkin").child(String.valueOf(checkInInfoHash.get("RestaurantID")))
                    .child(String.valueOf(checkInInfoHash.get("User"))).removeValue();
            return true;
        }
        return false;
    }

    /** This onClickListener is for the ListView which contains the activity
     * of your friends. Based on whether the user clicks on a list item containing a review message
     * or a check in message, the user is directed to a new activity.*/
    public void clickSelectActivityFriend() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String ID = allActivityIDs.get(position);
                String text = adapterView.getItemAtPosition(position).toString();
                if (text.contains("review")){
                    Integer position1 = mFriendsCompleteIDs.indexOf(allActivityHash.get(ID).get("Writer"));
                    String nameWriter = mFriendsCompleteNames.get(position1);
                    Intent getNameScreen = new Intent(getApplicationContext(),ReadReviewActivity.class);
                    getNameScreen.putExtra("reviewHash", allActivityHash.get(ID));
                    getNameScreen.putExtra("nameWriter", nameWriter);
                    startActivity(getNameScreen);
                } else if (text.contains("checked")){
                    Intent intent = new Intent(getApplicationContext(),SelectedRestaurantActivity.class);
                    intent.putExtra("restaurantName", allActivityHash.get(ID).get("RestaurantName"));
                    intent.putExtra("restaurantID", allActivityHash.get(ID).get("RestaurantID"));
                    startActivity(intent);
                }
            }
        });
    }

    /** On click method for the navigation bar and other buttons.*/
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.own_reviews_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), YourReviewsActivity.class);
            startActivity(getNameScreen);
            finish();
        } else if (i == R.id.restaurants_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), NearRestaurantActivity.class);
            startActivity(getNameScreen);
            finish();
        }
    }


}
