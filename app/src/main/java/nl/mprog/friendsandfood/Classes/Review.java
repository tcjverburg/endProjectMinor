package nl.mprog.friendsandfood.Classes;

/**
 * Created by Tom Verburg on 27-1-2017.
 * This is the Review class which creates a Review Object to be used in the listView
 * of the SelectedRestaurantActivity.
 */

public class Review {
    private String title;
    private String reviewID;
    private Float rating;

    /**Constructor of the Review class.*/
    public Review(String writer, String reviewID, Float rating) {
        this.title = writer;
        this.reviewID = reviewID;
        this.rating = rating;
    }

    /**All the getters of the Review class.*/
    public String getTitle() {
        return title;
    }

    public String getReviewID() {
        return reviewID;
    }

    public Float getRating() {
        return rating;
    }

}
