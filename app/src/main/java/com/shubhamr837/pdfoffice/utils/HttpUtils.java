package com.shubhamr837.pdfoffice.utils;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class HttpUtils {
    private static String idToken;
    private static class GetJSONRequestAsyncTasks extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(URL... urls) {
            if (urls.length > 0) {
                URL url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url.toString());
                httpget.setHeader("auth-token", idToken );
                try {
                    HttpResponse response = httpClient.execute(httpget);
                    if (response != null) {
                        //If status is OK 200
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String result = EntityUtils.toString(response.getEntity());
                            //Convert the string result to a JSON Object
                            return new JSONObject(result);
                        }
                    }
                } catch (IOException e) {

                } catch (JSONException e) {
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject data) {
            if (data != null) {


            }


        }

    }
    public void downloadFile(URL url,String type){


        idToken = FirebaseUtils.getToken();
        if(idToken==null){
            return;
        }

        GetJSONRequestAsyncTasks getJSONRequestAsyncTasks = new GetJSONRequestAsyncTasks();
        getJSONRequestAsyncTasks.execute(url);


    }
}
