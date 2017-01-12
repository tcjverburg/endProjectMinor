package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Gebruiker on 12-1-2017.
 */

public class NearRestaurantActivity extends AppCompatActivity implements View.OnClickListener{
    ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_restaurant);
        ArrayList<String> restaurants = new ArrayList<String>();
        restaurants.add("Restaurant 1");

        listView = (ListView) findViewById(R.id.listViewRestaurants);

        findViewById(R.id.own_reviews_nav).setOnClickListener(this);
        findViewById(R.id.friends_nav).setOnClickListener(this);

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurants); // simple text view for list item
        listView.setAdapter(adapter);
        clickSelectedRestaurant();
    }

    public void clickSelectedRestaurant(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String restaurant = String.valueOf(adapterView.getItemAtPosition(position));
                Intent intent = new Intent(NearRestaurantActivity.this,SelectedRestaurantActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
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
        else if (i == R.id.own_reviews_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), YourReviewsActivity.class);
            startActivity(getNameScreen);
            finish();
        }
    }
}
