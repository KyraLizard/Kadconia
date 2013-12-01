package de.dhbw.serverstatus;

import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
public class ServerStatusFragment extends ListFragment {

    private Context mContext;

    public ServerStatusFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();

        SQLiteDatabase mDataBase = (new DataBaseHelper(mContext)).getReadableDatabase();
        DataBaseServer mDataBaseServer = new DataBaseServer();

        List<String> ownerList = mDataBaseServer.getOwners(mDataBase);
        List<List<Server>> serverList = new ArrayList<List<Server>>();

        for (String owner : ownerList)
            serverList.add(mDataBaseServer.getAllServerByOwner(mDataBase, owner));

        List<String> listNames = new ArrayList<String>();
        List<Object> listObjects = new ArrayList<Object>();

        for (int i=0; i < ownerList.size(); i++)
        {
            String ownerName = Character.toUpperCase(ownerList.get(i).charAt(0)) + ownerList.get(i).substring(1);
            listNames.add(ownerName);
            listObjects.add(ownerName);
            for (Server server : serverList.get(i))
            {
                listNames.add(server.getName());
                listObjects.add(server);
            }
        }

        setListAdapter(new ServerStatusAdapter(mContext, R.layout.fragment_serverstatus_element, listNames, listObjects));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public List<String> setNameArray() {
        //TODO: Implement Method
        return null;
    }

    public List<Object> setObjectArray() {
        //TODO: Implement Method
        return null;
    }


    public class ServerStatusAdapter extends ArrayAdapter<String>
    {
        private List<Object> mListObjects;

        public ServerStatusAdapter(Context context, int resource, List<String> objects, List<Object> mListObjects) {
            super(context, resource, objects);
            this.mListObjects = mListObjects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_serverstatus_element, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.server_element_text);

            if (mListObjects.get(position) instanceof String)
            {
                textView.setText((String) mListObjects.get(position));
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.drawable.background_border);
            }
            else if (mListObjects.get(position) instanceof Server)
            {
                Server server = (Server) mListObjects.get(position);

                if (isOnline())
                {
                    try
                    {
                        boolean online = false;
                        textView.setText(server.getName());
                        for (int i=0; i<5; i++)
                        {
                            if (ServerPortOpenChecker.isServerPortOpen(server.getIp(), server.getPort()))
                            {
                                online = true;
                                break;
                            }
                        }
                        if (online)
                            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_server_online, 0, 0, 0);
                        else
                            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_server_offline, 0, 0, 0);
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
            }
            else
                textView.setText("Fehler beim Erstellen der Liste!");

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
