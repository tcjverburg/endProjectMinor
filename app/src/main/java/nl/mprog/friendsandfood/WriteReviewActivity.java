package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Gebruiker on 12-1-2017.
 */

public class WriteReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference myRefReviews;
    RatingBar ratingBar;
    String restaurantID;
    String reviewID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        Intent intent = getIntent();
        findViewById(R.id.btnSubmitReview).setOnClickListener(this);
        restaurantID = intent.getStringExtra("restaurantID");

        //Firebase database, database reference and authentication.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                String reviewID = user.getUid() + oldReviews.size();
                getReference(reviewID);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }

    public void getReference(String newReviewID){
        reviewID = newReviewID;
    }

    @Override
    public void onClick(View v) {

        myRefReviews.child(reviewID).child("Restaurant").setValue(restaurantID);
        myRefReviews.child(reviewID).child("Writer").setValue(Profile.getCurrentProfile().getId());
        myRefReviews.child(reviewID).child("Rating").setValue(ratingBar.getRating());
    }
}
