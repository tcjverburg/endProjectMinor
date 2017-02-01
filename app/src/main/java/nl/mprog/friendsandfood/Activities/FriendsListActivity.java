package nl.mprog.friendsandfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
 * review.
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

        findViewById(R.id.restaurants_nav).setOnClickListener(this);
        findViewById(R.id.own_reviews_nav).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listViewFriends);

        mainValueEventListener();
        clickSelect();
        setColorButton();
    }

    public void setColorButton(){
        //Sets the color of the navigation button of current activity.
        ImageButton Nav = (ImageButton)findViewById(R.id.friends_nav);
        int myColor = getResources().getColor(R.color.colorButtonPressed);
        Nav.setBackgroundColor(myColor);
    }

    public void mainValueEventListener(){
        DatabaseReference mRefFriends = database.getReference("users").child(getProfile()).child("friends");
        mRefFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String friendName = child.getValue().toString();
                    String friendID = child.getKey();
                    addFriendInformation(friendName, friendID);
                }
                findReviews(mFriendsCompleteIDs);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addFriendInformation(String friendName, String friendID){
        mFriendsCompleteNames.add(friendName);
        mFriendsCompleteIDs.add(friendID);
    }

    public void findReviews(final ArrayList<String> friends){
        DatabaseReference mRefReviews = database.getReference("reviews");
        mRefReviews.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>)child.getValue();
                    for (int i = 0; i < friends.size(); i++) {
                        String friend_id = friends.get(i);
                        if (reviewHashFirebase.get("Writer").equals(friend_id)){
                            String userName = mFriendsCompleteNames.get(i);
                            String restName = reviewHashFirebase.get("RestaurantName");
                            friendWriterActivity.add(userName + " wrote a review of " + restName);
                            allActivityIDs.add(reviewHashFirebase.get("ReviewID"));
                            allActivityHash.put(reviewHashFirebase.get("ReviewID"), reviewHashFirebase);
                        }
                    }
                }
                ListAdapter adapter = adapter(friendWriterActivity);
                listView.setAdapter(adapter);
                findCheckIn(mFriendsCompleteIDs, friendWriterActivity, allActivityIDs);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void findCheckIn(final ArrayList<String> friends,
                            final ArrayList<String> activity,
                            final ArrayList<String> allActivityIDs){
        DatabaseReference mRefCheckins =  database.getReference("checkin");
        mRefCheckins.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    HashMap<String, HashMap> checkinHashFirebase = (HashMap<String, HashMap>) child.getValue();
                    Object[] keys = checkinHashFirebase.keySet().toArray();
                    for (Object key1 : keys) {
                        String key = (String) key1;
                        HashMap checkInInfoHash = checkinHashFirebase.get(key);
                        for (int z = 0; z < friends.size(); z++) {
                            String friend_id = friends.get(z);
                            if (key.equals(friend_id)) {
                                String userName = mFriendsCompleteNames.get(z);
                                String restName = (String) checkInInfoHash.get("RestaurantName");
                                friendCheckInNames.add(userName + " checked in at " + restName);
                                allActivityIDs.add(key);
                                FriendsListActivity.this.allActivityHash.put(key, checkInInfoHash);
                            }
                        }
                        List<String> newList = new ArrayList<>(activity);
                        newList.addAll(friendCheckInNames);
                        ListAdapter adapter = adapter(newList);
                        listView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });}

    public void clickSelect() {
        //Starts SearchResultActivity after clicking a previous search term in the list view.
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
                    finish();
                } else if (text.contains("checked")){
                    Intent intent = new Intent(getApplicationContext(),SelectedRestaurantActivity.class);
                    intent.putExtra("restaurantName", allActivityHash.get(ID).get("RestaurantName"));
                    intent.putExtra("restaurantID", allActivityHash.get(ID).get("RestaurantID"));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public ListAdapter adapter(List<String> arrayList){
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
