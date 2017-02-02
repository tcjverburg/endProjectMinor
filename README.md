# Application Name:Friends&Food
### Tom Verburg 10769633
### Test Facebook Email: test.programmeer@gmail.com
### Test Facebook Password: ProgramProj

## Copyright
Copyright (c) 2017 Verburg, Tom under the terms of the MIT license.

## Application Purpose
When going out and one wishes to go out for diner, it is often tough choosing a restaurant. Even when you look up a specific restaurant, the ratings given will most likely be given by people you do not know or gave an anonymous rating. However, when someone you know recommends a specific restaurant, you are more likely to be convinced by their opinion and make a quick decision on where to eat. Therefore, I created an app which uses your geo location to see what restaurants are near, and by logging in with facebook sees if any of your facebook friends have been there and left a positive review or are even checked in at that moment. 


## Visual Aspects Application
<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/pic_1.png" width=33%><img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/pic_2.png" width=33%><img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/pic_3.png" width=33%>

--------SelectedRestaurantActivity-------------------ReadReviewActivity------------------------YourReviewsActivity------------



<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/pic_4.png" width=33%><img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/pic_5.png" width=33%><img
src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/pic_6.png" width=33%>

-----------WriteReviewActivity------------------------FriendsFeedActivity---------------------NearRestaurantsActivity-----------



<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/pic_7.png" width=33%>

---------------LoginActivity---------------


## Data sets and sources
The data set that are going to be used will be generated by the users themselves. The reviews will then be saved using the live Firebase database. For sources, I mainly used StackOverflow and the Android Developers Website. However, there are a couple of large sources I used to make my application:

### LoginActivity
To use the login with facebook combined with Firebase, I used and adapted this source code to implement the login feature.

source:https://github.com/firebase/quickstartandroid/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java

Copyright 2016 Google Inc. All Rights Reserved, Licensed under the Apache License, Version 2.0 (the "License").

### NearbyRestaurantsActivity
This was the trickiest activity. The first source I used was to get the current location of the user, and the second source was to
get the nearby restaurants to the location of the user.

source:https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/

source:http://androidmastermind.blogspot.nl/2016/06/android-google-maps-with-nearyby-places.html

### MyAsyncTask
This is from a tutorial I used to perform a HTTP request to the Google Places API Webservice.

source: https://github.com/hishamMuneer/JsonParsingDemo

Copyright (c) 2015 Hisham, the MIT License.


## External Components
- Google Maps Android API
- Google Places API Webservice
- Identity Toolkit API (The Google Identity Toolkit API lets you use open standards to verify a user's identity)
- Firebase 
- Facebook SDK


