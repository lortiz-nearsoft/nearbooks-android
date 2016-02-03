package com.nearsoft.nearbooks.ws.responses.googlebooks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ReadingModes {

    @SerializedName("text")
    @Expose
    private boolean text;
    @SerializedName("image")
    @Expose
    private boolean image;

    /**
     * @return The text
     */
    public boolean isText() {
        return text;
    }

    /**
     * @param text The text
     */
    public void setText(boolean text) {
        this.text = text;
    }

    /**
     * @return The image
     */
    public boolean isImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(boolean image) {
        this.image = image;
    }

}
