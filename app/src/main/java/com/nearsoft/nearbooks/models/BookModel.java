package com.nearsoft.nearbooks.models;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Book_Table;
import com.nearsoft.nearbooks.models.sqlite.Borrow;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.ws.BookService;
import com.nearsoft.nearbooks.ws.bodies.RequestBody;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        TransactionManager.transact(NearbooksDatabase.NAME, new Runnable() {
            @Override
            public void run() {
                Delete.table(Book.class);

                for (Book book : books) {
                    book.save();
                }
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

    public static Call<AvailabilityResponse> checkBookAvailability(
            Book book, Callback<AvailabilityResponse> callback) {
        Call<AvailabilityResponse> call = mBookService.getBookAvailability(book.getId() + "-0");
        call.enqueue(callback);
        return call;
    }

    public static Call<Borrow> requestBookToBorrow(final ViewDataBinding binding, User user,
                                                   String qrCode, final FloatingActionButton fabToHide) {
        final Context context = binding.getRoot().getContext();
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode(qrCode);
        requestBody.setUserEmail(user.getEmail());
        Call<Borrow> call = mBookService.requestBookToBorrow(requestBody);
        call.enqueue(new Callback<Borrow>() {
            @Override
            public void onResponse(Response<Borrow> response) {
                if (response.isSuccess()) {
                    Borrow borrow = response.body();
                    switch (borrow.getStatus()) {
                        case Borrow.STATUS_REQUESTED:
                            ViewUtil.showSnackbarMessage(binding,
                                    context.getString(R.string.message_book_requested));
                            break;
                        case Borrow.STATUS_ACTIVE:
                            ViewUtil.showSnackbarMessage(binding,
                                    context.getString(R.string.message_book_active));
                            break;
                        case Borrow.STATUS_CANCELLED:
                        case Borrow.STATUS_COMPLETED:
                        default:
                            break;
                    }
                    if (fabToHide != null) {
                        fabToHide.hide();
                    }
                } else {
                    MessageResponse messageResponse = ErrorUtil.parseError(MessageResponse.class,
                            response);
                    if (messageResponse != null) {
                        ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                    } else {
                        ViewUtil.showSnackbarMessage(binding,
                                context.getString(R.string.error_general,
                                        String.valueOf(response.code())));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ViewUtil.showSnackbarMessage(binding, t.getLocalizedMessage());
            }
        });
        return call;
    }

    public static Call<MessageResponse> doBookCheckIn(final ViewDataBinding binding, User user,
                                                      String codeQr) {
        final Context context = binding.getRoot().getContext();
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode(codeQr);
        requestBody.setUserEmail(user.getEmail());
        Call<MessageResponse> call = mBookService.checkInBook(requestBody);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Response<MessageResponse> response) {
                if (response.isSuccess()) {
                    MessageResponse messageResponse = response.body();
                    ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                } else {
                    MessageResponse messageResponse = ErrorUtil.parseError(MessageResponse.class,
                            response);
                    if (messageResponse != null) {
                        ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                    } else {
                        ViewUtil.showSnackbarMessage(binding,
                                context.getString(R.string.error_general,
                                        String.valueOf(response.code())));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ViewUtil.showSnackbarMessage(binding, t.getLocalizedMessage());
            }
        });
        return call;
    }

    public static Call<MessageResponse> doBookCheckOut(final ViewDataBinding binding, User user,
                                                       String codeQr) {
        final Context context = binding.getRoot().getContext();
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode(codeQr);
        requestBody.setUserEmail(user.getEmail());
        Call<MessageResponse> call = mBookService.checkOutBook(requestBody);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Response<MessageResponse> response) {
                if (response.isSuccess()) {
                    MessageResponse messageResponse = response.body();
                    ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                } else {
                    MessageResponse messageResponse = ErrorUtil.parseError(MessageResponse.class,
                            response);
                    if (messageResponse != null) {
                        ViewUtil.showSnackbarMessage(binding, messageResponse.getMessage());
                    } else {
                        ViewUtil.showSnackbarMessage(binding,
                                context.getString(R.string.error_general,
                                        String.valueOf(response.code())));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ViewUtil.showSnackbarMessage(binding, t.getLocalizedMessage());
            }
        });
        return call;
    }

}
