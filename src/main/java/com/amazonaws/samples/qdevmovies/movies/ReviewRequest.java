package com.amazonaws.samples.qdevmovies.movies;

public class ReviewRequest {
    private String userName;
    private int rating;
    private String comment;

    public ReviewRequest() {
    }

    public ReviewRequest(String userName, int rating, String comment) {
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
