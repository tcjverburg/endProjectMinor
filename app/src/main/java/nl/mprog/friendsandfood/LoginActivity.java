package nl.mprog.friendsandfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * LoginActivity.java
 * TomVerburg-OwnProject
 *
 * Starting activity in which the user has to login or create a new account using an email address.
 *
 * source:https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java
 */

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "FacebookLogin";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    private CallbackManager mCallbackManager;

    private String rawdata;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);



        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (rawdata != null) {
                        Intent intent = new Intent(LoginActivity.this,FriendsListActivity.class);
                        saveFriendsToFirebase(rawdata);
                        startActivity(intent);
                        updateUI(user);
                    }
                    else {
                        signOut();
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]

        // [START initialize_fb_login]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                findViewById(R.id.login_button).setVisibility(View.INVISIBLE);

                Log.d(TAG, "facebook:onSuccess:" + login_result);
                handleFacebookAccessToken(login_result.getAccessToken());
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    rawdata = rawName.toString();

                                    Log.d("RAWDATA", rawdata);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fb_login]
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]


    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    // [END auth_with_facebook]

    public void saveFriendsToFirebase(String rawData){
        String profile = Profile.getCurrentProfile().getId();

        //Firebase database, database reference and authentication.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefFriends = database.getReference("users").child(profile).child("friends");
        ArrayList<String> friends = new ArrayList<String>();
        JSONArray friendslist;
// Dit bij login doen, dan hier weer uitlezen uit Firebase met datasnapshot
        try {
            friendslist = new JSONArray(rawData);
            for (int l=0; l < friendslist.length(); l++) {
                friends.add(friendslist.getJSONObject(l).getString("name"));
                myRefFriends.child(friendslist.getJSONObject(l).getString("id")).setValue(friendslist.getJSONObject(l).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //findViewById(R.id.login_button).setVisibility(View.GONE);
            //findViewById(R.id.log_out_button).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.log_out_button).setVisibility(View.GONE);
        }
    }

}