package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gebruiker on 12-1-2017.
 */

public class SelectedRestaurantActivity extends AppCompatActivity implements View.OnClickListener{
    ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_restaurant);
        Intent intent = getIntent();
        String restaurant = intent.getStringExtra("restaurant");
        ArrayList<String> friends = new ArrayList<String>();
        friends.add("John");
        TextView name = (TextView) findViewById(R.id.selected_restaurant_name);
        findViewById(R.id.submit).setOnClickListener(this);
        name.setText(restaurant);
        listView = (ListView) findViewById(R.id.listViewReviewFriends);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friends); // simple text view for list item
        listView.setAdapter(adapter);
        clickReviewFriend();
    }

    public void clickReviewFriend(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String reviewFriend = String.valueOf(adapterView.getItemAtPosition(position));
                Intent intent = new Intent(SelectedRestaurantActivity.this,ReadReviewActivity.class);
                intent.putExtra("review friend", reviewFriend);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        //On click method for the navigation bar and other buttons.
        int i = v.getId();
        if (i == R.id.submit) {
            Intent getNameScreen = new Intent(getApplicationContext(), WriteReviewActivity.class);
            startActivity(getNameScreen);
            finish();
        }
    }
}
