package com.clio.exercise.johnchen.matters;

import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import com.clio.exercise.johnchen.matters.storage.Storage;
import com.clio.exercise.johnchen.matters.storage.StorageFactory;
import com.clio.exercise.johnchen.matters.sync.SyncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing Matter content for user interfaces.
 * Created by user on 2015/9/26.
 */
public class MatterContent {
    private ListFragment mListFragment;
    private static MatterContent mInstance;
    private ArrayAdapter<MatterContent.Matter> mListAdapter;

    private final String JSON_STORAGE_KEY = "json";
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

    /**
     * returns the singleton instance
     */
    public static synchronized MatterContent getInstance() {
        if (mInstance == null) {
            mInstance = new MatterContent();
        }
        return mInstance;
    }

    /**
     * Refresh list adapter with {@param json}
     *
     * @returns number of matters set to the list adapter
     */
    public int updateListAdaptor(String json) {
        if (mListAdapter == null)
            return 0;

        Storage storage = StorageFactory.getStorage(mListFragment.getContext());
        if (json != null)
            storage.setItem(JSON_STORAGE_KEY, json);

        if (json == null && ITEMS.isEmpty() )
            json = storage.getItem(JSON_STORAGE_KEY, "{\"matters\": []}");

        if (json == null)
            return 0;

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("matters");
            ITEM_MAP.clear();
            ITEMS.clear();
            for (int i = 0; i < arr.length(); i++) {
                addItem(arr.getJSONObject(i));
            }
            notifyDataSetChanged();
            return arr.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void notifyDataSetChanged(){
        if (mListAdapter != null) {
            mListFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    /**
     * Save {@param fragment} for future use and update it with stored data
     */
    public void update(ListFragment fragment) {
        mListFragment = fragment;
        SyncUtils.CreateSyncAccount(fragment.getActivity());
        mListAdapter = new ArrayAdapter<MatterContent.Matter>(
                fragment.getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                ITEMS);
        fragment.setListAdapter(mListAdapter);
        updateListAdaptor(null);
    }

    /**
     * Add an item into the content containers.
     */
    public void addItem(JSONObject obj) throws JSONException {
        Matter item = MatterContent.getInstance().convertMatter(obj);
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * Update an item with new data
     */
    public synchronized void updateItem(JSONObject obj) throws JSONException {
        Matter matter = ITEM_MAP.get(obj.getString("id"));
        fillMatter(matter, obj);
        notifyDataSetChanged();
    }

    private void fillMatter(Matter matter, JSONObject obj) {
        matter.id = obj.optString("id");
        matter.displayNumber = obj.optString("display_number");
        JSONObject client = obj.optJSONObject("client");
        if (client != null)
            matter.clientName = client.optString("name");
        matter.description = obj.optString("description");
        matter.status = obj.optString("status");
        matter.openDate = obj.optString("open_date");
    }
    private Matter convertMatter(JSONObject obj) throws JSONException {
        Matter matter = new Matter();
        fillMatter(matter, obj);
        return matter;
    }

    /**
     * A class holding matter information
     */

    public class Matter {
        public String id;
        public String displayNumber;
        public String clientName;
        public String description;
        public String openDate;
        public String status;

        Matter() {
        }

        @Override
        public String toString() {
            return displayNumber;
        }
    }
}

