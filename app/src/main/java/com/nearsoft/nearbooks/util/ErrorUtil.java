package com.nearsoft.nearbooks.util;

import android.content.Context;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.exceptions.NearbooksException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Error utilities.
 * Created by epool on 1/12/16.
 */
public class ErrorUtil {

    private static Retrofit sRetrofit = NearbooksApplication.Companion
            .applicationComponent()
            .provideNearbooksRetrofit();

    public static <T> T parseError(Class<T> responseClass, Response<?> response) {
        Converter<ResponseBody, T> converter = sRetrofit
                .responseBodyConverter(responseClass, responseClass.getAnnotations());

        try {
            return converter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String parseError(Response<?> response) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.errorBody()
                    .byteStream()));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getGeneralExceptionMessage(Context context, Object... formatArgs) {
        return getMessageFromThrowable(getGeneralException(formatArgs), context);
    }

    public static String getMessageFromThrowable(Throwable t, Context context) {
        if (t instanceof NearbooksException) {
            return ((NearbooksException) t).getDisplayMessage(context);
        }
        return t.getLocalizedMessage();
    }

    public static NearbooksException getGeneralException(Object... formatArgs) {
        return new NearbooksException("General error", R.string.error_general, formatArgs);
    }

    public static <T> Observable<T> getGeneralExceptionObservable(Object... formatArgs) {
        return Observable.error(getGeneralException(formatArgs));
    }

}
