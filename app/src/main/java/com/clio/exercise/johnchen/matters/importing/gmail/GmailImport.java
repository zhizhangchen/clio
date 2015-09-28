package com.clio.exercise.johnchen.matters.importing.gmail;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clio.exercise.johnchen.matters.R;

public class GmailImport {

    private static String CLIENT_ID = "1076117038029-v8sl0ejctm80lh8f7tfsb661svdmlo6h.apps.googleusercontent.com";
    //Use your own client id
    private static String CLIENT_SECRET ="NlGf_petkWI6QLOKMPtx0236";
    //Use your own client secret
    private static String REDIRECT_URI="http://localhost";
    private static String GRANT_TYPE="authorization_code";
    private static String TOKEN_URL ="https://accounts.google.com/o/oauth2/token";
    private static String OAUTH_URL ="https://accounts.google.com/o/oauth2/auth";
    private static String OAUTH_SCOPE="https://www.googleapis.com/auth/gmail.readonly";
    private final Context mContext;
    //Change the Scope as you need
    WebView web;
    SharedPreferences pref;
    TextView Access;

    public GmailImport(Context context) {
        mContext = context;
    }

    public void start() {
        pref = mContext.getSharedPreferences("AppPref", 0);
        final Dialog auth_dialog;

        // TODO Auto-generated method stub
        auth_dialog = new Dialog(mContext);
        auth_dialog.setContentView(R.layout.auth_dialog);
        web = (WebView) auth_dialog.findViewById(R.id.webv);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + CLIENT_ID + "&scope=" + OAUTH_SCOPE);
        web.setWebViewClient(new WebViewClient() {

            boolean authComplete = false;
            Intent resultIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            String authCode;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);
                    authComplete = true;
                    resultIntent.putExtra("code", authCode);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("Code", authCode);
                    edit.commit();
                    auth_dialog.dismiss();
                    new TokenGet().execute();
                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    resultIntent.putExtra("code", authCode);
                    authComplete = true;
                    Toast.makeText(mContext, "Error Occured", Toast.LENGTH_SHORT).show();
                    auth_dialog.dismiss();
                }
            }
        });
        auth_dialog.show();
        auth_dialog.setTitle("Authorize Learn2Crack");
        auth_dialog.setCancelable(true);
    }

    private class TokenGet extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String Code;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Contacting Google ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            Code = pref.getString("Code", "");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            GetAccessToken jParser = new GetAccessToken();
            JSONObject json = jParser.gettoken(TOKEN_URL,Code,CLIENT_ID,CLIENT_SECRET,REDIRECT_URI,GRANT_TYPE);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json != null){

                try {

                    String tok = json.getString("access_token");
                    String expire = json.getString("expires_in");
                    String refresh = json.getString("refresh_token");

                    Log.d("Token Access", tok);
                    Log.d("Expire", expire);
                    Log.d("Refresh", refresh);
                    new GetThreads(mContext, tok, Access).execute();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
    }
}

