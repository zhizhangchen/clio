package com.clio.exercise.johnchen.matters.importing.gmail;

import com.clio.exercise.johnchen.matters.JSONUtil;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class GetAccessToken {

    public GetAccessToken() {
    }

    /**
     * Get an access token, which is required to call Gmail APIs
     */
    public JSONObject getToken(String address,String token,String client_id,String client_secret,String redirect_uri,String grant_type) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        // Making HTTP request
        try {
            URL u = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");

            List<AbstractMap.SimpleEntry> params = new ArrayList<AbstractMap.SimpleEntry>();
            params.add(new AbstractMap.SimpleEntry("code", token));
            params.add(new AbstractMap.SimpleEntry("client_id", client_id));
            params.add(new AbstractMap.SimpleEntry("client_secret", client_secret));
            params.add(new AbstractMap.SimpleEntry("redirect_uri", redirect_uri));
            params.add(new AbstractMap.SimpleEntry("grant_type", grant_type));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            is = conn.getInputStream();
            return JSONUtil.readJSONObject(is);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getQuery(List<AbstractMap.SimpleEntry> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String) pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
