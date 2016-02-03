package com.nearsoft.nearbooks.ws.responses.googlebooks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class SaleInfo {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("saleability")
    @Expose
    private String saleability;
    @SerializedName("isEbook")
    @Expose
    private boolean isEbook;

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The saleability
     */
    public String getSaleability() {
        return saleability;
    }

    /**
     * @param saleability The saleability
     */
    public void setSaleability(String saleability) {
        this.saleability = saleability;
    }

    /**
     * @return The isEbook
     */
    public boolean isIsEbook() {
        return isEbook;
    }

    /**
     * @param isEbook The isEbook
     */
    public void setIsEbook(boolean isEbook) {
        this.isEbook = isEbook;
    }

}
