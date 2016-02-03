package com.nearsoft.nearbooks.models;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.ws.GoogleBooksService;
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Google books model.
 * Created by epool on 2/2/16.
 */
public class GoogleBooksModel {

    private static GoogleBooksService mGoogleBooksService = NearbooksApplication
            .getNearbooksApplicationComponent()
            .provideGoogleBooksService();

    public static Observable<List<GoogleBookBody>> findGoogleBooksByIsbn(String isbn) {
        return mGoogleBooksService.findBooksByIsbn("isbn:" + isbn)
                .filter(Response::isSuccess)
                .map(response -> response.body().getItems())
                .flatMap(Observable::from)
                .flatMap(volume -> mGoogleBooksService.getVolumeById(volume.getId()))
                .filter(Response::isSuccess)
                .map(volumeResponse -> new GoogleBookBody(isbn, volumeResponse.body()))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

}
