package com.nearsoft.nearbooks.models

import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.exceptions.NearbooksException
import com.nearsoft.nearbooks.util.ErrorUtil
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody
import com.nearsoft.nearbooks.ws.responses.googlebooks.Volume
import com.nearsoft.nearbooks.ws.responses.googlebooks.errors.GoogleBooksErrorResponse
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Google books model.
 * Created by epool on 2/2/16.
 */
object GoogleBooksModel {

    private val mGoogleBooksService = NearbooksApplication.applicationComponent.provideGoogleBooksService()

    fun findGoogleBooksByIsbn(isbn: String): Observable<List<GoogleBookBody>> {
        return mGoogleBooksService.findBooksByIsbn("isbn:" + isbn)
                .flatMap {
                    if (!it.isSuccessful) {
                        val errorResponse = ErrorUtil.parseError(GoogleBooksErrorResponse::class.java, it);
                        if (errorResponse != null) {
                            return@flatMap ErrorUtil.getGeneralExceptionObservable<Volume>(errorResponse.error.toString());
                        } else {
                            return@flatMap ErrorUtil.getGeneralExceptionObservable<Volume>(it.code());
                        }
                    }
                    if (it.body().totalItems == 0) {
                        return@flatMap Observable.error<Volume>(NearbooksException("Book not found", R.string.error_book_not_found));
                    }
                    return@flatMap Observable.from<Volume>(it.body().items);
                }
                .flatMap { mGoogleBooksService.getVolumeById(it.id) }
                .filter { it.isSuccessful }
                .map { GoogleBookBody(isbn, it.body()) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

}
