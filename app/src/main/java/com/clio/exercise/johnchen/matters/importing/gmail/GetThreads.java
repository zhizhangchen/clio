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
import java.util.Date;

/**
 * Created by user on 2015/9/28.
 */
public class GetThreads extends AsyncTask<String, String, JSONObject> {
    private final TextView mResult;
    private ProgressDialog pDialog;
    private Context mContext;
    private String mAccessToken;
    private String GET_THREAD_URL_PATTERN = "https://www.googleapis.com/gmail/v1/users/me/threads/%s?format=metadata&metadataHeaders=From&metadataHeaders=Subject&metadataHeaders=Date&fields=messages/payload";

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
        return fetchJSONObject("https://www.googleapis.com/gmail/v1/users/me/threads?labelIds=CATEGORY_PERSONAL&maxResults=8");
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
                    String threadId = thread.getString("id");
                    matter.put("id", threadId);
                    matter.put("description", thread.get("snippet"));
                    matter.put("display_number", thread.get("snippet"));
                    matter.put("open_date", new Date());
                    matter.put("status", "Open");
                    MatterContent.getInstance().addItem(matter);
                    new GetThread(threadId).execute();
                }
                MatterContent.getInstance().notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject fetchJSONObject(String url) {
        InputStream is = null;
        String json = "{}";
        try {
            URL u = new URL(url);
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

    private class GetThread extends AsyncTask<String, String, JSONObject> {
        String mThreadId;
        public GetThread(String threadId) {
            mThreadId = threadId;
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            return fetchJSONObject(String.format(GET_THREAD_URL_PATTERN, mThreadId));
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                try {
                    JSONArray messages = json.getJSONArray("messages");
                    JSONArray headers = messages.getJSONObject(0).getJSONObject("payload").getJSONArray("headers");
                    JSONObject matter = new JSONObject();
                    matter.put("id", mThreadId);
                    matter.put("description", getHeaderValue(headers, "Subject"));
                    JSONObject client = new JSONObject();
                    String from = getHeaderValue(headers, "From");
                    client.put("name", from);
                    matter.put("client", client);
                    matter.put("open_date", getHeaderValue(headers, "Date"));
                    matter.put("status", "Open");
                    matter.put("display_number", mThreadId + "-" + from);

                    MatterContent.getInstance().updateItem(matter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private String getHeaderValue(JSONArray array, String name) throws JSONException {
            for (int i = 0; i < array.length(); i++) {
                JSONObject elem = array.getJSONObject(i);
                if (elem.getString("name").equals(name))
                    return elem.getString("value");
            }
            return null;
        }
    }
}
