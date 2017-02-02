# Report
## Create a report (REPORT.md), based on your design document, containing important decisions that you’ve made, e.g. where you changed your mind during the past weeks. This is how you show the reviewer that you actually understand what you have done.

<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/report_picture.png" width=33%>

----------NearRestaurantActivity----------

## Descriprion
The application is designed to use your geo location to see what restaurants are near, and by logging in with facebook sees if any of your facebook friends have been there and left a positive review or are even checked in at that moment. By clicking on one of the markers, as the ones you see in the image, you are navigated to an activity which shows information about the restaurant, reviews left by friends, the option to check in and write a review.

## Complete Functionality
The first part of the application will be the login part using facebook. Following this, there will be a screen which shows an activity feed of what their friends have done recently. This is either written a review, or checked in in a restaurant. In the next activity there is a map with restaurants based on the location of the user. The user will also then be able to navigate to several other screens. The user can select a restaurant which opens a new screen which shows information about the restaurant, ratings of friends and friends who are checked in. In this screen, the user can toggle the checkin/checkout button and the user is prompted to leave a rating after they check out. Furthermore, the user can access an activity which shows their previous reviews and an activity which shows all their friends using the app.

## Technical Design
First of all, there are 8 activities. One for each screen and a BaseActivity which is the Superclass of all the other activities, with the exception of LoginActivity.  There is 1 CustomArrayAdapter class for implementing a RatingBar, 1 AsyncTask class to do a HTTP request for the Google Places API Webservice, 1 Review class and 1 AppConfig  class which makes NearRestaurantActivity less messy. This is how the entire structure looks like:

<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/new_design.png" width=100%>

This BaseActivity makes for less duplication of code and has a listener which directs the user back to the login screen, if the user is no longer logged in. This is convenient for the creating of the Actionbar, mAuthStateListener and methods like the getListAdapter which are used through out different activities. This means that all Activities are in one way or another related to BaseActivity. The actionbar is only used so the user can sign out at any moment they are in the application by clicking the signout button in top right hand corner. 

LoginActivity makes connection with Facebook and handles the Facebook acess Tokens. When Authentication is succesful, the application first requests the permissions for the app to function. These are access to email, name and friendslist. Following this, the users Facebook friends who use the application are saved under their Facebook ID to Firebase. The user is then navigated to FriendsFeedActivity. 

FriendsFeedActivity shows you a list of all the activities your friends have done in a single ListView. This is done by reading out the entire friends list from Firebase and seeing where these ID's have checked in and what reviews they have written. If a check in of one of these friends is more than 24 hours ago, the check in is removed from Firebase. By clicking on the items in the list, the user is either directed to a SelectedRestaurantActivity or a ReadReviewActivity if they click on a check in event or a write review event respectively. 

NearRestaurantActivity is the activity which shows the Map fragment. In the onCreate, all the permissions are checked for accessing and requesting the current location of the user . After  the map is created and the Google API client is built, the location is used to query Google Places API Webservice for restaurants near the user. The API then returns 20 of the most relevant restaurants in the radius of 1km of the user and adds them as locationmarks on the map. This is done immediately after the avtivity is created, so the result is that the user immediately sees a map with several markers. When the user clicks on one of the markers, the user is navigated to SelectedRestaurantActivity and this activity receives the ID of the selected restaurant using the intent. 

In SelectedRestaurantActivity the ID is received from the intent of the previous activity and all the information is then read from firebase using this unique ID and the unique ID of every user. First, all the friends are retrieved from the firebase database again, but this time we scan for check ins and reviews which are also of the selected restaurant. The user will see the average rating given by all his friends of that restaurant, individual ratings of friends and friends who are checked in at that moment in time. All this information is retrieved from the Firebase Database. From here on, the user can click on a specific review and read that review in ReadReviewActivity or click on the write review button to go to WriteReviewActivity.

In WriteReviewActivity, the user can give a specific rating out of 5 and add a text. On the click of the button "Submit Review", a reviewID has been generated and the review itself (with this ID) is saved to the Firebase database. Following this the activity finishes, the user is returned to the SelectedRestaurantActivity and sees a toast that their review has been submitted. This way, the user knows what has happenened and doesn't repeatedly press the submit review button. 

