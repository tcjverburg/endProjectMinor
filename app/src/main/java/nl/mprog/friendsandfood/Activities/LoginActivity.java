package nl.mprog.friendsandfood.Activities;

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

import nl.mprog.friendsandfood.R;

/**
 * LoginActivity.java
 * TomVerburg-OwnProject
 *
 * Starting activity in which the user has to login. This is different based on whether the user has
 * the Facebook application installed on their phone and whether they are signed in. If not signed
 * in or not having the app at all, the user has to manually enter their Facebook username/e-mail
 * and password to use the application. If the user has the app installed and is logged in, the
 * user only has to click the Login button.
 *
 * source:https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main
 * /java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FacebookLogin";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private LoginButton loginButton;
    private String rawData;
    private ArrayList<String> friends = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        //Setting LoginButton and permissions for user Facebook data.
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        setListener();
        callBackManager();
    }

    //*CallbackManager which manages the Facebook Login, information retrieval and updates the UI.*/
    public void callBackManager(){
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                findViewById(R.id.login_button).setVisibility(View.INVISIBLE);
                Log.d(TAG, "facebook:onSuccess:" + login_result);
                handleFacebookAccessToken(login_result.getAccessToken());
                requestGraph();
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                updateUI(null);
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                updateUI(null);
            }
        });
    }

    //* Requests Facebook user friends information.*/
    public void requestGraph(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            rawData = response.getJSONObject().getJSONArray("data").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    //* mAuth listener which updates the UI and starts the next activity based on the login
    // status of the user.*/
    public void setListener(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (rawData != null) {
                        Intent intent = new Intent(LoginActivity.this,FriendsListActivity.class);
                        saveFriendsToFirebase(rawData);
                        startActivity(intent);
                        updateUI(user);
                    } else {
                        signOut();
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        updateUI(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //* Passes the activity result back to the Facebook SDK.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //* Tries to get the Facebook AccessToken. */
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //* Once login is successful, the friends list of the user is saved to Firebase for later
    // retrieval*/
    public void saveFriendsToFirebase(String rawData){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefFriends = database.getReference("users").child(Profile
                .getCurrentProfile().getId()).child("friends");
        JSONArray friendsList;
        try {
            friendsList = new JSONArray(rawData);
            for (int l=0; l < friendsList.length(); l++) {
                friends.add(friendsList.getJSONObject(l).getString("name"));
                myRefFriends.child(friendsList.getJSONObject(l).getString("id"))
                        .setValue(friendsList.getJSONObject(l).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //* Signs the user out. */
    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    //* Updates the UI based on the status of the user. */
    private void updateUI(FirebaseUser user) {
        if (user == null) {
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
        }
    }

}