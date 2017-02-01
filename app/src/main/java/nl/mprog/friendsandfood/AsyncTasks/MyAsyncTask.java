package nl.mprog.friendsandfood.AsyncTasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tom Verburg on 31-1-2017.
 * This class contains the AsyncTask to get a JSON String from the Google Places Api, based
 * on the query which was generated in the NearRestaurantActivity.
 *
 * source: https://www.youtube.com/watch?v=Gyaay7OTy-w
 */

public class MyAsyncTask extends AsyncTask<String, String, String> {

    /** Tries to make connection with the API and returns the result in String form. */
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** Returns result in String. */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
