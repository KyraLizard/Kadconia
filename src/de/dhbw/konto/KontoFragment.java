package de.dhbw.konto;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.dhbw.database.DataBaseKontoEintraege;
import de.dhbw.database.Kontoeintrag;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 05.12.13.
 */

public class KontoFragment extends ListFragment {

    // b38eac06f56188a4

    private Context mContext;
    private WebView mWebView;
    private TextView mPageTextView;
    private ImageView mPagePrev;
    private ProgressBar mKontoProgressBar;
    private TextView mDateTextView;

    private int mCurrentPage;
    private int mReloadStatus = 0;  //0=kein Reload, 1=Login-Seite wird geladen, 2=Daten-Seite wird geladen
    private boolean areButtonsLocked = false;
    private List<Kontoeintrag> mKontoeintragList;

    public KontoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        mContext = getActivity();View view = inflater.inflate(R.layout.fragment_konto, null);

        mWebView = (WebView) view.findViewById(R.id.konto_webview);
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.setWebChromeClient(new CustomWebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(), "WebApp");

        mPageTextView = (TextView) view.findViewById(R.id.konto_page_text);
        mPagePrev = (ImageView) view.findViewById(R.id.konto_page_prev);
        ImageView mPageNext = (ImageView) view.findViewById(R.id.konto_page_next);

        mDateTextView = (TextView) view.findViewById(R.id.konto_date);
        mKontoProgressBar = (ProgressBar) view.findViewById(R.id.konto_progress);
        mKontoProgressBar.setProgress(0);

        //Lade Datum der letzten Aktualisierung aus SharedPreferences (Default = "Noch nicht aktualisiert")
        mDateTextView.setText(PreferenceManager.getDefaultSharedPreferences(mContext).getString(getString(R.string.pref_konto_date), "Noch nicht aktualisiert..."));

        //Lade Seitenzahl aus SharedPreferences in Variable (Default = 0)
        mCurrentPage = PreferenceManager.getDefaultSharedPreferences(mContext).getInt(getString(R.string.pref_konto_page),-1);
        if (mCurrentPage < 0)
        {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.putInt(getString(R.string.pref_konto_page),1);
            editor.commit();
            mCurrentPage = 1;
        }

        mPagePrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (areButtonsLocked)
                    return;

