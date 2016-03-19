package com.nearsoft.nearbooks.models;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.text.TextUtils;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Book_Table;
import com.nearsoft.nearbooks.models.sqlite.Borrow;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.ws.BookService;
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody;
import com.nearsoft.nearbooks.ws.bodies.RequestBody;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

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

    private static BookService mBookService = NearbooksApplication
            .getNearbooksApplicationComponent()
            .providesBookService();

    public static void cacheBooks(final List<Book> books) {
        if (books == null || books.isEmpty()) return;

        TransactionManager.transact(NearbooksDatabase.NAME, () -> {
            Delete.table(Book.class);

            for (Book book : books) {
                book.save();
            }
        });
    }

    public static Book findByBookId(String bookId) {
        return SQLite
                .select()
                .from(Book.class)
                .where(Book_Table.id.eq(bookId))
                .querySingle();
    }

    public static Where<Book> getAllBooks() {
        return SQLite
                .select()
                .from(Book.class)
                .orderBy(Book_Table.title, true);
    }

    public static Where<Book> getBooksByQuery(CharSequence charSequenceQuery) {
        if (TextUtils.isEmpty(charSequenceQuery)) {
            return getAllBooks();
        }

        String query = "%" + charSequenceQuery.toString() + "%";

        return SQLite
                .select()
                .from(Book.class)
                .where(Book_Table.id.like(query))
                .or(Book_Table.title.like(query))
                .or(Book_Table.author.like(query))
                .or(Book_Table.releaseYear.like(query))
                .orderBy(Book_Table.title, true);
    }

    public static Observable<Response<AvailabilityResponse>> checkBookAvailability(Book book) {
        Observable<Response<AvailabilityResponse>> observable
                = mBookService.getBookAvailability(book.getId() + "-0");
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
