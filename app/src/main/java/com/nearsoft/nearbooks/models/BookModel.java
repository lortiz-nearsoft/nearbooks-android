package com.nearsoft.nearbooks.models;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.text.TextUtils;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.models.realm.Borrow;
import com.nearsoft.nearbooks.models.view.User;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.ws.BookService;
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody;
import com.nearsoft.nearbooks.ws.bodies.RequestBody;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Model to handle book actions.
 * Created by epool on 11/18/15.
 */
public class BookModel {

    private static BookService mBookService = NearbooksApplication.Companion
            .applicationComponent()
            .providesBookService();

    public static void cacheBooks(final List<Book> books) {
        if (books == null || books.isEmpty()) return;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            r.delete(Book.class);
            r.copyToRealmOrUpdate(books);
        });
        realm.close();
    }

    public static Book findByBookId(String bookId) {
        Realm realm = Realm.getDefaultInstance();
        Book book = realm.where(Book.class)
                .equalTo("id", bookId)
                .findFirst();
        realm.close();
        return book;
    }

    public static RealmResults<Book> getAllBooks(Realm realm) {
        return realm.allObjects(Book.class).sort(Book.TITLE, Sort.ASCENDING);
    }

    public static RealmResults<Book> getBooksByQuery(Realm realm, String query) {
        if (TextUtils.isEmpty(query)) {
            return getAllBooks(realm);
        }

        return realm.where(Book.class)
                .contains(Book.ID, query, Case.INSENSITIVE)
                .or()
                .contains(Book.TITLE, query, Case.INSENSITIVE)
                .or()
                .contains(Book.AUTHOR, query, Case.INSENSITIVE)
                .or()
                .contains(Book.RELEASE_YEAR, query, Case.INSENSITIVE)
                .findAllSorted(Book.TITLE, Sort.ASCENDING);
    }

    public static Observable<Response<AvailabilityResponse>> checkBookAvailability(String bookId) {
        Observable<Response<AvailabilityResponse>> observable
                = mBookService.getBookAvailability(bookId + "-0");
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Observable<Response<Borrow>> requestBookToBorrow(User user, String qrCode) {
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode(qrCode);
        requestBody.setUserEmail(user.getEmail());
        Observable<Response<Borrow>> observable = mBookService.requestBookToBorrow(requestBody);
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Subscription doBookCheckIn(final ViewDataBinding binding, User user,
                                             String codeQr) {
        final Context context = binding.getRoot().getContext();
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode(codeQr);
        requestBody.setUserEmail(user.getEmail());
        Observable<Response<MessageResponse>> observable = mBookService.checkInBook(requestBody);
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response<MessageResponse>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable t) {
                        ViewUtil.showSnackbarMessage(binding, t.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Response<MessageResponse> response) {
                        if (response.isSuccessful()) {
                            MessageResponse messageResponse = response.body();
                            ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                        } else {
                            MessageResponse messageResponse = ErrorUtil
                                    .parseError(MessageResponse.class, response);
                            if (messageResponse != null) {
                                ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                            } else {
                                ViewUtil.showSnackbarMessage(binding,
                                        ErrorUtil.getGeneralExceptionMessage(context,
                                                response.code()));
                            }
                        }
                    }
                });
    }

    public static Subscription doBookCheckOut(final ViewDataBinding binding, User user,
                                              String codeQr) {
        final Context context = binding.getRoot().getContext();
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode(codeQr);
        requestBody.setUserEmail(user.getEmail());
        Observable<Response<MessageResponse>> observable = mBookService.checkOutBook(requestBody);
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Response<MessageResponse>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable t) {
                        ViewUtil.showSnackbarMessage(binding, t.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Response<MessageResponse> response) {
                        if (response.isSuccessful()) {
                            MessageResponse messageResponse = response.body();
                            ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                        } else {
                            MessageResponse messageResponse = ErrorUtil
                                    .parseError(MessageResponse.class, response);
                            if (messageResponse != null) {
                                ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                            } else {
                                ViewUtil.showSnackbarMessage(binding,
                                        ErrorUtil.getGeneralExceptionMessage(context,
                                                response.code()));
                            }
                        }
                    }
                });
    }

    public static Observable<Response<MessageResponse>> registerNewBook(
            GoogleBookBody googleBookBody) {
        return mBookService.registerNewBook(googleBookBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

}
