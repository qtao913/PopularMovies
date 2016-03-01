package com.sunnietech.hotflicks.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qlzh727 on 12/14/15.
 */
public class Movie implements Parcelable{
    int imageID;
    String originalTitle;
    String imagePath;
    String synopsis;
    double rating;
    String releaseDate;

    public Movie(int imageID, String originalTitle, String imagePath, String synopsis, double rating, String releaseDate) {
        this.imageID = imageID;
        this.originalTitle = originalTitle;
        this.imagePath = imagePath;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        imageID = in.readInt();
        originalTitle = in.readString();
        imagePath = in.readString();
        synopsis = in.readString();
        rating = in.readDouble();
        releaseDate = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //It doesn't matter which order to write the objects, so long as read them back in in the same order
        dest.writeInt(imageID);
        dest.writeString(originalTitle);
        dest.writeString(imagePath);
        dest.writeString(synopsis);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
