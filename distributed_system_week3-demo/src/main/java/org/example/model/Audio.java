package org.example.model;

public class Audio {
    public boolean equals(Object anObject) {
		return albumTitle.equals(anObject);
	}

	private String id;
    private String artistName;
    private String trackTitle;
    private String albumTitle;
    private int trackNumber;
    private int year;
    private int numberOfReviews;
    private int numofCopiesSold;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public int getNumofCopiesSold() {
        return numofCopiesSold;
    }

    public void setNumofCopiesSold(int numofCopiesSold) {
        this.numofCopiesSold = numofCopiesSold;
    }
}
