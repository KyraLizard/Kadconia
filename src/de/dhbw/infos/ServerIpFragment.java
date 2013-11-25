package de.dhbw.infos;

import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import de.dhbw.serverstatus.ServerPortOpenChecker;

/**
 * Created by Mark on 21.11.13.
 */
public class ServerIpFragment extends ListFragment{

    private Context mContext;

    public ServerIpFragment() {
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
        {
            serverNameList.add(server.getFormattedOwner() + " " + server.getName());
            serverNameList.add("IP: " + server.getIp() + ", Port: " + server.getPort());
        }

        setListAdapter(new ServerIpAdapter(mContext, R.layout.fragment_serverip_element, serverNameList));
        //setListAdapter(new ServerStatusAdapter(mContext, R.layout.fragment_serverip_element, serverNameList, serverList));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public class ServerIpAdapter extends ArrayAdapter<String>
    {
        private List<String> objects;

        public ServerIpAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_serverip_element, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.server_element_text);

            if (position % 2 == 0)
                view.setBackgroundResource(R.drawable.background_border);

            textView.setText(objects.get(position));

            return textView;

            //return super.getView(position, convertView, parent);
        }
    }

}
