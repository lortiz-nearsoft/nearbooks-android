package com.nearsoft.nearbooks.ws.responses;

import com.google.gson.annotations.SerializedName;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Borrow;

import java.util.Date;

/**
 * Availability response.
 * Created by epool on 1/12/16.
 */
public class AvailabilityResponse {

    @SerializedName("codeQRID")
    private String codeQrId;

    @SerializedName("bookID")
    private String bookId;

    @SerializedName("copyNumber")
    private int copyNumber;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("nextStatus")
    private int nextStatus;

    @SerializedName("initialDateAvailability")
    private Date initialDateAvailability;

    @SerializedName("finalDateAvailability")
    private Date finalDateAvailability;

    @SerializedName("message")
    private String message;

    @SerializedName("book")
    private Book book;

    public String getCodeQrId() {
        return codeQrId;
    }

    public void setCodeQrId(String codeQrId) {
        this.codeQrId = codeQrId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(int copyNumber) {
        this.copyNumber = copyNumber;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Borrow.Status
    public int getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(@Borrow.Status int nextStatus) {
        this.nextStatus = nextStatus;
    }

    public Date getInitialDateAvailability() {
        return initialDateAvailability;
    }

    public void setInitialDateAvailability(Date initialDateAvailability) {
        this.initialDateAvailability = initialDateAvailability;
    }

    public Date getFinalDateAvailability() {
        return finalDateAvailability;
    }

    public void setFinalDateAvailability(Date finalDateAvailability) {
        this.finalDateAvailability = finalDateAvailability;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

}
