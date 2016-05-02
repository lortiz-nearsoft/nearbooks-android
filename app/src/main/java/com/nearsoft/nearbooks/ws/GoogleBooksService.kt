package com.nearsoft.nearbooks.ws

import com.nearsoft.nearbooks.ws.responses.googlebooks.GoogleBooksSearchResponse
import com.nearsoft.nearbooks.ws.responses.googlebooks.Volume

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Google books service.
 * Created by epool on 2/2/16.
 */
interface GoogleBooksService {

    @GET("volumes")
    fun findBooksByIsbn(@Query("q") query: String): Observable<Response<GoogleBooksSearchResponse>>

    @GET("volumes/{volumeId}")
    fun getVolumeById(@Path("volumeId") volumeId: String): Observable<Response<Volume>>

}
