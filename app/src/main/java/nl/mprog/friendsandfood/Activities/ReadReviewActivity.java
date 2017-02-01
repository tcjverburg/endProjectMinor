package nl.mprog.friendsandfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.HashMap;

import nl.mprog.friendsandfood.R;

/**
 * Created by Gebruiker on 12-1-2017.
 * a
 */

public class ReadReviewActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_review);
        Intent intent = getIntent();

        HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("reviewHash");

        String nameWriter = intent.getStringExtra("nameWriter");
        TextView header = (TextView) findViewById(R.id.review_header);
        TextView text = (TextView) findViewById(R.id.review_text);
        TextView nameRestaurant = (TextView)findViewById(R.id.restaurant_name);
        RatingBar rBar = (RatingBar) findViewById(R.id.ratingBarReadReview);

        header.setText(String.format("%s%s", getString(R.string.review_from), nameWriter));
        nameRestaurant.setText(hashMap.get("RestaurantName"));
        text.setText(hashMap.get("Text"));
        rBar.setRating(Float.valueOf(String.valueOf(hashMap.get("Rating"))));
    }
}
