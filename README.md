# End Project Minor

## Problem
When going out and one wishes to go out for diner, it is often a tough choonsing a restaurant. Even when you look up a specific restaurant, the ratings given will most likely be given by people you do not know or gave an anonymous rating. However, when someone you know recommends a specific restaurant, you are more likely to be convinced by their opinion and make a quick decision on where to eat. Therefore, I would like to create an app which uses your geo location to see which (and what kind of) restaurants are near, and by logging in with facebook sees if any of your facebook friends have been there and left a positive review or are even checked in at that moment. 

## Features
To solve this problem, there are a couple of features which are essential for this problem to be solved. First of all, the user needs to be able to log in using facebook. Secondly, the user needs to see what restaurants and their ratings are near and see if any of their friends are checked in. Thirdly, the user needs to be able to check in and out when in a restaurant and give a rating after they have checked out.

## Visual Sketch
a visual sketch of what the application will look like for the user; if you envision the application to have multiple screens, sketch these all out; not in full detail though

## Data sets and sources
The data set that are going to be used will be generated by the users themselves. The reviews will then be saved using the live Firebase database.

## Seperate parts of the application
The first part of the application will be the login part using facebook. Following this, there will be a screen which shows a list of restaurants based on their distance from the user. The user will also then be able to navigate to ... other screens. The user can select a restaurant which opens a new screen which shows information about the restaurant, ratings of friends, friends that are checked in and a small map with the location of the restaurant. In this screen, the user can toggle the checkin/checkout button and the user is prompted to leave a rating after they check out. Furthermore, the user can access an activity which shows their previous reviews.

## External Components
- Google API
- Firebase 
- Facebook API
- Yelp API (optional)

## Technical Problems and how to overcome them
Technical problems which may arise are with using the various API's and the location of the user. I believe that Firebase API should not pose a large problem, seeing as I have worked with it before. The Google API which has to be used to get the location is something that has been done many times before, so I do not believe that this will pose to be a problem either. However, to get the Facebook friends of the user we need to use the Facebook API and this may pose a problem. If I do not get to solve this problem, an option could be for the user to create an account for this application and add friends using their e-mail adresses. 

## Similar Applications
- Trip Advisor
- Restocheck

