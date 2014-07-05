package de.dhbw.serverstatus;

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
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.dhbw.database.DataBaseServer;
import de.dhbw.database.Server;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 21.11.13.
 */
public class ServerStatusMojangFragment extends ListFragment {

    private Context mContext;
    private ProgressBar mProgressBar;

    public ServerStatusMojangFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_serverstatus_mojang, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.serverstatus_mojang_progress);

        (new RefreshListTask()).execute();

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
                (new RefreshListTask()).execute();
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
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    private class RefreshListTask extends AsyncTask<Object,Object,Object> {

        private List<Server> mServerList = new ArrayList<Server>();

        @Override
        protected void onPreExecute() {

            if (!isOnline())
            {
                Toast.makeText(mContext, R.string.error_no_internet, Toast.LENGTH_LONG).show();
                cancel(true);
            }
            else
                mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            DataBaseServer mDataBaseServer = new DataBaseServer(mContext);
            int serverCount = mDataBaseServer.getServerCountByOwner(getString(R.string.serverstatus_owner_mojang));

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

                for (Server server : mDataBaseServer.getAllServerByOwner(getString(R.string.serverstatus_owner_mojang)))
                {
                    for (String serverStatus : serverStatusArray)
                    {
                        if (server.getDomain().equals(serverStatus.split(":")[0]) && serverStatus.split(":")[1].equals("green"))
                            server.setOnline(true);
                    }
                    mServerList.add(server);
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {

            setListAdapter(new ServerStatusAdapter(mContext, R.layout.fragment_serverstatus_element, mServerList));
            mProgressBar.setVisibility(View.INVISIBLE);
            super.onProgressUpdate(values);
        }
    }
}
