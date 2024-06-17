package com.iesvegademijas.socialflavours.data.remote;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiOperator {

    private static ApiOperator me = null;
    private static final String TAG = "ApiOperatorTag"; // Log tag

    private ApiOperator(){}

    public static ApiOperator getInstance() {
        synchronized (ApiOperator.class) {
            if (me == null) {
                me = new ApiOperator();
            }
        }
        return me;
    }

    public String postText(String url, Map<String, Object> params) {
        int cont = 0;
        String res = okPostText(url, params);
        while ((cont < 5) && res.equals("error.PIPE")) {
            ++cont;
            res = okPostText(url, params);
        }
        Log.d(TAG, "postText response: " + res);
        return res;
    }

    private String okPostText(String url, Map<String, Object> params) {
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();

            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value instanceof List) {
                    jsonObject.put(key, new JSONArray((List) value));
                } else {
                    jsonObject.put(key, value);
                }
            }

            RequestBody body = RequestBody.create(jsonObject.toString(),
                    MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e(TAG, "okPostText error: " + response.code());
                return "error.OKHttp";
            } else {
                String result = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "okPostText result: " + result);
                return result;
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException in okPostText", e);
            return "error.PIPE";
        } catch (JSONException e) {
            Log.e(TAG, "JSONException in okPostText", e);
            return "error.JSONException";
        }
    }

    public String putText(String url, Map<String, Object> params) {
        int cont = 0;
        String res = okPutText(url, params);
        while ((cont < 5) && res.equals("error.PIPE")) {
            ++cont;
            res = okPutText(url, params);
        }
        Log.d(TAG, "putText response: " + res);
        return res;
    }

    private String okPutText(String url, Map<String, Object> params) {
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();

            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value instanceof List) {
                    jsonObject.put(key, new JSONArray((List) value));
                } else {
                    jsonObject.put(key, value);
                }
            }

            RequestBody body = RequestBody.create(jsonObject.toString(),
                    MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e(TAG, "okPutText error: " + response.code());
                return "error.OKHttp";
            } else {
                String result = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "okPutText result: " + result);
                return result;
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException in okPutText", e);
            return "error.PIPE";
        } catch (JSONException e) {
            Log.e(TAG, "JSONException in okPutText", e);
            return "error.JSONException";
        }
    }

    public String putText(String url) {
        int cont = 0;
        String res = okPutText(url);
        while ((cont < 5) && res.equals("error.PIPE")) {
            ++cont;
            res = okPutText(url);
        }
        Log.d(TAG, "putText response: " + res);
        return res;
    }

    private String okPutText(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create("",
                    MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e(TAG, "okPutText error: " + response.code());
                return "error.OKHttp";
            } else {
                String result = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "okPutText result: " + result);
                return result;
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException in okPutText", e);
            return "error.PIPE";
        }
    }

    public String postText(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create("",
                    MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e(TAG, "postText error: " + response.code());
                return "error.OKHttp";
            } else {
                String result = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "postText result: " + result);
                return result;
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException in postText", e);
            return "error.PIPE";
        }
    }
    public String getString(String myurl) {
        int cont = 0;
        String res = okGetString(myurl);
        while ((cont < 5) && res.equals("error.IOException")) {
            ++cont;
            res = okGetString(myurl);
        }
        Log.d(TAG, "getString response: " + res);
        return res;
    }

    public String okGetString(String myurl) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(myurl)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e(TAG, "okGetString error: " + response.code());
                return "error.OKHttp";
            } else {
                String result = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "okGetString result: " + result);
                return result;
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException in okGetString", e);
            return "error.IOException";
        }
    }

    public String deleteTask(String myurl) {
        int cont = 0;
        String res = okDeleteTask(myurl);
        while ((cont < 5) && res.equals("error.IOException")) {
            ++cont;
            res = okDeleteTask(myurl);
        }
        Log.d(TAG, "deleteTask response: " + res);
        return res;
    }

    public String okDeleteTask(String myurl) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .delete()
                    .url(myurl)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e(TAG, "okDeleteTask error: " + response.code());
                return "error.OKHttp";
            } else {
                String result = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "okDeleteTask result: " + result);
                return result;
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException in okDeleteTask", e);
            return "error.IOException";
        }
    }
}
