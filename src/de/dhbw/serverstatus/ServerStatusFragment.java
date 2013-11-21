package de.dhbw.serverstatus;

import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.database.DataBaseHelper;
import de.dhbw.database.DataBaseServer;
import de.dhbw.database.Server;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 21.11.13.
 */
public class ServerStatusFragment extends ListFragment{

    private Context mContext;

    public ServerStatusFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();

        //View view = inflater.inflate(R.layout.fragment_serverstatus_element, null);
        SQLiteDatabase mDataBase = (new DataBaseHelper(mContext)).getReadableDatabase();
        DataBaseServer mDataBaseServer = new DataBaseServer();

        List<Server> serverList = mDataBaseServer.getAllServer(mDataBase);
        List<String> serverNameList = new ArrayList<String>();
        for (Server server : serverList)
            serverNameList.add(server.getName());

        setListAdapter(new ServerStatusAdapter(mContext, R.layout.fragment_serverstatus_element, serverNameList, serverList));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public class ServerStatusAdapter extends ArrayAdapter<String>
    {
        private List<Server> mServerList;

        public ServerStatusAdapter(Context context, int resource, List<String> objects, List<Server> mServerList) {
            super(context, resource, objects);
            this.mServerList = mServerList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_serverstatus_element, parent, false);

            Server server = mServerList.get(position);

            TextView textView = (TextView) view.findViewById(R.id.server_element_text);

            if (isOnline())
            {
                try
                {
                    boolean online = false;
                    for (int i=0; i<5; i++)
                    {
                        if (ServerPortOpenChecker.isServerPortOpen(server.getIp(), server.getPort()))
                        {
                            online = true;
                            break;
                        }
                    }
                    if (online)
                    {
                        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_server_online, 0, 0, 0);
                        textView.setText(server.getName() + " (online)");
                    }
                    else
                    {
                        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_server_offline, 0, 0, 0);
                        textView.setText(server.getName() + " (offline)");
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link_ban, 0, 0, 0);
                textView.setText(server.getName());
                Toast.makeText(mContext, "Keine Internet-Verbindung", Toast.LENGTH_LONG).show();
            }

            return textView;
            //return super.getView(position, convertView, parent);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
