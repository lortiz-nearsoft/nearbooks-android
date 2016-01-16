package com.nearsoft.nearbooks.ws;

import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Borrow;
import com.nearsoft.nearbooks.ws.bodies.RequestBody;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Book service.
 * Created by epool on 11/18/15.
 */
public interface BookService {

    @GET("books")
    Call<List<Book>> getAllBooks();

    @GET("borrows/availability/codeqr/{codeQr}")
    Call<AvailabilityResponse> getBookAvailability(@Path("codeQr") String codeQr);

    @POST("borrows/register")
    Call<Borrow> requestBookToBorrow(@Body RequestBody requestBody);

    @PUT("borrows/checkin")
    Call<MessageResponse> checkInBook(@Body RequestBody requestBody);

    @PUT("borrows/checkout")
    Call<MessageResponse> checkOutBook(@Body RequestBody requestBody);

}
