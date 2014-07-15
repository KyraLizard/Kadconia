package de.dhbw.infos;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.dhbw.database.Admin;
import de.dhbw.database.DataBaseAdmins;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 27.11.13.
 */
public class AdminFragment extends ListFragment {

    private Context mContext;
    private List<Admin> mAdminList = new ArrayList<Admin>();
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

        setAdminList();
        if (mAdminList.isEmpty())
        {
            isRefreshLocked = true;
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);
            mWebView.loadUrl("http://forum.kadcon.de/team");
        }
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
                if (!isOnline())
                    Toast.makeText(mContext, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
                else
                {
                    if (!isRefreshLocked) {
                        isRefreshLocked = true;
                        mProgressBar.setProgress(0);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mWebView.loadUrl("http://forum.kadcon.de/team");
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshList() {

        mProgressBar.setVisibility(View.INVISIBLE);
        setAdminList();
        setListAdapter(new AdminListAdapter(mContext, mAdminList));
    }
    private void setAdminList() {

        mAdminList.clear();

        List<Admin> tempAdminList = (new DataBaseAdmins()).getAllAdmins(mContext);
        List<String> tempRankList = new ArrayList<String>();

        for (Admin admin : tempAdminList)
            if (!tempRankList.contains(admin.getRank()))
                tempRankList.add(admin.getRank());

        Collections.sort(tempRankList);

        for (String rank : tempRankList)
        {
            mAdminList.add(null);

            for (Admin admin : tempAdminList)
                if (admin.getRank().equals(rank))
                    mAdminList.add(admin);
        }
    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {

            view.loadUrl("javascript:(function(){var e=new Array;var t=document.getElementsByClassName('box48');for(var n=0;n<t.length;n++){var r=new Array(11);r[0]=t[n].children[1].children[0].children[0].children[0].text;r[1]=t[n].children[0].children[0].src;var i=t[n].children[1].children[0].children[0].children[1];if(i.className.indexOf('red')>-1)r[2]='Administrator';else if(i.className.indexOf('blue')>-1)r[2]='Moderator';else if(i.className.indexOf('green')>-1)r[2]='Berater';else if(i.className.indexOf('none')>-1)r[2]='Test-Moderatoren';r[3]=i.textContent;var s=t[n].children[1].children[1].children;for(o=0;o<s.length;o++){if(s[o].textContent.indexOf('Mitglied seit')>-1)r[4]=s[o].textContent.replace('Mitglied seit ','');else if(s[o].textContent.indexOf('aus')>-1)r[5]=s[o].textContent.replace('aus ','');else if(s[o].textContent=='Männlich'||s[o].textContent=='Weiblich')r[6]=s[o].textContent;else r[7]=s[o].textContent}r[8]=t[n].children[1].children[3].children[1].textContent.replace('.','');r[9]=t[n].children[1].children[3].children[3].textContent.replace('.','');r[10]=t[n].children[1].children[3].children[5].textContent.replace('.','');for(var o=0;o<r.length;o++)if(r[o]==undefined)r[o]='';e.push(r)}WebApp.refreshAdmins(JSON.stringify(e));})()");
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
            dbAdmins.deleteAllEntries(new DataBaseAdmins().getWritableDatabase(mContext));

            for (ArrayList<String> tempAdminList : (ArrayList<ArrayList<String>>) gson.fromJson(adminsList, type))
            {
                String name = tempAdminList.get(0);
                String image = tempAdminList.get(1);
                String rank = tempAdminList.get(2);
                String detailedRank = tempAdminList.get(3);
                long date = (new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN).parse(tempAdminList.get(4))).getTime();
                String location = tempAdminList.get(5);
                String gender = tempAdminList.get(6);
                int age;
                if (tempAdminList.get(7).isEmpty())
                    age = -1;
                else
                    age = Integer.parseInt(tempAdminList.get(7));
                int postCount = Integer.parseInt(tempAdminList.get(8));
                int likeCount = Integer.parseInt(tempAdminList.get(9));
                int points = Integer.parseInt(tempAdminList.get(10));

                Admin admin = new Admin(name, image, rank, detailedRank, date, location,
                                        gender, age, postCount, likeCount, points);

                dbAdmins.addAdmin(mContext, admin);
            }

            isRefreshLocked = false;

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshList();
                }
            });
        }
    }
    private class AdminListAdapter extends ArrayAdapter<Admin> {

        private List<Admin> mListItems;

        public AdminListAdapter(Context context, List<Admin> objects) {
            super(context, R.layout.fragment_admin_element, objects);
            mListItems = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_admin_element, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.admin_element_text);

            if (mListItems.get(position) == null)
            {
                textView.setBackgroundResource(R.drawable.background_border);
                textView.setText(mListItems.get(position+1).getRank());
                textView.setGravity(Gravity.CENTER);
            }
            else
                textView.setText(mListItems.get(position).getName());

            return view;
            //return super.getView(position, convertView, parent);
        }
    }
}