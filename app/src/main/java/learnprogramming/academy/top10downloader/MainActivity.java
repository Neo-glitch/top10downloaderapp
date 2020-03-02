package learnprogramming.academy.top10downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    // field used to hold the listView widget value.
    private ListView listApps;
//    "%d" in a string means that we want to replace that part with an integer value using the String.format(String, int to be passed) method.
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedUrl = "INVALIDATED";
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

//      if bundle is not null, that means the app was ran b4 but just rotated.
        if(savedInstanceState != null){
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedUrl, feedLimit));
//        Log.d(TAG, "onCreate: Starting AsyncTask");
//        DownloadData downloadData = new DownloadData();
//        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
//        Log.d(TAG, "onCreate: done");

    }

    // method is used to create the menu obj from the xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflater uses the appcompatActivity as a context to get the menu xml file
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if(feedLimit == 10){
            menu.findItem(R.id.mnu10).setChecked(true);
        }
        else{
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case(R.id.mnuFree):
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case(R.id.mnuPaid):
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case(R.id.mnuSongs):
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                }
                else{
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feed Limit unchanged");
                }
                break;
//          this sets the feedCachedurl to something else, so that it not same as feed url and it downloads url after pressing the refresh widget.
            case R.id.mnuRefresh:
                feedCachedUrl = "INVALIDATED";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    // This does what was already done in the downloading of xml in the onCreate method, but the url isnt fixed.
    private void downloadUrl(String feedUrl){
        if(!feedUrl.equalsIgnoreCase(feedCachedUrl)){
            Log.d(TAG, "downloadUrl: Starting AsyncTask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl;
            Log.d(TAG, "donwloadUrl: done");
        }
        else{
            Log.d(TAG, "downloadUrl: URL not changed");
        }
    }

    // class that is a subclass of aysynctask class used for multithreading
    // info passed to the class is String i.e the url of the rss feed as 1st params
    // 2nd params is set to void, but it's used normally to display our download progress bar
    // 3rd params is the type of result we want to get back, i.e the xml code of the rss feed.
    private class DownloadData extends AsyncTask<String, Void, String>{
        private static final String TAG = "DownloadData";

        // This method is executed on the main U.I tread n is exceuted when the app is ran after the doInBackground method.
        // the params; "String s" is the xml data.
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

//  when creating an arrayAdapter object, the params passed are the context, the resource(layout) that has the textView that the arrayAdapter will use to store data.
//  The last arg is the list of object to display
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<>(
//                    MainActivity.this, R.layout.list_item, parseApplications.getApplications());
////  Links the listView widget to the adapter.
//            listApps.setAdapter(arrayAdapter);

//          creates an Array adapter object using our custom adapter.
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);

        }


        // "..." means that the number of args passed to the method is var # and array of args
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Start with " + strings[0]);
            // strings[0] is for the first arg passed to the doInBackground method.
            String rssFeed = downloadXML(strings[0]);

            if(rssFeed == null){
                Log.e(TAG, "doInBackground: error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();

            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);

                // This code below is same as that of the 3 lines of code commented above.
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];
                while(true){
                    charsRead = reader.read(inputBuffer);

                    // when Bufferedreader read method gives a value less than 0 it means that we're at the end of the stream of data.
                    if(charsRead < 0){
                        break;
                    }
                    if(charsRead > 0){
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();
                // converts the StringBuilder to a String inorder to pass as an arg.
                return xmlResult.toString();
            }

            catch(MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            }
            catch(IOException e){
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            }
            catch(SecurityException e){
                Log.e(TAG, "downloadXML: Security Exception. Needs Permission? " + e.getMessage());
//                e.printStackTrace();
            }

            return null;
        }
    }
}
