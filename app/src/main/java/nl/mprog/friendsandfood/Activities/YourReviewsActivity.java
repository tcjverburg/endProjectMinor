package nl.mprog.friendsandfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
 * Created by Gebruiker on 12-1-2017.
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

        listView = (ListView) findViewById(R.id.listViewReviews);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurants); // simple text view for list item
        listView.setAdapter(adapter);

        setButtonColor();
        getOwnReviews();
        clickSelectedReview();
        clickDeleteReview();
    }

    public void setButtonColor(){
        //Sets the color of the navigation button of current activity.
        ImageButton Nav = (ImageButton)findViewById(R.id.own_reviews_nav);
        int myColor = getResources().getColor(R.color.colorButtonPressed);
        Nav.setBackgroundColor(myColor);
    }

    public void getOwnReviews(){

        mRefReviews =  database.getReference("reviews");
        listener = mRefReviews.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.
            ArrayList<String> ownReviews = new ArrayList<String>();
            int totalscore = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    HashMap<String,String> reviewHashFirebase = (HashMap<String, String>) child.getValue();
                    if (reviewHashFirebase.get("Writer").equals(getProfile())){
                        ownReviews.add(reviewHashFirebase.get("RestaurantName"));
                        allReviewIDs.add(reviewHashFirebase.get("ReviewID"));
                        allReviewsHash.put(reviewHashFirebase.get("ReviewID"), reviewHashFirebase);
                    }
                }
                ListAdapter adapter = adapter(ownReviews);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

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


    public void clickDeleteReview() {
        //Deletes favorite from list view and from shared preference after long clicking the item.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                String reviewID = allReviewIDs.get(position);
                mRefReviews.child(reviewID).removeValue();
                Toast.makeText(getApplicationContext(), "Deleted Review",
                        Toast.LENGTH_SHORT).show();
                //TESTING :   getOwnReviews();
                return true;
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