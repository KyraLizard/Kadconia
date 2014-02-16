package de.dhbw.infos;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import de.dhbw.database.DataBaseServer;
import de.dhbw.database.Server;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 21.11.13.
 */
public class ServerInfoFragment extends Fragment {

    private Context mContext;

    private List<Server> mServerList;
    private ExpandableListView mExpandableListView;

    public ServerInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();

        View view = inflater.inflate(R.layout.fragment_info_serverinfo, null);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.serverinfo_expandablelist);

        mServerList = (new DataBaseServer(mContext)).getAllServer();
        mExpandableListView.setAdapter(new ServerInfoAdapter(mServerList));

        return mExpandableListView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public class ServerInfoAdapter extends BaseExpandableListAdapter
    {
        private List<Server> mServerList;

        public ServerInfoAdapter(List<Server> serverList) {
            this.mServerList = serverList;
        }

        @Override
        public int getGroupCount() {
            return mServerList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return 1;
        }

        @Override
        public Object getGroup(int i) {
            return mServerList.get(i);
        }

        @Override
        public Object getChild(int i, int i2) {
            return i2;
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
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(mServerList.get(i).getOwner() + " " + mServerList.get(i).getName());
            return view;
        }

        @Override
        public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText("Domain: " + mServerList.get(i).getDomain() + "\nPort: " + mServerList.get(i).getPort());
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return false;
        }
    }
}
