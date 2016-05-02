package com.nearsoft.nearbooks.ws

import com.nearsoft.nearbooks.models.realm.Book
import com.nearsoft.nearbooks.models.realm.Borrow
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody
import com.nearsoft.nearbooks.ws.bodies.RequestBody
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse
import com.nearsoft.nearbooks.ws.responses.MessageResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import rx.Observable

/**
 * Book service.
 * Created by epool on 11/18/15.
 */
interface BookService {

    @GET("books")
    fun getAllBooks(): Call<List<Book>>

    @GET("books")
    fun getAllBooksRx(): Observable<List<Book>>

    // TODO update this endpoint url if necessary after the backend implementation
    @POST("book/new")
    fun registerNewBook(@Body googleBookBody: GoogleBookBody): Observable<Response<MessageResponse>>

    @GET("borrows/availability/codeqr/{codeQr}")
    fun getBookAvailability(@Path("codeQr") codeQr: String): Observable<Response<AvailabilityResponse>>

    @POST("borrows/register")
    fun requestBookToBorrow(@Body requestBody: RequestBody): Observable<Response<Borrow>>

    @PUT("borrows/checkin")
    fun checkInBook(@Body requestBody: RequestBody): Observable<Response<MessageResponse>>

    @PUT("borrows/checkout")
    fun checkOutBook(@Body requestBody: RequestBody): Observable<Response<MessageResponse>>

}
