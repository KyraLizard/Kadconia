package de.dhbw.player;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 05.12.13.
 */
public class PlayerFragment extends Fragment {
    /*
    Javascript-Code zum Ausgeben der Spielernamen (map.kadcon.de)
    var playerList = Object.keys(DynMap.prototype.players);
    console.log(JSON.stringify(playerList));
     */

    private Context mContext;
    private ListView mListView;
    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_player, null);

        mListView = (ListView) view.findViewById(R.id.player_list);

        mWebView = (WebView) view.findViewById(R.id.player_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new PlayerWebInterface(), "WebApp");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                Log.d("Test", "onPageFinished");
                view.loadUrl("javascript:(function(){var playerList = Object.keys(DynMap.prototype.players);\n" +
                        "WebApp.setList(JSON.stringify(playerList));})()");
                super.onPageFinished(view, url);
            }
        });

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
                Log.d("Test", "loadUrl");
                mWebView.loadUrl("http://map.kadcon.de");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class PlayerWebInterface {

        private PlayerWebInterface() {
        }

        @JavascriptInterface
        public void setList(String playerArray) {

            Log.d("Test", "setList");
            Log.d("PlayerList", "---"+playerArray+"---");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            List<String> playerList = gson.fromJson(playerArray, type);
            mListView.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_expandable_list_item_1, playerList));
        }
    }
}