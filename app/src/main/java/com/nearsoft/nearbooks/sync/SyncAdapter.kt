package com.nearsoft.nearbooks.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.models.BookModel
import com.nearsoft.nearbooks.ws.BookService
import java.io.IOException

/**
 * Sync adapter.
 * Created by epool on 12/21/15.
 */
class SyncAdapter : AbstractThreadedSyncAdapter {
    private var mBookService: BookService = NearbooksApplication.applicationComponent().provideBookService()

    constructor(context: Context, autoInitialize: Boolean) : super(context, autoInitialize) {
    }

    constructor(context: Context, autoInitialize: Boolean, allowParallelSyncs: Boolean) : super(context, autoInitialize, allowParallelSyncs) {
    }

    override fun onPerformSync(account: Account, extras: Bundle, authority: String,
                               provider: ContentProviderClient, syncResult: SyncResult) {
        try {
            val response = mBookService.getAllBooks().execute()
            if (response.isSuccessful) {
                val books = response.body()
                BookModel.cacheBooks(books)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            syncResult.stats.numIoExceptions += 1
        }

    }

}
