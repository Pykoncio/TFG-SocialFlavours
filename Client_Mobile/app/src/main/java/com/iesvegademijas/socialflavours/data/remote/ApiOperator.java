package com.iesvegademijas.socialflavours.data.remote;

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

    private ApiOperator(){}

    public static ApiOperator getInstance()
    {
        synchronized (ApiOperator.class) {
            if (me == null)
            {
                me = new ApiOperator();
            }
        }
        return me;
    }

    public String postText(String url, Map<String, Object> params){
        int cont=0;
        String res=okPostText(url, params);
        while((cont<5)&&(res.equals("error.PIPE"))){
            ++cont;
            res=okPostText(url,params);
        }
        return res;
    }

    private String okPostText(String url, Map<String, Object> params){
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject=new JSONObject();
            for (String pair : params.keySet()) { // Map.Entry<String, Object> entry if the String pair doesnt work properly
                jsonObject.put(pair, params.get(pair));
            }
            RequestBody body = RequestBody.create(jsonObject.toString(),
                    MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return "error.OKHttp";
            } else {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error.PIPE";
        } catch (JSONException e) {
            e.printStackTrace();
            return "error.JSONException";
        }
    }

    public String putText(String url, Map<String, Object> params){
        int cont=0;
        String res=okPutText(url, params);
        while((cont<5)&&(res.equals("error.PIPE"))){
            ++cont;
            res=okPutText(url,params);
        }
        return res;
    }

    private String okPutText(String url, Map<String, Object> params){
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject=new JSONObject();
            for (String pair : params.keySet()) {
                jsonObject.put(pair, params.get(pair));
            }
            RequestBody body = RequestBody.create(jsonObject.toString(),
                    MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return "error.OKHttp";
            } else {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error.PIPE";
        } catch (JSONException e) {
            e.printStackTrace();
            return "error.JSONException";
        }
    }

    public String postText(String url) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return "error.OKHttp";
            } else {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error.PIPE";
        }
    }

    public String okGetString(String myurl, Map<String, String> params){
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();
            for (String pair: params.keySet()) {
                jsonObject.put(pair, params.get(pair));
            }

            RequestBody body = RequestBody.create(jsonObject.toString(),
                    MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(myurl)
                    .put(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()){
                return "error.OKHttp";
            }
            else{
                return response.body().string();
            }
        }
        catch(IOException e){
            e.printStackTrace();
            return "error.IOException";
        }
        catch(JSONException e){
            e.printStackTrace();
            return "error.JSONException";
        }
    }

    public String getString(String myurl){
        int cont=0;
        String res=okGetString(myurl);
        while((cont<5)&&(res.equals("error.IOException"))){
            ++cont;
            res=okGetString(myurl);
        }
        return res;
    }

    public String okGetString(String myurl){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(myurl)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()){
                return "error.OKHttp";
            }
            else{
                return response.body().string();
            }
        }
        catch(IOException e){
            e.printStackTrace();
            return "error.IOException";
        }
    }


    public String deleteTask(String myurl){
        int cont=0;
        String res=okDeleteTask(myurl);
        while((cont<5)&&(res.equals("error.IOException"))){
            ++cont;
            res=okDeleteTask(myurl);
        }
        return res;
    }

    public String okDeleteTask(String myurl){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .delete()
                    .url(myurl)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()){
                return "error.OKHttp";
            }
            else{
                return response.body().string();
            }
        }
        catch(IOException e){
            e.printStackTrace();
            return "error.IOException";
        }
    }


}
