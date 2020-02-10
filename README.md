# yelp-sample
* The Project is written in Kotlin 
* Used RxJava and Retrofit2 on the networking side. Used Google's EasyPermissions library to streamline management of "Location permissions" from user.
* From a architecture standpoint, used the LiveData/ViewModel approach and best practices (MVVM) (per Android architectural components) to allow for UI layer to be decoupled from the business layer. 
* It uses the latest androidx framework (rather than older support libraries).
* From a UI standpoint, leveraged latest androidx cardviews and Recyclerviews
* Also handled Location management and user location permission scenarios to streamiline fetching of user's "current location". Getting user "last location"(current location) directly is not always reliable and may return null. Therefore, imlplemented a more extensive and more reliable "Location update" approach instead.   

Extras: I implemented "auto-complete" for better end-user expereince.

Next Steps (Todos and challenges) : Due to time constraint & time limit on the project, I have the testing framework already in place to test the viewmodels and live data e.t.c but I was attempting to setup a "mock server" to properly test items, ran into some issues setting up the "mock server" and data, need some more time to address that going forward.

If the team has any questions or need any clarification during the review process let me know, and I will be happy to address any queries the team may have.

![Alt text](./device-2020-02-09-165243.png?raw=true "Optional Title")


![Alt text](./device-2020-02-09-165330.png?raw=true "Optional Title")


![Alt text](./device-2020-02-09-165401.png?raw=true "Optional Title")


![Alt text](./device-2020-02-09-165505.png?raw=true "Optional Title")

