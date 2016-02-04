package com.nearsoft.nearbooks.ws.responses.googlebooks;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Google books search response.
 * Created by epool on 2/2/16.
 */
public class GoogleBooksSearchResponse {

    @SerializedName("kind")
    private String kind;
    @SerializedName("totalItems")
    private int totalItems;
    @SerializedName("items")
    private List<Volume> items;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<Volume> getItems() {
        return items;
    }

    public void setItems(List<Volume> items) {
        this.items = items;
    }
}
