package nl.mprog.friendsandfood.Rest;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Gebruiker on 31-1-2017.
 * //source: https://www.youtube.com/watch?v=Gyaay7OTy-w

 */

public class MyAsyncTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        //tries to make connection if possible
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();

            String line;

            //adds line by line to the buffer from the api
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

    //filters the string we got as a result of the method getting the information from the api
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
