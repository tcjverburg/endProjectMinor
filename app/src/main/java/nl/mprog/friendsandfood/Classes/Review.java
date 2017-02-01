package nl.mprog.friendsandfood.Classes;

/**
 * Created by Tom Verburg on 27-1-2017.
 * This class
 */

public class Review {
    private String writer;
    private String reviewID;
    private Float rating;

    public Review(String writer, String reviewID, Float rating) {
        this.writer = writer;
        this.reviewID = reviewID;
        this.rating = rating;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}