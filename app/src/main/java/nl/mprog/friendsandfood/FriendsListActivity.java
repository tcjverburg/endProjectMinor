package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Gebruiker on 11-1-2017.
 */

public class FriendsListActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference myRefFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsondata");
        String userUid = intent.getStringExtra("uid");

        JSONArray friendslist;
        ArrayList<String> friends = new ArrayList<String>();

        listView = (ListView) findViewById(R.id.listView);

        //Firebase database, database reference and authentication.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert userUid != null;
        myRefFriends = database.getReference("users").child(userUid).child("friends");

// Dit bij login doen, dan hier weer uitlezen uit Firebase met datasnapshot
        try {
            friendslist = new JSONArray(jsondata);
            for (int l=0; l < friendslist.length(); l++) {
                //friends.add(friendslist.getJSONObject(l).getString("name"));
                myRefFriends.child(friendslist.getJSONObject(l).getString("id")).setValue(friendslist.getJSONObject(l).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friends); // simple textview for list item
        listView.setAdapter(adapter);

    }

    private void saveFriends(String friends){

    }

}
