# Clio exercise
An Android App that displays a list of all matters on Clio using the results of a call to `https://app.goclio.com/api/v2/matters`. Upon clicking on a matter row, the app drills into the matter’s profile. On the individual profile page, the matter’s display name, client name, description, open date, and status are shown.

###Implementation details
* Master/Detail Activities are used to reflect the relationship between matter list and profile details. 
* The app works fine in offline mode after a fresh open. Fetched data are saved in SharedPreferences with abstraction for future persistence option.
* A SyncAdapter is used to allow better offline experience. Currently A helper class is used to provide content and notify changes. Later a Content provider can be developed to accomplish these tasks.
* The commits are arranged in such a way that every commit can be checked out and works out of the box. Each commit is dedicated to one task.
