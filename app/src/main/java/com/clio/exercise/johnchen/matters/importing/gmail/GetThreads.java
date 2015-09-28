package com.clio.exercise.johnchen.matters.importing.gmail;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.clio.exercise.johnchen.matters.MatterContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 2015/9/28.
 */
public class GetThreads extends AsyncTask<String, String, JSONObject> {
    private final TextView mResult;
    private ProgressDialog pDialog;
    private Context mContext;
    private String mAccessToken;
    private String GET_THREAD_URL = "https://www.googleapis.com/gmail/v1/users/me/threads/15012cfe93a6bc42?format=metadata&metadataHeaders=From&fields=messages%2Fpayload";
    public GetThreads(Context context, String accessToken, TextView result) {
        mContext = context;
        mAccessToken = accessToken;
        mResult = result;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Contacting Google ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        InputStream is = null;
        String json = "{}";
        try {
            URL u = new URL("https://www.googleapis.com/gmail/v1/users/me/threads?labelIds=CATEGORY_PERSONAL&maxResults=20");
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", "Bearer " + mAccessToken);
            conn.connect();
            is = conn.getInputStream();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();

            json = sb.toString();
            Log.e("JSONStr", json);
        } catch (Exception e) {
            e.getMessage();
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // Parse the String to a JSON Object
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // Return JSON String
        return jObj;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        pDialog.dismiss();
        if (json != null) {
            try {
                JSONArray threads = json.getJSONArray("threads");
                for (int i = 0; i < threads.length(); i++) {
                    JSONObject thread = threads.getJSONObject(i);
                    JSONObject matter = new JSONObject();
                    matter.put("id", thread.get("id"));
                    matter.put("description", thread.get("snippet"));
                    matter.put("display_number", thread.get("snippet"));
                    MatterContent.getInstance().addItem(matter);
                }
                MatterContent.getInstance().notifyDatasetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
