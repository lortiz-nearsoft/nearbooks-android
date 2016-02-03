package com.nearsoft.nearbooks.util;

import com.nearsoft.nearbooks.NearbooksApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Error utilities.
 * Created by epool on 1/12/16.
 */
public class ErrorUtil {

    private static Retrofit sRetrofit = NearbooksApplication
            .getNearbooksApplicationComponent()
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

}
