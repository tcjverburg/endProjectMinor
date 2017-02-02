package nl.mprog.friendsandfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import nl.mprog.friendsandfood.R;

/**
 * Created by Tom Verburg on 12-1-2017.
 *
 * In this activity, the user is able to see his own reviews and read them by selecting them.
 * A review can also be deleted by long pressing it.
 */

public class YourReviewsActivity extends BaseActivity implements View.OnClickListener{
    ListView listView;
    DatabaseReference mRefReviews;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    ArrayList<String> allReviewIDs = new ArrayList<String>();
    ArrayList<String> restaurants = new ArrayList<String>();

    HashMap<String,HashMap> allReviewsHash = new HashMap<String, HashMap>();
    ValueEventListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_review);
        findViewById(R.id.restaurants_nav).setOnClickListener(this);
        findViewById(R.id.friends_nav).setOnClickListener(this);

        //View
        listView = (ListView) findViewById(R.id.listViewReviews);

        setButtonColor();
        getOwnReviews();
        clickSelectedReview();
        clickDeleteReview();
    }

    /** Sets the color of the navigation button of current activity. */

     public void setButtonColor(){
        ImageButton Nav = (ImageButton)findViewById(R.id.own_reviews_nav);
        int myColor = getResources().getColor(R.color.colorButtonPressed);
        Nav.setBackgroundColor(myColor);
    }

    /** Gets all the reviews from Firebase of the current user. */
    public void getOwnReviews(){

        mRefReviews =  database.getReference("reviews");
        listener = mRefReviews.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> ownReviews = new ArrayList<String>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>) child.getValue();
                    if (reviewHashFirebase.get("Writer").equals(getProfile())){
                        ownReviews.add(reviewHashFirebase.get("RestaurantName"));
                        allReviewIDs.add(reviewHashFirebase.get("ReviewID"));
                        allReviewsHash.put(reviewHashFirebase.get("ReviewID"), reviewHashFirebase);
                    }
                }
                ListAdapter adapter = getAdapter(ownReviews);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** Navigates the user to ReadReviewActivity and gives the user the opportunity to read back
     * a review.
     */
    public void clickSelectedReview(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String nameWriter = "You";
                String reviewID = allReviewIDs.get(position);
                HashMap selectedReviewHash = allReviewsHash.get(reviewID);
                Intent getNameScreen = new Intent(getApplicationContext(),ReadReviewActivity.class);
                getNameScreen.putExtra("reviewHash", selectedReviewHash);
                getNameScreen.putExtra("nameWriter", nameWriter);
                startActivity(getNameScreen);
            }
        });
    }

    /** Deletes review from list view and from Firebase after long clicking the item. */

    public void clickDeleteReview() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                String reviewID = allReviewIDs.get(position);
                mRefReviews.child(reviewID).removeValue();
                Toast.makeText(getApplicationContext(), R.string.deleted_review,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    /** On click method for the navigation bar. */
    @Override
    public void onClick(View v) {
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

}