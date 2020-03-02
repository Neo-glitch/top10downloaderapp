package learnprogramming.academy.top10downloader;


import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

// This is the class that holds all te data from the feed entry class, # all the apps found in the xml data.
public class ParseApplications {
    private static final String TAG = "ParseApplications";
    // ArrayList to hold all the feed entry xml data
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

// the xml data will be parsed to this method.
    public boolean parse(String xmlData){
        boolean status = true;
        FeedEntry currentRecord = null;

        // the inEntry var is used to tell us if we are processing data in an entry or not.
        boolean inEntry = false;
        // used to store the value of the current tag
        String textValue ="";

        try{
            // from here to xpp.setInput is usedd to setup the java xml parser that helps in using the xml data.
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();
            // while loop executes as long as we haven't reached the end of the xml data.
            while(eventType != XmlPullParser.END_DOCUMENT){

                // .getName method of the pullparser obj = returns the name of the current tag.
                String tagName = xpp.getName();
                switch (eventType){
                    // the event here is a start of a tag.
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;
                    // stores tag in the textValue var, but does nothing with it until the end of a tag is reached.
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        // if statement checks if the pullparser is in an entry tag.
                        if(inEntry){

                            // n.b: syntax; "if("entry".equalsIgnoreCase(tagName))" = assures us that we can't get a null, unlike the normal other way.
                            // if statement is activated once we reach the end entry tag of the subject entry tag.
                            if("entry".equalsIgnoreCase(tagName)){
                                applications.add(currentRecord);
                                inEntry = false;
                            }
                            else if("name".equalsIgnoreCase(tagName)){
                                currentRecord.setName(textValue);
                            }
                            else if("artist".equalsIgnoreCase(tagName)){
                                currentRecord.setArtist(textValue);
                            }
                            else if("releaseDate".equalsIgnoreCase(tagName)){
                                currentRecord.setReleaseDate(textValue);
                            }
                            else if("summary".equalsIgnoreCase(tagName)){
                                currentRecord.setSummary(textValue);
                            }
                            else if("image".equalsIgnoreCase(tagName)){
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;

                    default:
                }

                // xpp.next tells the pull parser to continue working through the xml to the next important event.
                eventType = xpp.next();
            }
//            for (FeedEntry app: applications){
//                Log.d(TAG, "**************");
//                Log.d(TAG, app.toString());
//            }

        }
        catch(Exception e){
            status = false;
            e.printStackTrace();
        }

        return status;


    }
}
