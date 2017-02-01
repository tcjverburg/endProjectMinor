package nl.mprog.friendsandfood.Classes;

/**
 * Created by Tom Verburg on 16-1-2017.
 * This class contains a variety of variables used by the NearRestaurantActivity which are needed to
 * get the information from the JSON object.
 *
 * Source: http://androidmastermind.blogspot.nl/2016/06/android-google-maps-with-nearyby-places.html
 */

public final class AppConfig {

    public static final String TAG = "gplaces";
    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";

    //Keys for nearby places for the JSON object returned from the Google API.
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String NAME = "name";
    public static final String PLACE_ID = "place_id";


}
