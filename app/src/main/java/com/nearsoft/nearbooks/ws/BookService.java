package com.nearsoft.nearbooks.ws;

import com.nearsoft.nearbooks.models.viewmodels.BookViewModel;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Book service.
 * Created by epool on 11/18/15.
 */
public interface BookService {
    @GET("nearbooks")
    Call<List<BookViewModel>> getAllBooks();
}
