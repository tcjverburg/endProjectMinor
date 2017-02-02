##Monday 09-01
Decided on what kind of application I wanted to make and what features I wish to include. Furthermore I discussed with several of my
class mates what I could further implement or do to make the application more convenient. Based on this new information,I wrote the
proposal of my application with a couple of sketches of how I imagine the application to look like.

##Tuesday 10-01
After having received feedback, I came to the conclusion that my app mainly depends on the ability to get friends who use the application.
This is easiest when the application would import all of your facebook friends, and thus I have decided to focus today on figuring out how this works. At the end of the day, I had connected Firebase to Facebook and my project.

##Wednesday 11-01
Today I tried going through several tutorials and seeing how you implement logging in with facebook and getting the friends list. As of 
now, I am able to log in using facebook but I am still having trouble with getting the friends list.. It seems as if Facebook only allows the names of friends who also use the application so my plan is to make a new facebook account to test this.

##Thursday 12-01
For today, I had the goal to finish my technical design, finish the prototype and start with the Firebase database. All of this has been accomplished but I am only now starting to realise the scope of the app I want to make, especially since I haven't even looked at the Google Maps API.

##Friday 13-01
This morning we had the presentations of our prototype. Based on feedback, I will continue with the design I have so far, except for the incheck option. According to the observers, this feature will probably cost me too much time.

##Weekend 14-01 and 15-01
In the weekend I made connection to the Google Places API and the Google Maps API and implemented them into my application. Still needs a lot of work concerning the query but the start has been made. Based on my research, instead of presenting a list of possible restaurants, I want to show a map fragment and make it possible for the user to click on a specific restaurant. In turn, no map fragment will be shown in the next activity to create more space in the layout.

##Monday 16-01
Today I was able to succesfully implement the Google Places API and get all the necessay information to connect the users for the Firebase Database. My plan for tomorrow is to try and save the first reviews and connect them to specific restaurants and users(writers of the review). 

##Tuesday 17-01
Today I did not have that much time to work on the project, seeing as I had a lot of trouble getting to Amsterdam. However, I was able to save more data to Firebase and plan to find a way to read this data tomorrow. 

##Wednesday 18-01
The plan for today was to finish the alpha seeing as the only real challenge ahead was reading the Firebase data and I succeeded in this. For tomorrow I do not have anything planned, except finishing the style sheet with my group.

##Thursday 19-01
Today I realized the main concept of the app which I wanted to make. However, seeing as I still have a couple of weeks to go, I wanted to add some extra features to expand my application. Seeing as the "Friends" tab doesnt have any functionality except showing a list of friends, I thought that it might be interesting to turn this tab into an activity wall feed, so the user can see what their friends are doing. Furthermore I was able to implement a checkin/checkout button to the selected restaurant tab. 

##Friday 20-01
THe presentation was a lot easier this week, seeing as I had a lot more to present, adding to the fact that my applications functioninality is near completion. For this weekend, the plan is to be able to read and write the activity of individual users to Firebase, using a timestamp so that friends can see what you are doing on a day to day basis. The activities I wish to follow are checking in and writing a review.

##Weekend 21-01 and 22-01
In this weekend I did not really work a lot on the application, seeing as I was quite ahead of schedule compared to several classmates. Still, I did think a lot about how to structure my Firebase Database to write and read the new data.

##Monday 23-01
Today I really struggled with the Firebase database with how I wanted to save all the data, seeing as I wanted to have as little as possible that there was the same data in different places in my database. Furthermore, the async tasks of the Firebase datasnapshots are giving me a hard time.

##Tuesday 24-01
Seeing as I was ill today, I did not do anything significant to the application. 

##Wednesday 25-01
At the end of the day I had cleaned up a lot of visual aspects of the application, and continued to work on the activity feed. In the end, I was able to finish being able to being able to get whether a friend wrote a review that day. Tomorrow, I will look at the checkin part.

##Thursday 26-01
All is done, minimal viable product is there but I still wish to add a few list adapters to make it visually more pleasing.

##Friday 27-01
Today we did the presentations, and I got some feedback on my UI and whether I should still implement certain features that day. I suggested that I still wanted to make a custom list adapter for my SelectedRestaurantActivity to show a ratingbar and the name of the writer of the review, but I was pushed to finish this as soon as possible. Furthermore I need to give the user more information on how the app works to improve the user experience.

##Weekend 28-01 and 29-01
This weekend I didn't really work on the application, except for looking into how to make a custom list adapter.

##Monday 30-01
Today I decided was the last day I wanted to write new code to make everything smoother. Therefore I finished the custom list adapter and started cleaning up my code. However, the activity feed was still giving me trouble, seeing as it was quite challenging to get both the checkins and the writing of review activities in a single listview. I have it sort of working at the moment, but I need to clean out a couple of buggs. I also reached my API quota for the Google Places API Webservice today which caused me a lot of stress. The reason for this, is because I wanted to implement a feature so that the locationrequest would continue to query for new restaurants around the user. However, seeing as it fired every time the user location changed a little, I reached 1000 requests in a manner of minutes. This is also the reason why I decided against implementing this.

##Tuesday 31-01
Time is not playing a large role and I need to push myself to wrap things up. Today I continued on cleaning up some of the code and got some more bugs out concerning the activity feed. Furthermore, I worked on some of the state restoration and the entire navigation throughout the application. 

##Wednesday 01-02
Today the real refractoring started for me. I redivided all the activities in several packages so that the activities would be more ordened. Furthermore I made many of my methods a lot shorter so that I would get a higher rating on bettercodehub. And last but not least, I added all my comments.

##Thursday 02-02





