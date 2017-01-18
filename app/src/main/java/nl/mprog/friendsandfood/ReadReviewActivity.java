package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by Gebruiker on 12-1-2017.
 */

public class ReadReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_review);
        Intent intent = getIntent();
        HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("reviewHash");
        String nameWriter = intent.getStringExtra("nameWriter");
        TextView header = (TextView) findViewById(R.id.review_header);
        TextView text = (TextView) findViewById(R.id.review_text);
        RatingBar rBar = (RatingBar) findViewById(R.id.ratingBarReadReview);
        header.setText("Review from " + nameWriter + " of Restaurant : " + hashMap.get("RestaurantName"));
        text.setText(hashMap.get("Text"));
        rBar.setRating(Float.valueOf(String.valueOf(hashMap.get("Rating"))));

    }
}
