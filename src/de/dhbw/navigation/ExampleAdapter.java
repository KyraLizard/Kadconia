package de.dhbw.navigation;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 18.12.13.
 */
public class ExampleAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    // Sample data set.  children[i] contains the children (String[]) for groups[i].
    private List<String> mListGroups = new ArrayList<String>();
    private List<String> mPlayerOnlineElements = new ArrayList<String>();
    /*private String[][] children = {
            { "Arnold", "Barry", "Chuck", "David" },
            { "Ace", "Bandit", "Cha-Cha", "Deuce" },
            { "Fluffy", "Snuggles" },
            { "Goldy", "Bubbles" }
    };*/

    public ExampleAdapter(Context context) {
        mContext = context;
        String[] listGroups = mContext.getResources().getStringArray(R.array.nav_array);
        for (int i=0; i<listGroups.length; i++)
            mListGroups.add(listGroups[i]);
        String[] playerOnlineElements = mContext.getResources().getStringArray(R.array.nav_elements_serverstatus);
        for (int i=0; i<playerOnlineElements.length; i++)
            mPlayerOnlineElements.add(playerOnlineElements[i]);
    }

    public Object getChild(int groupPosition, int childPosition) {
        if (mListGroups.get(groupPosition).equals(mContext.getString(R.string.nav_player_online)))
            return mPlayerOnlineElements.get(childPosition);
        else
            return null;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        if (mListGroups.get(groupPosition).equals(mContext.getString(R.string.nav_player_online)))
            return mPlayerOnlineElements.size();
        else
            return 0;
    }

    /*public TextView getGenericView() {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);

        TextView textView = new TextView(mContext);
        textView.setLayoutParams(lp);
        // Center the text vertically
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set the text starting position
        //textView.setPaddingRelative(36, 0, 0, 0);
        // Set the text alignment
        //textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        return textView;
    }*/

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        /*AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);*/

        //TextView textView = new TextView(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        //textView.setLayoutParams(lp);
        // Center the text vertically
        //textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        //textView.setText(getChild(groupPosition, childPosition).toString());
        textView.setText(mPlayerOnlineElements.get(childPosition));
        textView.setBackgroundColor(mContext.getResources().getColor(R.color.nav_element));
        return view;
    }

    public Object getGroup(int groupPosition) {
        return mListGroups.get(groupPosition);
    }

    public int getGroupCount() {
        return mListGroups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);

        TextView textView = new TextView(mContext);
        textView.setLayoutParams(lp);
        // Center the text vertically
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setText(getGroup(groupPosition).toString());
        return textView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

}
