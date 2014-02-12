package de.dhbw.serverstatus;

import android.app.Activity;
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

import de.dhbw.database.DataBaseHelper;
import de.dhbw.database.DataBaseServer;
import de.dhbw.database.Server;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 21.11.13.
 */
public class MojangServerStatusFragment extends ListFragment {

    private Context mContext;
    private ProgressBar mProgressBar;

    private static final String OWNER = "mojang";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_serverstatus, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.serverstatus_progress);

        mProgressBar.setVisibility(View.VISIBLE);
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
                mProgressBar.setVisibility(View.VISIBLE);
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

        private RefreshListTask() {
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            DataBaseServer mDataBaseServer = new DataBaseServer();
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
                try
                {
                    URL url = new URL("http://status.mojang.com/check");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String[] serverStatusArray = reader.readLine().split(",");
                    String[] serverNameArray = {"minecraft.net","account.mojang.com","authserver.mojang.com","sessionserver.mojang.com","skins.minecraft.net"};

                    for (String serverName : serverNameArray)
                    {
                        Server mojangServer = new Server(firstCharToUpperCase(serverName.replace(".mojang.com","").replace(".minecraft.net","")));
                        mojangServer.setOnline(false);

                        for (String serverStatus : serverStatusArray)
                        {
                            serverStatus = serverStatus.replace("[", "");
                            serverStatus = serverStatus.replace("]", "");
                            serverStatus = serverStatus.replace("{", "");
                            serverStatus = serverStatus.replace("}", "");
                            serverStatus = serverStatus.replace("\"", "");

                            if (serverName.equals(serverStatus.split(":")[0]))
                                mojangServer.setOnline(serverStatus.split(":")[1].equals("green"));
                        }

                        mServerList.add(mojangServer);
                    }

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

        public String firstCharToUpperCase(String string) {
            return Character.toUpperCase(string.charAt(0))+string.substring(1);
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