In ReadReviewActivity, the user cannot interact with anything and can only see the rating of the specific review and the text if it was added. 

YourReviewsActivity is the activity that shows all your own reviews you have written. In the listview, you can see the name of the restaurant you wrote a review about and what rating you gave it. This information was retrieved from Firebase in this same activity. When the user clicks on a specific review in the listView, he is directed to ReadReviewActivity.

CustomListAdapter is a class which sets a custom adapter for a string TextView to be together with a RatingBar in a single list item in a ListView. This adapter was initially created for the reviews in SelectedRestaurantActivity, but reused in YourReviewsActivity to show the rating and writer/restaurant in a single list item. 

AppConfig is only a small class which contains a variety of variables needed in NearRestaurantActivity, but because this activity was already so full, I decided on putting these in a seperate class and importing them later.

The class Review has the variables title, id and rating. Here, title can be the name of the restaurant (YourReviewsActivity) or the name of the writer (SelectedRestaurantActivity). 

MyAsyncTask is only used by NearRestaurantActivity for the HTTP request to the Google Places API Webservice about what restaurants are near to the users current location. This only asynctask returns a JSON string which is parsed in NearRestaurantActivity. 

## Firebase Structure

- Users
  - User ID
    - Friends
      - FriendID

- Check in
  - Restaurant ID
    - User ID
      - RestaurantName
      - RestaurantID
      - Time
      - UserID
      
- Reviews
  - User ID
    - RestaurantName
    - RestaurantID
    - Rating
    - ReviewID
    - Text
    - Writer




## Challenges and Solutions

There were several challenges allong the way, but I have overcome most of them. First of all I had to put a lot of trouble in getting the Facebook SDK working in the first week. Seeing as logging in with Facebook wasn't enough (seeing as I had to get the friendslist as well), it took quite some reading and research to figure out how I could get the friendslist of the current user. Even though it took quite a while (and the Facebook SDK only gets a Facebook friendlist of existing users who use the app), I decided on still implementing this feature. In hindsight I would still go for this option, seeing as it makes it easier for the user seeing as you dont have to manually add friends.

Concerning the Google API's I only had trouble seeing as I reached the quota when experimenting with requesting the location and sending a query to the Google Places API Webservice. At first, I did not understand why the API was failing, but after doing some research and visiting the Google console, I figured out that my continuous requesting of the location was to blame. This happened Monday 30-01, so it stressed me out thinking that I broke the API in one way or another, seeing as I got error messages that made no sense. Seeing as a constantly requesting the location was not an option, I decided on adding a refresh button on top of the map fragment that recreates() the activity. Now I am quite pleased that I have come to this conclusion, because it is a very natural action to click on a refresh icon and refresh the map.

The Firebase structure also was quite tricky in the second and third week. In the first week I had made a design of how I thought I wanted it to look, but after 2 weeks I had altered this design to such an extent, that the end result is nowhere near the same as it used to be. In the second week I mainly had trouble because I had to start from scratch, and it took me quite a while to get a structure which saved the reviews in a way that worked for my application. In the third week I wanted to save the check in/out as well, and this caused my to add various rules to my database. Furthermore, at the end of the third week I decided on adding the activity feed which also resulted in a lot of stress and work to create a clean database structure.

The reason I wanted to implement an activity feed of all your friends, was because I had an activity (FriendsActivity) which only showed a couple of friends who also use the app, but there were no other features. Several people who used the app in the third week said that, if I had the time, I should try to implement some sort of functionality, so I would not have an empty activity. Seeing as the goal of the application was about the ratings of restaurants of your friends, I decided not to implement a chat function. After all, these are people you know and can contact via messenger. So, I came to the conclusion that I wanted a feed which showed all the activity of your friends concerning the application. Still, I am not entirely pleased with the result. This is because, even though all the activities are within a single ListView, the check ins and writing of review events are still seperated due to order of the value event listeners. Seeing as the order in which these events stand is important for the position, I could not simply shuffle the arrayList. So, this is the only thing I would really have done differently when looking back at it now.




