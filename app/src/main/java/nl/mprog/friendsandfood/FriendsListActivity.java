package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Gebruiker on 11-1-2017.
 */

public class FriendsListActivity extends BaseActivity implements View.OnClickListener{

    private ListView listView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference mRefFriends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        findViewById(R.id.restaurants_nav).setOnClickListener(this);
        findViewById(R.id.own_reviews_nav).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listViewFriends);

        Intent intent = getIntent();
        String profile = Profile.getCurrentProfile().getId();
        mRefFriends = database.getReference("users").child(profile).child("friends");

        mRefFriends.addValueEventListener(new ValueEventListener() {
            ArrayList<String> mFriendsCompleteNames = new ArrayList<>();

            //Database listener which fires when the database changes and counts reviews.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String friendName = child.getValue().toString();
                    mFriendsCompleteNames.add(friendName);

                }
                ListAdapter adapter = adapter(mFriendsCompleteNames);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public ListAdapter adapter(ArrayList<String> arrayList){
        //Returns arrayAdapter for list view.
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
    }

    @Override
    public void onClick(View v) {
        //On click method for the navigation bar and other buttons.
        int i = v.getId();
        if (i == R.id.own_reviews_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), YourReviewsActivity.class);
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
