package com.clio.exercise.johnchen.matters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Mater detail screen.
 * This fragment is either contained in a {@link MaterListActivity}
 * in two-pane mode (on tablets) or a {@link MaterDetailActivity}
 * on handsets.
 */
public class MaterDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private MatterContent.Matter mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MaterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = MatterContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mater_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            View detailView =  rootView.findViewById(R.id.mater_detail);
            setText(detailView, R.id.display_number, mItem.displayNumber);
            setText(detailView, R.id.client_name, mItem.clientName);
            setText(detailView, R.id.description, mItem.description);
            setText(detailView, R.id.open_date, mItem.openDate);
            setText(detailView, R.id.status, mItem.status);
        }

        return rootView;
    }

    private void setText(View parentView, int id, String text) {
        ((TextView)parentView.findViewById(id)).setText(text);
    }
}
