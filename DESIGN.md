##Diagram of Modules

<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/Tech%20Design.png" width=100%>

##Sketches

<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/Login%20Activity.png" width=33%><img
src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/Friends%20Activity.png" width=33%><img
src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/Read%20Review%20Activity.png" width=33%>

--------------LoginActivity--------------------------------FriendsActivity----------------------------ReadReviewActivity----------

<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/Restaurant%20Near%20User%20Activity.png" width=33%><img 
src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/Selected%20Restaurant%20Activity.png" width=33%><img
src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/Write%20Review%20Activity.png" width=33%>

--------RestaurantNearUserActivity-------------------SelectedRestaurantActivity-------------------WriteReviewActivity--------

<img src="https://github.com/tcjverburg/endProjectMinor/blob/master/doc/Your%20Reviews%20Activity.png" width=33%>

-----------YourReviewsActivity-----------

## API's
- Google API
- Firebase 
- Facebook API

##Firebase Structure

- Users
  - User ID
    - Name User
    - Profile Picture (?)
    - Friends
      - Friend ID
    - Review 
      - Review ID
        - Restaurant ID
        - Rating
        - Text
  
- Restaurants
  - Restaurant ID
    - Name Restaurant
    - Type Restaurant (?)
    - Review 
      - Review ID
        - User ID
        - Rating
        - Text
    - Checked In
      - User ID
  

