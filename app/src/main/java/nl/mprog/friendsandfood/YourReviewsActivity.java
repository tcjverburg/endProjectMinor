package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
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
 * Created by Gebruiker on 12-1-2017.
 */

public class YourReviewsActivity extends BaseActivity implements View.OnClickListener{
    ListView listView;
    DatabaseReference mRefFriends;
    DatabaseReference mRefReviews;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String profile = Profile.getCurrentProfile().getId();
    ArrayList<String> allReviewIDs = new ArrayList<String>();
    HashMap<String,HashMap> allReviewsHash = new HashMap<String, HashMap>();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_review);
        ArrayList<String> restaurants = new ArrayList<String>();
        findViewById(R.id.restaurants_nav).setOnClickListener(this);
        findViewById(R.id.friends_nav).setOnClickListener(this);

        ValueEventListener listener;
        //Demo
        restaurants.add("Restaurant 1");
        listView = (ListView) findViewById(R.id.listViewReviews);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurants); // simple text view for list item
        listView.setAdapter(adapter);




        mRefReviews =  database.getReference("reviews");
        listener = mRefReviews.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.
            ArrayList<String> ownReviews = new ArrayList<String>();
            int totalscore = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>) child.getValue();
                    if (reviewHashFirebase.get("Writer").equals(profile)){
                            ownReviews.add(reviewHashFirebase.get("RestaurantName"));
                            allReviewIDs.add(reviewHashFirebase.get("ReviewID"));
                            allReviewsHash.put(reviewHashFirebase.get("ReviewID"), reviewHashFirebase);

                    }
                    }
                    ListAdapter adapter = adapter(ownReviews);
                    listView.setAdapter(adapter);

                    //Map<String, String> reviewInfo = new HashMap<>();
                    //String value = child.getValue().toString();
                    //String key = child.getKey();
                    //reviewInfo.put(key, value);
                    //findReviews(mFriends);
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        clickSelectedReview();
    }

    public void clickSelectedReview(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String nameWriter = "yourself";
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
        if (i == R.id.friends_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), FriendsListActivity.class);
            startActivity(getNameScreen);
            finish();
        }
        else if (i == R.id.restaurants_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), NearRestaurantActivity.class);
            startActivity(getNameScreen);
            finish();
        }
    }

    public ListAdapter adapter(ArrayList<String> arrayList){
        //Returns arrayAdapter for list view.
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
    }
}