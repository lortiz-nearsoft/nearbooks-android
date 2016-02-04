package com.nearsoft.nearbooks.ws;

import com.nearsoft.nearbooks.ws.responses.googlebooks.GoogleBooksSearchResponse;
import com.nearsoft.nearbooks.ws.responses.googlebooks.Volume;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Google books service.
 * Created by epool on 2/2/16.
 */
public interface GoogleBooksService {

    @GET("volumes")
    Observable<Response<GoogleBooksSearchResponse>> findBooksByIsbn(@Query("q") String query);

    @GET("volumes/{volumeId}")
    Observable<Response<Volume>> getVolumeById(@Path("volumeId") String volumeId);

}
