package de.dhbw.infos;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.net.UnknownHostException;
import java.util.List;

import de.dhbw.database.DataBaseServer;
import de.dhbw.database.Server;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 21.11.13.
 */
public class ServerInfoFragment extends Fragment {

    private Context mContext;

    public ServerInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();

        View view = inflater.inflate(R.layout.fragment_info_serverinfo, null);
        ExpandableListView mExpandableListView = (ExpandableListView) view.findViewById(R.id.serverinfo_expandablelist);

        List<Server> mServerList = (new DataBaseServer(mContext)).getAllServer();
        mExpandableListView.setAdapter(new ServerInfoAdapter(mServerList));

        return mExpandableListView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public class ServerInfoAdapter extends BaseExpandableListAdapter {

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
            return 3;
        }

        @Override
        public Object getGroup(int i) {
            return mServerList.get(i);
        }

        @Override
        public Object getChild(int i, int i2) {
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
            return true;
        }

        @Override
        public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(mServerList.get(i).getOwner() + " " + mServerList.get(i).getName());

            if (isExpanded)
                textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_action_collapse,0);
            else
                textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_action_expand,0);

            textView.setCompoundDrawablePadding(10);

            return view;
        }

        @Override
        public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);

            final TextView textView = (TextView) view.findViewById(android.R.id.text1);
            switch (i2)
            {
                case 0:
                    textView.setText("Domain: " + mServerList.get(i).getDomain());
                    break;
                case 1:
                    textView.setText("IP: " + "Lädt...");
                    (new AsyncTask<Integer,String,Object>() {

                        private String mIpAdress;

                        @Override
                        protected Object doInBackground(Integer... i) {
                            try {
                                mIpAdress = java.net.InetAddress.getByName(mServerList.get(i[0]).getDomain()).getHostAddress();
                            }
                            catch (UnknownHostException e) {
                                e.printStackTrace();
                                mIpAdress = "Nicht verfügbar";
                            }
                            publishProgress(mIpAdress);
                            return null;
                        }

                        @Override
                        protected void onProgressUpdate(String... values) {
                            textView.setText("IP: " + values[0]);
                            super.onProgressUpdate(values);
                        }
                    }).execute(i);
                    break;
                case 2:
                    textView.setText("Port: " + mServerList.get(i).getPort());
                    break;
            }

            //textView.setPadding(75, 0, 0, 0);
            view.setBackgroundColor(mContext.getResources().getColor(R.color.nav_element));

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return false;
        }
    }
}
