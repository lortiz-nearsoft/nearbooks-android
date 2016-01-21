package com.nearsoft.nearbooks.ws;

import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Borrow;
import com.nearsoft.nearbooks.ws.bodies.RequestBody;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Book service.
 * Created by epool on 11/18/15.
 */
public interface BookService {

    @GET("books")
    Observable<List<Book>> getAllBooks();

    @GET("borrows/availability/codeqr/{codeQr}")
    Observable<Response<AvailabilityResponse>> getBookAvailability(@Path("codeQr") String codeQr);

    @POST("borrows/register")
    Observable<Response<Borrow>> requestBookToBorrow(@Body RequestBody requestBody);

    @PUT("borrows/checkin")
    Observable<Response<MessageResponse>> checkInBook(@Body RequestBody requestBody);

    @PUT("borrows/checkout")
    Observable<Response<MessageResponse>> checkOutBook(@Body RequestBody requestBody);

}
