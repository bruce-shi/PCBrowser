package com.jupiter.Service;

import com.loopj.android.http.*;

/**
 * Created by lovew_000 on 2015/5/6.
 */
public class JupiterHttpClient {

    private static final String BASE_URL = "http://lovejupiter.me:8000/";

    private static SyncHttpClient client = new SyncHttpClient();

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