                if (mCurrentPage > 1) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                    editor.putInt(getString(R.string.pref_konto_page),mCurrentPage-1);
                    editor.commit();
                    mCurrentPage--;
                    refreshList();
                }
            }
        });

        mPageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (areButtonsLocked)
                    return;

                int anzEintraege = (new DataBaseKontoEintraege()).getKontoEintraegeCount(mContext);

                //Wenn für die neue Seite nicht genug Einträge vorhanden sind...
                if (mCurrentPage == 1 && anzEintraege == 0)
                {
                    reloadData();
                }
                else if (mCurrentPage+1 > anzEintraege / 50)
                {
                    if (anzEintraege%250 == 0)
                    {
                        loadData(anzEintraege / 250);
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                        editor.putInt(getString(R.string.pref_konto_page),mCurrentPage+1);
                        editor.commit();
                        mCurrentPage++;
                    }
                    else
                        Toast.makeText(mContext,"Keine Einträge mehr vorhanden",Toast.LENGTH_LONG).show();
                }
                else
                {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                    editor.putInt(getString(R.string.pref_konto_page),mCurrentPage+1);
                    editor.commit();
                    mCurrentPage++;
                    refreshList();
                }
            }
        });

        refreshList();

        return view;
        //return inflater.inflate(R.layout.fragment_konto, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Fragment fragment = new KontoeintragFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.bundle_key_kontoeintrag), (new Gson()).toJson(mKontoeintragList.get(position)));
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(((Activity) mContext).getActionBar().getTitle().toString());
        fragmentTransaction.commit();

        ((Activity) mContext).getActionBar().setTitle("Kontoeintrag #" + (new DecimalFormat("#000")).format(mKontoeintragList.get(position).getId()));

        super.onListItemClick(l, v, position, id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_refresh:
                reloadData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.konto, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void reloadData() {

        String token = PreferenceManager.getDefaultSharedPreferences(mContext).getString(getString(R.string.pref_konto_token_key), "error");
        if (!isOnline())
            Toast.makeText(mContext, getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        else if (token.equals("error"))
            Toast.makeText(mContext, "Bitte gib dein Konto-Token in den Einstellungen ein.", Toast.LENGTH_LONG).show();
        else
        {
            mReloadStatus = 1;

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.putInt(getString(R.string.pref_konto_page),1);
            editor.commit();
            mCurrentPage = 1;

            (new DataBaseKontoEintraege()).deleteAllData(mContext);

            mKontoProgressBar.setProgress(0);
            mKontoProgressBar.setVisibility(View.VISIBLE);
            mWebView.loadUrl("http://bank.kadcon.de/?token="+token);
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
    private void refreshList() {

        mKontoProgressBar.setVisibility(View.INVISIBLE);
        mDateTextView.setText(PreferenceManager.getDefaultSharedPreferences(mContext).getString(getString(R.string.pref_konto_date), "Noch nicht aktualisiert..."));

        if (mCurrentPage <= 1)
            mPagePrev.setImageResource(0);
        else
            mPagePrev.setImageResource(R.drawable.ic_action_previous_item);

        mPageTextView.setText("Seite " + mCurrentPage);

        try
        {
            mKontoeintragList = (new DataBaseKontoEintraege()).getKontoEintraege(mContext, 50, mCurrentPage);
        }
        catch (IllegalArgumentException e)
        {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.putInt(getString(R.string.pref_konto_page),1);
            editor.commit();
            mCurrentPage = 1;
            mKontoeintragList = (new DataBaseKontoEintraege()).getKontoEintraege(mContext, 50, mCurrentPage);
            Toast.makeText(mContext, "Leider ist etwas schiefgelaufen. Die Seite wird neu geladen.", Toast.LENGTH_LONG).show();
        }

        setListAdapter(new CustomKontoAdapter(mContext, R.layout.fragment_konto_element, mKontoeintragList));
        areButtonsLocked = false;
    }
    private void loadData(int page) {

        String token = PreferenceManager.getDefaultSharedPreferences(mContext).getString(getString(R.string.pref_konto_token_key), "error");
        if (!isOnline())
            Toast.makeText(mContext, getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        else if (token.equals("error"))
            Toast.makeText(mContext, "Bitte gib dein Konto-Token in den Einstellungen ein.", Toast.LENGTH_LONG).show();
        else
        {
            areButtonsLocked = true;
            mKontoProgressBar.setProgress(0);
            mKontoProgressBar.setVisibility(View.VISIBLE);

            if (page == 0)
                mWebView.loadUrl("http://bank.kadcon.de/index.php?limit=250");
            else
                mWebView.loadUrl("http://bank.kadcon.de/index.php?limit=250&last_limit_start=" + (page-1)*250 + "&next_page=true");
        }
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {

            if (!url.contains("limit"))
            {
                if (url.contains("login"))
                {
                    mKontoProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "Das Konto-Token in den Einstellungen ist ungültig!", Toast.LENGTH_LONG).show();
                    areButtonsLocked = false;
                }
                else
                {
                    mReloadStatus = 2;
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadData((new DataBaseKontoEintraege()).getKontoEintraegeCount(mContext) / 250);
                        }
                    });
                }
            }
            else
            {
                view.loadUrl("javascript:(function(){var rawElements = document.getElementsByClassName('hover');" +
                        "var finalArray = new Array(rawElements.length);" +
                        "for (var i=0; i<rawElements.length; i++)" +
                        "{" +
                        "finalArray[i] = new Array(rawElements[i].children.length-1);" +
                        "finalArray[i][0] = rawElements[i].children[0].innerText;" +
                        "finalArray[i][1] = rawElements[i].children[1].innerText;" +
                        "finalArray[i][2] = rawElements[i].children[2].className;" +
                        "finalArray[i][3] = rawElements[i].children[3].innerText.replace(',','.');" +
                        "finalArray[i][4] = rawElements[i].children[5].innerText;" +
                        "finalArray[i][5] = rawElements[i].children[6].innerText;" +
                        "finalArray[i][6] = rawElements[i].children[7].innerText;" +
                        "finalArray[i][7] = rawElements[i].children[8].innerText;" +
                        "finalArray[i][8] = rawElements[i].children[9].innerText;" +
                        "}" +
                        "WebApp.addKontoeintraegeFromJSON(JSON.stringify(finalArray));" +
                        "})()");
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(mContext, "Fehler beim Laden der Website", Toast.LENGTH_SHORT).show();
            areButtonsLocked = false;
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
    private class CustomWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            switch (mReloadStatus)
            {
                case 0:
                    mKontoProgressBar.setProgress((int)0.7*newProgress);
                    break;
                case 1:
                    mKontoProgressBar.setProgress((int)0.35*newProgress);
                    break;
                case 2:
                    mKontoProgressBar.setProgress((int)(35+0.35*newProgress));
                    break;
            }
            super.onProgressChanged(view, newProgress);
        }
    }
    private class WebAppInterface {

        @JavascriptInterface
        public void addKontoeintraegeFromJSON(String kontoeintraegeArray) throws ParseException {

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayList<String>>>(){}.getType();
            DataBaseKontoEintraege dbKonto = new DataBaseKontoEintraege();
            int counter = 1;
            for (ArrayList<String> kontoeintrag : (ArrayList<ArrayList<String>>) gson.fromJson(kontoeintraegeArray, type))
            {
                long date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(kontoeintrag.get(0)).getTime();
                double betrag = Double.parseDouble(kontoeintrag.get(3));
                if (kontoeintrag.get(2).equals("negative"))
                    betrag *= -1;
                double newSaldo = Double.parseDouble(kontoeintrag.get(8));
                Kontoeintrag newEintrag = new Kontoeintrag(date, kontoeintrag.get(1),
                        betrag, kontoeintrag.get(4), kontoeintrag.get(5), kontoeintrag.get(6), kontoeintrag.get(7), newSaldo);
                    dbKonto.addKontoEintrag(mContext, newEintrag);
                mKontoProgressBar.setProgress(70+30*counter/250);
                counter++;
            }
            if (mReloadStatus == 2)
            {
                mReloadStatus = 0;
                String dateString = "Zuletzt aktualisiert am " + (new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")).format(new Date()) + " Uhr";
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                editor.putString(getString(R.string.pref_konto_date), dateString);
                editor.commit();
            }
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshList();
                }
            });
        }
    }
    private class CustomKontoAdapter extends ArrayAdapter<Kontoeintrag> {

        private List<Kontoeintrag> mKontoeintragList;

        private CustomKontoAdapter(Context context, int resource, List<Kontoeintrag> objects) {
            super(context, resource, objects);
            mKontoeintragList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(android.R.layout.simple_list_item_1, null);

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            DecimalFormat df = new DecimalFormat("#0.00");
            if (mKontoeintragList.get(position).getBetrag() >= 0)
            {
                textView.setText(String.valueOf(mKontoeintragList.get(position).getType() + ": +" + df.format(mKontoeintragList.get(position).getBetrag())));
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_konto_money,0,0,0);
            }
            else
            {
                textView.setText(String.valueOf(mKontoeintragList.get(position).getType() + ": " + df.format(mKontoeintragList.get(position).getBetrag())));
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_konto_item, 0, 0, 0);
            }
            textView.setCompoundDrawablePadding(10);

            return view;
            //return super.getView(position, convertView, parent);
        }
    }
}