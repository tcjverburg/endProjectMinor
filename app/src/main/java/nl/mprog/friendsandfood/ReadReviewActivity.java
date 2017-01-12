package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Gebruiker on 12-1-2017.
 */

public class ReadReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_review);
        Intent intent = getIntent();
        String review = intent.getStringExtra("review friend");
        TextView header = (TextView) findViewById(R.id.review_header);
        header.setText("Review from " + review);
    }
}
