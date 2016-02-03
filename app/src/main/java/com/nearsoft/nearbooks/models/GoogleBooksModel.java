package com.nearsoft.nearbooks.models;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.ws.GoogleBooksService;
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody;
import com.nearsoft.nearbooks.ws.responses.googlebooks.GoogleBooksSearchResponse;
import com.nearsoft.nearbooks.ws.responses.googlebooks.Volume;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
                .filter(new Func1<Response<GoogleBooksSearchResponse>, Boolean>() {
                    @Override
                    public Boolean call(Response<GoogleBooksSearchResponse> response) {
                        return response.isSuccess();
                    }
                })
                .map(new Func1<Response<GoogleBooksSearchResponse>, List<Volume>>() {
                    @Override
                    public List<Volume> call(Response<GoogleBooksSearchResponse> response) {
                        return response.body().getItems();
                    }
                })
                .flatMap(new Func1<List<Volume>, Observable<Volume>>() {
                    @Override
                    public Observable<Volume> call(List<Volume> volumes) {
                        return Observable.from(volumes);
                    }
                })
                .flatMap(new Func1<Volume, Observable<Response<Volume>>>() {
                    @Override
                    public Observable<Response<Volume>> call(final Volume volume) {
                        return mGoogleBooksService.getVolumeById(volume.getId());
                    }
                })
                .filter(new Func1<Response<Volume>, Boolean>() {
                    @Override
                    public Boolean call(Response<Volume> volumeResponse) {
                        return volumeResponse.isSuccess();
                    }
                })
                .map(new Func1<Response<Volume>, GoogleBookBody>() {
                    @Override
                    public GoogleBookBody call(Response<Volume> volumeResponse) {
                        return new GoogleBookBody(volumeResponse.body());
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

}
