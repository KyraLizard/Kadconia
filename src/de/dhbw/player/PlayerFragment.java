package de.dhbw.player;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 05.12.13.
 */
public class PlayerFragment extends Fragment {

    private Context mContext;
    private ListView mListView;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_player, null);

        mProgressBar = (ProgressBar) view.findViewById(R.id.player_progress);
        mProgressBar.setVisibility(View.GONE);

        mListView = (ListView) view.findViewById(R.id.player_list);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        List<String> playerList = new ArrayList<String>(sharedPreferences.getStringSet(getString(R.string.pref_player_list_key), new HashSet<String>()));
        if (playerList.size() == 0)
        {
            List<String> defaultList = new ArrayList<String>();
            defaultList.add("Diese Liste ist leer.");
            defaultList.add("Bitte aktualisiere die Liste.");
            mListView.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, defaultList));
        }
        else
        {
            Collections.sort(playerList);
            playerList.set(0, playerList.size() + " Spieler online" + "\n" + playerList.get(0));
            mListView.setAdapter(new CustomListAdapter(mContext, android.R.layout.simple_list_item_1, playerList));
        }

        mWebView = (WebView) view.findViewById(R.id.player_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new PlayerWebInterface(), "WebApp");
        mWebView.setWebViewClient(new CustomWebViewClient());

        mWebView.setWebChromeClient(new CustomWebChromeClient());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.playeronline, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_refresh:
                if (!isOnline())
                    Toast.makeText(mContext, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
                else
                {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mWebView.loadUrl("http://map.kadcon.de");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress((int)(0.75*newProgress));
            super.onProgressChanged(view, newProgress);
        }
    }
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {

            view.loadUrl("javascript:(function(){\n" +
                    "var i=0;\n" +
                    "var intervalId = setInterval(function(){\n" +
                    "i++;\n" +
                    "console.log(i);" +
                    "var playerList = Object.keys(DynMap.prototype.players);\n" +
                    "var string = JSON.stringify(playerList);\n" +
                    "if (string != '[]')\n" +
                    "{\n" +
                    "clearInterval(intervalId);\n" +
                    "WebApp.setList(JSON.stringify(playerList));\n" +
                    "}\n" +
                    "}, 1000);\n" +
                    "})()");
            super.onPageFinished(view, url);
        }
    }
    private class PlayerWebInterface {

        private PlayerWebInterface() {
        }

        @JavascriptInterface
        public void setList(String playerArray) {

            mProgressBar.setProgress(87);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            final List<String> playerList = new ArrayList<String>();
            for (String element : (List<String>) gson.fromJson(playerArray, type))
                playerList.add(Character.toUpperCase(element.charAt(0)) + element.substring(1));

            Collections.sort(playerList);
            playerList.add(0, "(Stand: " + ((new SimpleDateFormat("dd.MM.yy HH:mm:ss")).format(new Date())) + ")");

            Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.putStringSet(getString(R.string.pref_player_list_key), new HashSet<String>(playerList));
            editor.commit();

            playerList.set(0, playerList.size() + " Spieler online" + "\n" + playerList.get(0));

            mWebView.loadUrl("");

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mProgressBar.setProgress(100);
                    mProgressBar.setVisibility(View.GONE);
                    mListView.setAdapter(new CustomListAdapter(mContext, android.R.layout.simple_list_item_1, playerList));
                }
            });
        }
    }
    private class CustomListAdapter extends ArrayAdapter<String> {
        private List<String> mObjectList;

        private CustomListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjectList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(mObjectList.get(position));

            if (mObjectList.get(position).contains("Spieler online"))
            {
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.drawable.background_border);
                textView.setTextSize(15);
            }

            return view;
        }
    }
}