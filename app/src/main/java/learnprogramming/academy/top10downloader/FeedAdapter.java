package learnprogramming.academy.top10downloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

//  Method is used to get the number of items to diaplay, and helps the scroll bar to display how far we have gone.
    @Override
    public int getCount() {
//      gives number of apps is based on the list(applications) size.
        return applications.size();
    }


//  Used to get the views to rep in the listView.
//  when the getView method is called, the listView tells it the position of the item it needs to display in the position params
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
//     creates a a new view by inflating the layout resource only if the convertView is null, i.e no view was created b4.
//     the convertView param is passed to the method via the Listview inorder to give it a view to reuse and save mem.
        if(convertView == null){
            Log.d(TAG, "getView: called with null convertView");
            convertView = layoutInflater.inflate(layoutResource, parent, false);
//            creates the view holder obj and store it in the convertView
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            Log.d(TAG, "getView:  provided a convertView");
//            retrieves the viewHolder from using getTag method
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        we then find the three textView widgets by using the findViewById method of the view (list_record).i.e the contraintlayout.
//        ViewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
//        TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);

//      retrieves the object of the position given by the listView.
        FeedEntry currentApp = applications.get(position);
        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());
        return convertView;
    }


// The class does the finding of the views once and stores the results.
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v){
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
        }
    }
}
