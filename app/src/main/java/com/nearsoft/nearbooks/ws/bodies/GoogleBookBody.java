package com.nearsoft.nearbooks.ws.bodies;

import com.google.gson.annotations.SerializedName;
import com.nearsoft.nearbooks.ws.responses.googlebooks.Volume;

import java.util.ArrayList;
import java.util.List;

/**
 * Body to create a new book on the server.
 * Created by epool on 2/2/16.
 */
public class GoogleBookBody {

    @SerializedName("isbn")
    private String isbn;
    @SerializedName("title")
    private String title;
    @SerializedName("authors")
    private List<String> authors = new ArrayList<>();
    @SerializedName("publishedDate")
    private String publishedDate;
    @SerializedName("description")
    private String description;
    @SerializedName("thumbnailImageUrl")
    private String thumbnailImageUrl;

    public GoogleBookBody(String isbn, Volume volume) {
        this.isbn = isbn;
        title = volume.getVolumeInfo().getTitle();
        authors = volume.getVolumeInfo().getAuthors();
        publishedDate = volume.getVolumeInfo().getPublishedDate();
        description = volume.getVolumeInfo().getDescription();
        thumbnailImageUrl = volume.getVolumeInfo().getImageLinks().getThumbnail();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

}
