package com.example.sagar.trac.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sagar on 2/27/2017.
 */

public class GoogleAdressApiTask extends AsyncTask<String,String,String> {
    Context context;
    String latlog;
    GoogleApiTaskCompleted listener;
    ProgressDialog progressDialog;

    public GoogleAdressApiTask(Context destinationActivity, String latlog, GoogleApiTaskCompleted googleApiTaskCompleted) {
        this.context=destinationActivity;
        this.listener=googleApiTaskCompleted;
        this.latlog=latlog;
    }

    @Override
    protected String doInBackground(String... params) {


        URL url;
        HttpURLConnection urlConnection = null;
   String server_response=" ";
        try {
            url = new URL(latlog);
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                server_response = readStream(urlConnection.getInputStream());
                Log.v("CatalogClient", server_response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
return server_response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    progressDialog= new ProgressDialog(context);
        progressDialog.setMessage("Loading api..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.onFinishApi(s);
        progressDialog.dismiss();
    }
    // Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
