package de.dhbw.serverstatus;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        setHasOptionsMenu(true);

        //setList();
        //setListAdapter(null);
        //(new RefreshListTask()).execute();
        List<String> listNames = new ArrayList<String>();
        List<Object> listObjects = new ArrayList<Object>();
        DataBaseServer mDataBaseServer = new DataBaseServer();

        if (!isOnline())
            Toast.makeText(mContext, R.string.error_no_internet, Toast.LENGTH_SHORT).show();

        for (String owner : mDataBaseServer.getOwners(mContext))
        {
            String ownerName = Character.toUpperCase(owner.charAt(0)) + owner.substring(1);
            listNames.add(Character.toUpperCase(ownerName.charAt(0)) + ownerName.substring(1));
            listObjects.add(ownerName);
            for (Server server : mDataBaseServer.getAllServerByOwner(mContext, owner))
            {
                server.setOnline(checkOnline(server));
                listNames.add(server.getName());
                listObjects.add(server);
            }
        }

        setListAdapter(new ServerStatusAdapter(mContext, R.layout.fragment_serverstatus_element, listNames, listObjects));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vote, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_refresh:
                setListShown(false);
                (new RefreshListTask()).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ServerStatusAdapter extends ArrayAdapter<String> {

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
                textView.setText(server.getName());

                if (server.isOnline())
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_server_online, 0, 0, 0);
                else
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_server_offline, 0, 0, 0);
            }
            else
                textView.setText("Fehler beim Erstellen der Liste!");

            return textView;
            //return super.getView(position, convertView, parent);
        }
    }

    public boolean checkOnline(Server server) {
        boolean online = false;
        try {
            for (int i=0; i<5; i++) {
                if (ServerPortOpenChecker.isServerPortOpen(server.getDomain(), server.getPort())) {
                    online = true;
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return online;
    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private class RefreshListTask extends AsyncTask<Object,Object,Object> {

        private List<String> listNames = new ArrayList<String>();
        private List<Object> listObjects = new ArrayList<Object>();

        private RefreshListTask() {
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            DataBaseServer mDataBaseServer = new DataBaseServer();

            if (!isOnline())
                Toast.makeText(mContext, R.string.error_no_internet, Toast.LENGTH_SHORT).show();

            for (String owner : mDataBaseServer.getOwners(mContext))
            {
                String ownerName = Character.toUpperCase(owner.charAt(0)) + owner.substring(1);
                listNames.add(Character.toUpperCase(ownerName.charAt(0)) + ownerName.substring(1));
                listObjects.add(ownerName);
                for (Server server : mDataBaseServer.getAllServerByOwner(mContext, owner))
                {
                    server.setOnline(checkOnline(server));
                    listNames.add(server.getName());
                    listObjects.add(server);
                }
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {

            setListAdapter(new ServerStatusAdapter(mContext, R.layout.fragment_serverstatus_element, listNames, listObjects));
            setListShown(true);
            super.onProgressUpdate(values);
        }
    }
}
