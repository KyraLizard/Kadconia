package de.dhbw.serverstatus;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.database.DataBaseServer;
import de.dhbw.database.Kontoeintrag;
import de.dhbw.database.Server;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 21.11.13.
 */
public class ServerStatusFragment extends ListFragment {

    private Context mContext;
    private ProgressBar mProgressBar;
    private String mOwner;

    public ServerStatusFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        setHasOptionsMenu(true);

        if (getArguments().getString(getString(R.string.bundle_key_serverstatus_owner)) != null)
            mOwner = getArguments().getString(getString(R.string.bundle_key_serverstatus_owner));
        else
            mOwner = savedInstanceState.getString(getString(R.string.bundle_key_serverstatus_owner));

        View view = inflater.inflate(R.layout.fragment_serverstatus, null);

        mProgressBar = (ProgressBar) view.findViewById(R.id.serverstatus_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        (new RefreshListTask(mOwner)).execute();

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.bundle_key_serverstatus_owner),mOwner);
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
                (new RefreshListTask(mOwner)).execute();
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

            if (mOwner.equals(getString(R.string.serverstatus_owner_kadcon)))
            {
                textView.getLayoutParams().height = 150;
                if (server.getServerInformation() != null)
                    textView.setText(textView.getText() + "\n" + server.getServerInformation());
            }
            else if (mOwner.equals(getString(R.string.serverstatus_owner_mojang)))
                ;

            return textView;
            //return super.getView(position, convertView, parent);
        }
    }
    private static HashMap getServerInformation(Server server) {
        try
        {
            String mServer = server.getDomain() + ":" + server.getPort();
            InputStream inputStream = new URL("http://minecraft-api.com/v1/get/?server=" + mServer).openStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));

            List<String> serverDataList = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                serverDataList.add(line);
            String data = "";
            for (String serverData : serverDataList)
                data += serverData;

            Gson gson = new Gson();
            return gson.fromJson(data, HashMap.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        private String mOwner;

        private RefreshListTask(String owner) {
            mOwner = owner;
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
                DataBaseServer mDataBaseServer = new DataBaseServer(mContext);
                int serverCount = mDataBaseServer.getServerCountByOwner(mOwner);

                if (mOwner.equals(getString(R.string.serverstatus_owner_kadcon)))
                {
                    for (Server server : mDataBaseServer.getAllServerByOwner(getString(R.string.serverstatus_owner_kadcon)))
                    {
                        mServerList.add(server);

                        HashMap JSON = getServerInformation(server);
                        if (JSON.get("status").equals(true))
                            server.setOnline(true);
                        LinkedTreeMap players = (LinkedTreeMap) JSON.get("players");
                        server.setServerInformation( ((Double)players.get("online")).intValue() + "/" + ((Double)players.get("max")).intValue() + " Spieler\n" + JSON.get("motd"));

                        mProgressBar.setProgress(mProgressBar.getProgress() + 100/serverCount + 1);
                    }
                }
                else if (mOwner.equals(getString(R.string.serverstatus_owner_mojang)))
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

                        for (Server server : mDataBaseServer.getAllServerByOwner(getString(R.string.serverstatus_owner_mojang)))
                        {
                            for (String serverStatus : serverStatusArray)
                            {
                                if (server.getDomain().equals(serverStatus.split(":")[0]) && serverStatus.split(":")[1].equals("green"))
                                    server.setOnline(true);
                            }
                            mServerList.add(server);
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
