package com.clio.exercise.johnchen.matters.importing.gmail;

/**
 * Created by user on 2015/9/27.
 */

import android.os.AsyncTask;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.services.gmail.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Gmail API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private GmailActivity mActivity;

    /**
     * Constructor.
     * @param activity GmailActivity that spawned this task.
     */
    ApiAsyncTask(GmailActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Gmail API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearResultsText();
            mActivity.updateResultsText(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    GmailActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        if (mActivity.mProgress.isShowing()) {
            mActivity.mProgress.dismiss();
        }
        return null;
    }

    /**
     * Fetch a list of Gmail labels attached to the specified account.
     * @return List of Strings labels.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<String> labels = new ArrayList<String>();
        ListLabelsResponse listResponse =
                mActivity.mService.users().labels().list(user).execute();
        for (Label label : listResponse.getLabels()) {
            labels.add(label.getName());
        }
        return labels;
    }

}
