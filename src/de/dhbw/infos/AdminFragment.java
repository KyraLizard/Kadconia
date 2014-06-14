package de.dhbw.infos;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import de.dhbw.database.Admin;
import de.dhbw.database.DataBaseAdmins;
import de.dhbw.database.DataBaseKontoEintraege;
import de.dhbw.database.Kontoeintrag;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 27.11.13.
 */
public class AdminFragment extends ListFragment {

    private Context mContext;
    private static final String KEY_CATEGORY = "Category";
    private List<Rank> rankList = new ArrayList<Rank>();
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private boolean isRefreshLocked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info_admin, null);

        setHasOptionsMenu(true);
        mContext = getActivity();

        mWebView = (WebView) view.findViewById(R.id.info_admin_webview);
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.setWebChromeClient(new CustomWebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(), "WebApp");

        mProgressBar = (ProgressBar) view.findViewById(R.id.info_admin_progress);

        rankList.add(new Rank("Besitzer","owner.txt"));
        rankList.add(new Rank("Administratoren","admins.txt"));
        rankList.add(new Rank("Super-Moderatoren", "smods.txt"));
        rankList.add(new Rank("Moderatoren","mods.txt"));
        rankList.add(new Rank("Test-Moderatoren", "tmods.txt"));

        boolean isRankEmpty = true;
        for (Rank rank : rankList)
        {
            if (PreferenceManager.getDefaultSharedPreferences(mContext).getStringSet(rank.getFile().split("\\.")[0] + "List", new HashSet<String>()).size() != 0)
            {
                isRankEmpty = false;
                break;
            }
        }

        if (isRankEmpty)
            (new NetworkTask()).execute();
        else
            refreshList();

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
        //return inflater.inflate(R.layout.fragment_admin_element, container, false);
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
                if (!isRefreshLocked)
                {
                    isRefreshLocked = true;
                    new NetworkTask().execute();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshList() {

        List<String> listElements = new ArrayList<String>();

        for (Rank rank : rankList)
        {
            listElements.add(KEY_CATEGORY + rank.getName());

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            HashSet<String> memberNamesSet = (HashSet<String>) sharedPreferences.getStringSet(rank.getFile().split("\\.")[0] + "List", new HashSet<String>());
            if (memberNamesSet.size() > 0)
            {
                List<String> memberNames = new ArrayList<String>(memberNamesSet);
                for (String memberName : memberNames)
                    listElements.add(memberName);
            }
            else
                listElements.add(getString(R.string.list_empty));
        }

        setListAdapter(new AdminListAdapter(mContext, listElements));
    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    private class NetworkTask extends AsyncTask<Object,Object,Object>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isOnline())
            {
                isRefreshLocked = false;
                cancel(true);
            }
            else
            {
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            refreshList();
            mProgressBar.setVisibility(View.INVISIBLE);
            isRefreshLocked = false;
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object... objects) {

            String url = "https://dl.dropboxusercontent.com/u/62033432/Kadconia-Dateien/";
            try
            {
                for (Rank rank : rankList)
                {
                    List<String> stringList = new ArrayList<String>();
                    InputStream inputStream = new URL(url+rank.getFile()).openStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
                    String line;
                    //Read File Line By Line
                    while ((line = bufferedReader.readLine()) != null)
                        stringList.add(line);

                    //Close the input stream
                    dataInputStream.close();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                    editor.putStringSet(rank.getFile().split("\\.")[0]+"List", new HashSet<String>(stringList));
                    editor.commit();
                    mProgressBar.setProgress(mProgressBar.getProgress()+(100/rankList.size()));
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {

            view.loadUrl("javascript:(function(){var e=new Array;var t=document.getElementsByClassName('box48');for(var n=0;n<t.length;n++){var r=new Array(11);r[0]=t[n].children[1].children[0].children[0].children[0].text;r[1]=t[n].children[0].children[0].src;var i=t[n].children[1].children[0].children[0].children[1];if(i.className.indexOf('red')>-1)r[2]='Administrator';else if(i.className.indexOf('blue')>-1)r[2]='Moderator';else if(i.className.indexOf('green')>-1)r[2]='Berater';r[3]=i.textContent;var s=t[n].children[1].children[1].children;for(o=0;o<s.length;o++){if(s[o].textContent.indexOf('Mitglied seit')>-1)r[4]=s[o].textContent.replace('Mitglied seit ','');else if(s[o].textContent.indexOf('aus')>-1)r[5]=s[o].textContent.replace('aus ','');else if(s[o].textContent=='Männlich'||s[o].textContent=='Weiblich')r[6]=s[o].textContent;else r[7]=s[o].textContent}r[8]=t[n].children[1].children[3].children[1].textContent.replace('.','');r[9]=t[n].children[1].children[3].children[3].textContent.replace('.','');r[10]=t[n].children[1].children[3].children[5].textContent.replace('.','');for(var o=0;o<r.length;o++)if(r[o]==undefined)r[o]='';e.push(r)}console.log(e)})()");

            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(mContext, "Fehler beim Laden der Website", Toast.LENGTH_SHORT).show();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
    private class CustomWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            mProgressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }
    private class WebAppInterface {

        @JavascriptInterface
        public void refreshAdmins(String adminsList) throws ParseException {

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayList<String>>>(){}.getType();
            DataBaseAdmins dbAdmins = new DataBaseAdmins();
            for (ArrayList<String> adminList : (ArrayList<ArrayList<String>>) gson.fromJson(adminsList, type))
            {
                String name = adminList.get(0);
                String image = adminList.get(1);
                String rank = adminList.get(2);
                String detailedRank = adminList.get(3);
                long date = (new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN).parse(adminList.get(4))).getTime();
                String location = adminList.get(5);
                int membershipDate = Integer.parseInt(adminList.get(6));
                String gender = adminList.get(7);
                int postCount = Integer.parseInt(adminList.get(8));
                int likeCount = Integer.parseInt(adminList.get(9));
                int points = Integer.parseInt(adminList.get(10));

                Admin admin = new Admin(name, image, rank, detailedRank, date, location,
                                        membershipDate, gender, postCount, likeCount, points);

                dbAdmins.addAdmin(mContext, admin);
            }
        }
    }

    private class AdminListAdapter extends ArrayAdapter<String> {

        private List<String> mListItems;

        public AdminListAdapter(Context context, List<String> objects) {
            super(context, R.layout.fragment_admin_element, objects);
            mListItems = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_admin_element, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.admin_element_text);

            if (mListItems.get(position).contains(KEY_CATEGORY))
            {
                textView.setBackgroundResource(R.drawable.background_border);
                textView.setText(mListItems.get(position).replace(KEY_CATEGORY, ""));
                textView.setGravity(Gravity.CENTER);
            }
            else
                textView.setText(mListItems.get(position));

            return view;
            //return super.getView(position, convertView, parent);
        }
    }
}