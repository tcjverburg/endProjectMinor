package nl.mprog.friendsandfood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nl.mprog.friendsandfood.R;

/**
 * Created by Tom Verburg on 18-1-2017.
 * Base activity which contains a listener which tracks whether a user is still logged in
 * and returns the application to the LoginActivity if this is not the case. This class also
 * contains various methods which are used more than once through different activities.
 */

public class BaseActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String profile = Profile.getCurrentProfile().getId();
    private FirebaseUser user;
    private static final String TAG = "Base";
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        user = mAuth.getCurrentUser();
        mainListener();
    }

    /** The listener which starts the LoginActivity if the user is logged out. */
    public void mainListener(){
         mAuthListener = new FirebaseAuth.AuthStateListener() {

             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 if (user != null) {
                     // User is signed in
                     Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                 }
                 else {
                     // User is signed out
                     Log.d(TAG, "onAuthStateChanged:signed_out");
                     Intent getNameScreen = new Intent(getApplicationContext(), LoginActivity.class);
                     startActivity(getNameScreen);
                 }
             }
         };
     }

    /** Creates actionbar and adds the logout button. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //
        getSupportActionBar().setTitle(R.string.app_name);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /** Calls signOut() if sign out item is selected in the action bar. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                signOut();
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        user =  mAuth.getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public String getProfile(){
        return profile;
    }


}
