package com.nearsoft.nearbooks.models

import android.databinding.ViewDataBinding
import android.text.TextUtils
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.models.realm.Book
import com.nearsoft.nearbooks.models.realm.Borrow
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.util.ErrorUtil
import com.nearsoft.nearbooks.util.ViewUtil
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody
import com.nearsoft.nearbooks.ws.bodies.RequestBody
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse
import com.nearsoft.nearbooks.ws.responses.MessageResponse
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import retrofit2.Response
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Model to handle book actions.
 * Created by epool on 11/18/15.
 */
object BookModel {

    private val sBookService = NearbooksApplication.applicationComponent().provideBookService()

    fun cacheBooks(books: List<Book>?) {
        if (books?.isEmpty() ?: true) return

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { r ->
            r.delete(Book::class.java)
            r.copyToRealmOrUpdate(books)
        }
        realm.close()
    }

    fun findByBookId(bookId: String): Book {
        val realm = Realm.getDefaultInstance()
        val book = realm.where(Book::class.java).equalTo("id", bookId).findFirst()
        realm.close()
        return book
    }

    fun getAllBooks(realm: Realm): RealmResults<Book> {
        return realm.where(Book::class.java).findAll().sort(Book.TITLE, Sort.ASCENDING)
    }

    fun getBooksByQuery(realm: Realm, query: String): RealmResults<Book> {
        if (TextUtils.isEmpty(query)) {
            return getAllBooks(realm)
        }

        return realm.where(Book::class.java).contains(Book.ID, query, Case.INSENSITIVE).or().contains(Book.TITLE, query, Case.INSENSITIVE).or().contains(Book.AUTHOR, query, Case.INSENSITIVE).or().contains(Book.RELEASE_YEAR, query, Case.INSENSITIVE).findAllSorted(Book.TITLE, Sort.ASCENDING)
    }

    fun checkBookAvailability(bookId: String): Observable<Response<AvailabilityResponse>> {
        val observable = sBookService.getBookAvailability(bookId + "-0")
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
    }

    fun requestBookToBorrow(user: User, qrCode: String): Observable<Response<Borrow>> {
        val requestBody = RequestBody()
        requestBody.qrCode = qrCode
        requestBody.userEmail = user.email
        val observable = sBookService.requestBookToBorrow(requestBody)
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
    }

    fun doBookCheckIn(binding: ViewDataBinding, user: User,
                      codeQr: String): Subscription {
        val context = binding.root.context
        val requestBody = RequestBody()
        requestBody.qrCode = codeQr
        requestBody.userEmail = user.email
        val observable = sBookService.checkInBook(requestBody)
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(object : Subscriber<Response<MessageResponse>>() {
            override fun onCompleted() {
            }

            override fun onError(t: Throwable) {
                ViewUtil.showSnackbarMessage(binding, t.message!!)
            }

            override fun onNext(response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    val messageResponse = response.body()
                    ViewUtil.showSnackbarMessage(binding, messageResponse.message!!)
                } else {
                    val messageResponse = ErrorUtil.parseError(MessageResponse::class.java, response)
                    if (messageResponse != null) {
                        ViewUtil.showSnackbarMessage(binding, messageResponse.message!!)
                    } else {
                        ViewUtil.showSnackbarMessage(binding,
                                ErrorUtil.getGeneralExceptionMessage(context, response.code())!!)
                    }
                }
            }
        })
    }

    fun doBookCheckOut(binding: ViewDataBinding, user: User,
                       codeQr: String): Subscription {
        val context = binding.root.context
        val requestBody = RequestBody()
        requestBody.qrCode = codeQr
        requestBody.userEmail = user.email
        val observable = sBookService.checkOutBook(requestBody)
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(object : Subscriber<Response<MessageResponse>>() {
            override fun onCompleted() {
            }

            override fun onError(t: Throwable) {
                ViewUtil.showSnackbarMessage(binding, t.message!!)
            }

            override fun onNext(response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    val messageResponse = response.body()
                    ViewUtil.showSnackbarMessage(binding, messageResponse.message!!)
                } else {
                    val messageResponse = ErrorUtil.parseError(MessageResponse::class.java, response)
                    if (messageResponse != null) {
                        ViewUtil.showSnackbarMessage(binding, messageResponse.message!!)
                    } else {
                        ViewUtil.showSnackbarMessage(binding,
                                ErrorUtil.getGeneralExceptionMessage(context, response.code())!!)
                    }
                }
            }
        })
    }

    fun registerNewBook(
            googleBookBody: GoogleBookBody): Observable<Response<MessageResponse>> {
        return sBookService.registerNewBook(googleBookBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
    }

}
