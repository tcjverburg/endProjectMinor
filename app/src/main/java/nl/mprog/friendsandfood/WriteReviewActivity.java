package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 12-1-2017.
 */

public class WriteReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference myRefReviews;
    private DatabaseReference myRefReviewsInfo;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RatingBar ratingBar;
    String restaurantID;
    String restaurantName;
    String reviewID;
    final String profile = Profile.getCurrentProfile().getId();
    EditText editText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        Intent intent = getIntent();
        findViewById(R.id.btnSubmitReview).setOnClickListener(this);
        restaurantID = intent.getStringExtra("restaurantID");
        restaurantName = intent.getStringExtra("restaurantName");
        editText = (EditText) findViewById(R.id.edit_text_review);

        //Firebase database, database reference and authentication.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRefReviews = database.getReference("reviews");

        Log.d("profile", profile);
        FacebookSdk.sdkInitialize(getApplicationContext());




        myRefReviews.addValueEventListener(new ValueEventListener() {
            //Database listener which fires when the database changes and counts reviews.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> oldReviews = new ArrayList<>();

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String oldSearchTerm = child.getValue().toString();
                    oldReviews.add(oldSearchTerm);
                }
                String reviewID = profile + oldReviews.size();
                getReference(reviewID);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }

    public void getReference(String newReviewID){
        myRefReviewsInfo = myRefReviews.child(newReviewID);
        reviewID = newReviewID;
    }

    @Override
    public void onClick(View v) {
        DatabaseReference mRefActivity = database.getReference("users").child(profile).child("activity");
        String time = String.valueOf(System.currentTimeMillis());
        Map<String, Object> activityInfo = new HashMap<>();
        Map<String, Object> reviewInfo = new HashMap<>();
        reviewInfo.put("ReviewID", reviewID);
        reviewInfo.put("RestaurantID", restaurantID);
        reviewInfo.put("RestaurantName", restaurantName);
        reviewInfo.put("Writer", profile);
        reviewInfo.put("Rating", ratingBar.getRating());
        reviewInfo.put("Text", String.valueOf(editText.getText()));
        activityInfo.put("Type", "Review");
        activityInfo.put("RestaurantName", restaurantName);
        activityInfo.put("Time", time);
        mRefActivity.child(time+profile).updateChildren(activityInfo);
        myRefReviews.child(reviewID).updateChildren(reviewInfo);
    }
}
