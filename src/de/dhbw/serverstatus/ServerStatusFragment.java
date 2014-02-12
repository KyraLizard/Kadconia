package de.dhbw.serverstatus;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.database.DataBaseServer;
import de.dhbw.database.Server;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 21.11.13.
 */
public class ServerStatusFragment extends ListFragment {

    private Context mContext;
    private ProgressBar mProgressBar;
    private String mOwner;

    public ServerStatusFragment(String owner) {
        this.mOwner = owner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_serverstatus, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.serverstatus_progress);

        mProgressBar.setVisibility(View.VISIBLE);

        if (mOwner.equals(getString(R.string.serverstatus_owner_kadcon)))
            (new KadconRefreshListTask()).execute();
        else if (mOwner.equals(getString(R.string.serverstatus_owner_mojang)))
            (new MojangRefreshListTask()).execute();

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.serverstatus, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_refresh:
                mProgressBar.setVisibility(View.VISIBLE);
                if (mOwner.equals(getString(R.string.serverstatus_owner_kadcon)))
                    (new KadconRefreshListTask()).execute();
                else if (mOwner.equals(getString(R.string.serverstatus_owner_mojang)))
                    (new MojangRefreshListTask()).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ServerStatusAdapter extends ArrayAdapter<Server> {

        private List<Server> mServerList;

        public ServerStatusAdapter(Context context, int resource, List<Server> serverList) {
            super(context, resource, serverList);
            this.mServerList = serverList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_serverstatus_element, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.server_element_text);

            Server server = mServerList.get(position);
            textView.setText(server.getName());

            if (server.isOnline())
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_server_online, 0, 0, 0);
            else
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_server_offline, 0, 0, 0);

            return textView;
            //return super.getView(position, convertView, parent);
        }
    }

    private boolean checkOnline(Server server) {
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
    private class KadconRefreshListTask extends AsyncTask<Object,Object,Object> {

        private List<Server> mServerList = new ArrayList<Server>();

        private KadconRefreshListTask() {
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            DataBaseServer mDataBaseServer = new DataBaseServer(mContext);
            int serverCount = mDataBaseServer.getServerCount(mContext);

            if (!isOnline())
            {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                for (Server server : mDataBaseServer.getAllServerByOwner(getString(R.string.serverstatus_owner_kadcon)))
                {
                    server.setOnline(checkOnline(server));
                    mServerList.add(server);
                    mProgressBar.setProgress(mProgressBar.getProgress() + 100/serverCount + 1);
                }
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {

            setListAdapter(new ServerStatusAdapter(mContext, R.layout.fragment_serverstatus_element, mServerList));
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.setProgress(0);
            super.onProgressUpdate(values);
        }
    }
    private class MojangRefreshListTask extends AsyncTask<Object,Object,Object> {

        private List<Server> mServerList = new ArrayList<Server>();
        private DataBaseServer mDataBaseServer = new DataBaseServer(mContext);

        private MojangRefreshListTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mServerList = mDataBaseServer.getAllServerByOwner(getString(R.string.serverstatus_owner_mojang));
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if (!isOnline())
            {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                try
                {
                    URL url = new URL("http://status.mojang.com/check");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String serverStatusString = reader.readLine();
                    serverStatusString = serverStatusString.replace("[", "");
                    serverStatusString = serverStatusString.replace("]", "");
                    serverStatusString = serverStatusString.replace("{", "");
                    serverStatusString = serverStatusString.replace("}", "");
                    serverStatusString = serverStatusString.replace("\"", "");

                    String[] serverStatusArray = serverStatusString.split(",");

                    for (Server server : mServerList)
                    {
                        for (String serverStatus : serverStatusArray)
                        {
                            if (server.getDomain().equals(serverStatus.split(":")[0]) && serverStatus.split(":")[1].equals("green"))
                                server.setOnline(true);
                        }
                    }

                    int serverCount = mDataBaseServer.getServerCount(mContext);
                    mProgressBar.setProgress(mProgressBar.getProgress() + 100/serverCount + 1);
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {

            setListAdapter(new ServerStatusAdapter(mContext, R.layout.fragment_serverstatus_element, mServerList));
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.setProgress(0);
            super.onProgressUpdate(values);
        }
    }
}
