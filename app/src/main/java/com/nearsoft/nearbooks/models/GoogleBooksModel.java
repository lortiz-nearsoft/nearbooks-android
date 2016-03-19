package com.nearsoft.nearbooks.models;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.exceptions.NearbooksException;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.ws.GoogleBooksService;
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody;
import com.nearsoft.nearbooks.ws.responses.googlebooks.errors.GoogleBooksErrorResponse;

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
                .flatMap(response -> {
                    if (!response.isSuccessful()) {
                        GoogleBooksErrorResponse errorResponse = ErrorUtil
                                .parseError(GoogleBooksErrorResponse.class, response);
                        if (errorResponse != null) {
                            return ErrorUtil
                                    .getGeneralExceptionObservable(
                                            errorResponse.getError().toString()
                                    );
                        } else {
                            return ErrorUtil.getGeneralExceptionObservable(response.code());
                        }
                    }
                    if (response.body().getTotalItems() == 0) {
                        return Observable.error(new NearbooksException("Book not found",
                                R.string.error_book_not_found));
                    }
                    return Observable.from(response.body().getItems());
                })
                .flatMap(volume -> mGoogleBooksService.getVolumeById(volume.getId()))
                .filter(Response::isSuccessful)
                .map(volumeResponse -> new GoogleBookBody(isbn, volumeResponse.body()))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

}
