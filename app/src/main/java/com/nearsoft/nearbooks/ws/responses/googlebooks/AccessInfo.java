package com.nearsoft.nearbooks.ws.responses.googlebooks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class AccessInfo {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("viewability")
    @Expose
    private String viewability;
    @SerializedName("embeddable")
    @Expose
    private boolean embeddable;
    @SerializedName("publicDomain")
    @Expose
    private boolean publicDomain;
    @SerializedName("textToSpeechPermission")
    @Expose
    private String textToSpeechPermission;
    @SerializedName("epub")
    @Expose
    private Epub epub;
    @SerializedName("pdf")
    @Expose
    private Pdf pdf;
    @SerializedName("webReaderLink")
    @Expose
    private String webReaderLink;
    @SerializedName("accessViewStatus")
    @Expose
    private String accessViewStatus;
    @SerializedName("quoteSharingAllowed")
    @Expose
    private boolean quoteSharingAllowed;

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
     * @return The viewability
     */
    public String getViewability() {
        return viewability;
    }

    /**
     * @param viewability The viewability
     */
    public void setViewability(String viewability) {
        this.viewability = viewability;
    }

    /**
     * @return The embeddable
     */
    public boolean isEmbeddable() {
        return embeddable;
    }

    /**
     * @param embeddable The embeddable
     */
    public void setEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
    }

    /**
     * @return The publicDomain
     */
    public boolean isPublicDomain() {
        return publicDomain;
    }

    /**
     * @param publicDomain The publicDomain
     */
    public void setPublicDomain(boolean publicDomain) {
        this.publicDomain = publicDomain;
    }

    /**
     * @return The textToSpeechPermission
     */
    public String getTextToSpeechPermission() {
        return textToSpeechPermission;
    }

    /**
     * @param textToSpeechPermission The textToSpeechPermission
     */
    public void setTextToSpeechPermission(String textToSpeechPermission) {
        this.textToSpeechPermission = textToSpeechPermission;
    }

    /**
     * @return The epub
     */
    public Epub getEpub() {
        return epub;
    }

    /**
     * @param epub The epub
     */
    public void setEpub(Epub epub) {
        this.epub = epub;
    }

    /**
     * @return The pdf
     */
    public Pdf getPdf() {
        return pdf;
    }

    /**
     * @param pdf The pdf
     */
    public void setPdf(Pdf pdf) {
        this.pdf = pdf;
    }

    /**
     * @return The webReaderLink
     */
    public String getWebReaderLink() {
        return webReaderLink;
    }

    /**
     * @param webReaderLink The webReaderLink
     */
    public void setWebReaderLink(String webReaderLink) {
        this.webReaderLink = webReaderLink;
    }

    /**
     * @return The accessViewStatus
     */
    public String getAccessViewStatus() {
        return accessViewStatus;
    }

    /**
     * @param accessViewStatus The accessViewStatus
     */
    public void setAccessViewStatus(String accessViewStatus) {
        this.accessViewStatus = accessViewStatus;
    }

    /**
     * @return The quoteSharingAllowed
     */
    public boolean isQuoteSharingAllowed() {
        return quoteSharingAllowed;
    }

    /**
     * @param quoteSharingAllowed The quoteSharingAllowed
     */
    public void setQuoteSharingAllowed(boolean quoteSharingAllowed) {
        this.quoteSharingAllowed = quoteSharingAllowed;
    }

}
