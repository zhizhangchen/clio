package com.clio.exercise.johnchen.matters;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing Matter content for user interfaces.
 * Created by user on 2015/9/26.
 */
public class MatterContent {
    private ListFragment mListFragment;
    private ProgressDialog mDialog;

    private static MatterContent mInstance;
    /**
     * An array of Matter items.
     */
    public static List<Matter> ITEMS = new ArrayList<Matter>();

    /**
     * A map of Matter items, by ID.
     */
    public static Map<String, Matter> ITEM_MAP = new HashMap<>();

    private MatterContent() {
    }

    public static synchronized MatterContent getInstance() {
        if (mInstance == null) {
            mInstance = new MatterContent();
        }
        return mInstance;
    }


    private static void addItem(Matter item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public void update(ListFragment fragment, String url) {
        mListFragment = fragment;
        mDialog = new ProgressDialog(fragment.getActivity());
        new AsyncListViewLoader().execute(url);
    }

    /**
     * A class holding matter information
     */

    public class Matter {
        public String id;
        public String displayNumber;
        public String description;


        public Matter(String id, String displayNumber, String description) {
            this.id = id;
            this.displayNumber = displayNumber;
            this.description = description;
        }

        @Override
        public String toString() {
            return displayNumber;
        }
    }


    private class AsyncListViewLoader  extends AsyncTask<String, Void, List<MatterContent.Matter>> {
        @Override
        protected void onPostExecute(List<MatterContent.Matter> result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            // TODO: replace with a real list adapter.
            mListFragment.setListAdapter(new ArrayAdapter<Matter>(
                    mListFragment.getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    result));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Downloading matters...");
            mDialog.show();
        }

        @Override
        protected List<MatterContent.Matter> doInBackground(String... params) {
            List<MatterContent.Matter> result = new ArrayList<MatterContent.Matter>();

            try {
                URL u = new URL(params[0]);

                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestProperty("Authorization", "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("GET");

                conn.connect();
                InputStream is = conn.getInputStream();

                // Read the stream
                byte[] b = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while (is.read(b) != -1) {
                    baos.write(b);
                    Arrays.fill(b, (byte) 0);
                }

                String JSONResp = new String(baos.toByteArray());

                JSONObject obj = new JSONObject(JSONResp);
                JSONArray arr = obj.getJSONArray("matters");
                for (int i = 0; i < arr.length(); i++) {
                    Matter matter = convertMatter(arr.getJSONObject(i));
                    result.add(matter);
                    addItem(matter);
                }

                return result;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        private MatterContent.Matter convertMatter(JSONObject obj) throws JSONException {
            String id = obj.getString("id");
            String displayNumber = obj.getString("display_number");
            String description = obj.getString("description");

            return new MatterContent.Matter(id, displayNumber, description);
        }
    }
}

