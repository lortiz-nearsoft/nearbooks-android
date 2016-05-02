package com.nearsoft.nearbooks.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.ws.BookService;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

/**
 * Sync adapter.
 * Created by epool on 12/21/15.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private BookService mBookService;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mBookService = NearbooksApplication.Companion
                .applicationComponent()
                .providesBookService();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mBookService = NearbooksApplication.Companion
                .applicationComponent()
                .providesBookService();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, final SyncResult syncResult) {
        try {
            Response<List<Book>> response = mBookService.getAllBooks().execute();
            if (response.isSuccessful()) {
                List<Book> books = response.body();
                BookModel.cacheBooks(books);
            }
        } catch (IOException e) {
            e.printStackTrace();
            syncResult.stats.numIoExceptions += 1;
        }
    }

}
