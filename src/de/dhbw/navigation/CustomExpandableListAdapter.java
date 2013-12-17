package de.dhbw.navigation;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 17.12.13.
 */
public class CustomExpandableListAdapter implements ExpandableListAdapter {

    private Context mContext;
    private List<String> mListGroups = new ArrayList<String>();
    private List<String> mPlayerOnlineElements = new ArrayList<String>();

    public CustomExpandableListAdapter(Context context) {
        mContext = context;
        String[] listGroups = mContext.getResources().getStringArray(R.array.nav_array);
        for (int i=0; i<listGroups.length; i++)
            mListGroups.add(listGroups[i]);
        String[] playerOnlineElements = mContext.getResources().getStringArray(R.array.nav_elements_player_online);
        for (int i=0; i<playerOnlineElements.length; i++)
            mPlayerOnlineElements.add(playerOnlineElements[i]);
    }


    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return mListGroups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (mListGroups.get(i).equals(mContext.getString(R.string.nav_player_online)))
            return mPlayerOnlineElements.size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int i) {
        return mListGroups.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        if (mListGroups.get(i).equals(mContext.getString(R.string.nav_player_online)))
            return mPlayerOnlineElements.get(i2);
        else
            return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(mListGroups.get(i));

        if (mListGroups.get(i).equals(mContext.getString(R.string.nav_player_online)))
        {
            if (isExpanded)
                textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_action_collapse,0);
            else
                textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_action_expand,0);

            textView.setCompoundDrawablePadding(10);
        }

        //TODO: Remove (Just for testing)
        //textView.setBackgroundResource(R.drawable.background_border);

        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {

        if (mListGroups.get(i).equals(mContext.getString(R.string.nav_player_online)))
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(mPlayerOnlineElements.get(i2));
            textView.setPadding(75,0,0,0);
            view.setBackgroundColor(mContext.getResources().getColor(R.color.nav_element));
            return view;
        }
        else
            return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
        //return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
        //return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l2) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
