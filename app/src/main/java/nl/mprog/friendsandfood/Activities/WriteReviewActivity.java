package nl.mprog.friendsandfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.mprog.friendsandfood.R;

/**
 * Created by Tom Verburg on 12-1-2017.
 * In this activity the user is able to write a review of a specific restaurant and give it a rating
 * out of 5 using a RatingBar. After the user clicks the "submit" button, the review is saved to
 * Firebase and the user is returned to the previous activity.
 */

public class WriteReviewActivity extends BaseActivity implements View.OnClickListener {

    private DatabaseReference myRefReviews;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private RatingBar ratingBar;
    private String restaurantID;
    private String restaurantName;
    private String reviewID;
    private final String profile = Profile.getCurrentProfile().getId();
    private EditText editText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        Intent intent = getIntent();
        restaurantID = intent.getStringExtra("restaurantID");
        restaurantName = intent.getStringExtra("restaurantName");

        findViewById(R.id.btnSubmitReview).setOnClickListener(this);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        editText = (EditText) findViewById(R.id.edit_text_review);
        TextView infoReview = (TextView)findViewById(R.id.write_restaurant_info);

        infoReview.setText(restaurantName);
        generateNewReviewID();
    }

    //* Generates a unique review ID based on the previous amount of reviews written
    // and the id of the user*/
    public void generateNewReviewID(){
        myRefReviews = database.getReference("reviews");
        myRefReviews.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> oldReviews = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String oldSearchTerm = child.getValue().toString();
                    oldReviews.add(oldSearchTerm);
                }
                WriteReviewActivity.this.reviewID = profile + oldReviews.size();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //*This is an onClick method which saves all the review information to Firebase and then
    // finishes this activity. By doing this, the user is returned to the SelectedRestaurant
    // activity.*/
    @Override
    public void onClick(View v) {
        String time = String.valueOf(System.currentTimeMillis());
        Map<String, Object> reviewInfo = new HashMap<>();
        reviewInfo.put("ReviewID", reviewID);
        reviewInfo.put("RestaurantID", restaurantID);
        reviewInfo.put("RestaurantName", restaurantName);
        reviewInfo.put("Writer", profile);
        reviewInfo.put("Rating", ratingBar.getRating());
        reviewInfo.put("Text", String.valueOf(editText.getText()));
        reviewInfo.put("Time", time);
        myRefReviews.child(reviewID).updateChildren(reviewInfo);
        Toast.makeText(getApplicationContext(), R.string.review_submitted,
                Toast.LENGTH_SHORT).show();
        finish();
    }
}
