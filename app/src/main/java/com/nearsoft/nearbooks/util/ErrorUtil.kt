package com.nearsoft.nearbooks.util

import android.content.Context
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.exceptions.NearbooksException
import retrofit2.Response
import rx.Observable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Error utilities.
 * Created by epool on 1/12/16.
 */
object ErrorUtil {

    private val sRetrofit = NearbooksApplication.applicationComponent().provideNearbooksRetrofit()

    fun <T> parseError(responseClass: Class<T>, response: Response<*>): T? {
        val converter = sRetrofit.responseBodyConverter<T>(responseClass, responseClass.annotations)

        try {
            return converter.convert(response.errorBody())
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    fun parseError(response: Response<*>): String? {
        try {
            val reader = BufferedReader(InputStreamReader(response.errorBody().byteStream()))
            val out = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                out.append(line)
                line = reader.readLine()
            }
            return out.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    fun getGeneralExceptionMessage(context: Context, vararg formatArgs: Any): String? {
        return getMessageFromThrowable(getGeneralException(*formatArgs), context)
    }

    fun getMessageFromThrowable(t: Throwable, context: Context): String? {
        if (t is NearbooksException) {
            return t.getDisplayMessage(context)
        }
        return t.message
    }

    fun getGeneralException(vararg formatArgs: Any): NearbooksException {
        return NearbooksException("General error", R.string.error_general, *formatArgs)
    }

    fun <T> getGeneralExceptionObservable(vararg formatArgs: Any): Observable<T> {
        return Observable.error<T>(getGeneralException(*formatArgs))
    }

}
