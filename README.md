# Clio exercise
An Android App that displays a list of all matters on Clio using the results of a call to `https://app.goclio.com/api/v2/matters`. Upon clicking on a matter row, the app drills into the matter’s profile. On the individual profile page, the matter’s display name, client name, description, open date, and status are shown.

###Implementation details
* Master/Detail Activities are used to reflect the relationship between matter list and profile details. 
* The app works fine in offline mode after a fresh open. Fetched data are saved in SharedPreferences with abstraction for future persistence option.
* A SyncAdapter is used to allow better offline experience. Currently A helper class is used to provide content and notify changes. Later a Content provider can be developed to accomplish these tasks.
* The commits are arranged in such a way that every commit can be checked out and works out of the box. Each commit is dedicated to one task.

###Gmail importing
I frist tried to implement it following the guidance from https://developers.google.com/gmail/api/quickstart/android, which utilizes Android play service to authorize and access Gmail. But there seems to be some problem in the Android client implementation of this API - the account manager couldn't find accounts created in the system. So I resorted to the standard oauth2 method, basically WebView => user permission => Auth Code => Access token (Java) => Gmail API. This is actually a better solution in my point of view, as it doesn't depend on Google Play service, which many phones don't have by default, although we have to send compose Http request and parse response by ourselves.

Of course, there are still a lot to be done - uploading the imported matters to server, letting users choose which email threads should be imported, importing contacts, calendars, tasks and documents,  merging Clio inbox with Gmail inbox, being smarter on client detection and matter detection, so on and so on. But, this experiment definitely opened up the door to all these possibilities.


###Known issues:
* The imported matters will go away after a while. This is because that the SyncAdapter is running periodically in the backend, which fetches data from server and refreshes the list. I intentionally left this unfixed, hoping that this can in some way demonstrate the effectiveness of the SyncAdapter.
