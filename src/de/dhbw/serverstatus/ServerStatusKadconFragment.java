package de.dhbw.serverstatus;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class ServerStatusKadconFragment extends ListFragment {

    private Context mContext;
    private ProgressBar mProgressBar;
    private ImageView mImageView;

    public ServerStatusKadconFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_serverstatus_kadcon, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.serverstatus_kadcon_progress);
        mImageView = (ImageView) view.findViewById(R.id.serverstatus_kadcon_image);

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

            textView.getLayoutParams().height = 150;
            if (server.getServerInformation() != null)
                textView.setText(textView.getText() + "\n" + server.getServerInformation());

            return textView;
            //return super.getView(position, convertView, parent);
        }
    }
    private static StatusResponse getServerInformation(Server server) {

        try
        {
            ServerListPing17 serverPing = new ServerListPing17();
            serverPing.setAddress(new InetSocketAddress(server.getDomain(), server.getPort()));

            return serverPing.fetchData();
        }
        catch (IOException e)
        {
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
        private String faviconString = "";

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
            int serverCount = mDataBaseServer.getServerCountByOwner(getString(R.string.serverstatus_owner_kadcon));

            for (Server server : mDataBaseServer.getAllServerByOwner(getString(R.string.serverstatus_owner_kadcon)))
            {
                mServerList.add(server);

                StatusResponse statusResponse = getServerInformation(server);

                if (statusResponse != null)
                {
                    server.setOnline(true);
                    String motd = statusResponse.getDescription().replaceAll("§.", "");
                    server.setServerInformation(statusResponse.getPlayers().getOnline() + "/" + statusResponse.getPlayers().getMax() + " Spieler\n" + motd);
                    mProgressBar.setProgress(mProgressBar.getProgress() + 100/serverCount + 1);
                    faviconString = statusResponse.getFavicon();
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

            //Set favicon
            if (faviconString != null && !faviconString.isEmpty())
            {
                String faviconDataBytes = faviconString.substring(faviconString.indexOf(",") + 1);
                InputStream stream = new ByteArrayInputStream(Base64.decode(faviconDataBytes.getBytes(), Base64.DEFAULT));
                Bitmap faviconBitmap = BitmapFactory.decodeStream(stream);
                mImageView.setImageBitmap(faviconBitmap);
            }

            super.onProgressUpdate(values);
        }
    }
}
