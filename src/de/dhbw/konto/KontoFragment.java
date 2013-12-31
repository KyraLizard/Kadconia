package de.dhbw.konto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.dhbw.database.DataBaseKontoEintraege;
import de.dhbw.database.Kontoeintrag;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 05.12.13.
 */

public class KontoFragment extends Fragment {

    public static final int LIST_ELEMENTS_PER_PAGE = 100;

    private Context mContext;
    private WebView mWebView;
    private ListView mListView;
    private TextView mPageTextView;
    private ImageView mPagePrev;
    private ImageView mPageNext;

    private int currentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_konto, null);

        mWebView = (WebView) view.findViewById(R.id.konto_webview);
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(), "WebApp");

        mPageTextView = (TextView) view.findViewById(R.id.konto_page_text);
        mPagePrev = (ImageView) view.findViewById(R.id.konto_page_prev);
        mPageNext = (ImageView) view.findViewById(R.id.konto_page_next);

        mListView = (ListView) view.findViewById(R.id.konto_list);
        refreshList();

        mPagePrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage >= 2)
                {
                    currentPage--;
                    refreshList();
                }
            }
        });

        mPageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage <= (new DataBaseKontoEintraege()).getKontoEintraegeCount(mContext) / LIST_ELEMENTS_PER_PAGE)
                {
                    currentPage++;
                    refreshList();
                }
            }
        });

        return view;
        //return inflater.inflate(R.layout.fragment_konto, container, false);
    }

    private void refreshList() {
        List<Kontoeintrag> kontoeintragList = (new DataBaseKontoEintraege()).getKontoEintraege(mContext, LIST_ELEMENTS_PER_PAGE, currentPage);
        mListView.setAdapter(new CustomKontoAdapter(mContext, R.layout.fragment_konto_element, kontoeintragList));

        if (currentPage == 1)
            mPagePrev.setImageResource(0);
        else
            mPagePrev.setImageResource(R.drawable.ic_action_previous_item);

        if (currentPage == (new DataBaseKontoEintraege()).getKontoEintraegeCount(mContext) / LIST_ELEMENTS_PER_PAGE + 1)
            mPageNext.setImageResource(0);
        else
            mPageNext.setImageResource(R.drawable.ic_action_next_item);

        mPageTextView.setText("Seite " + currentPage + "/" + ((new DataBaseKontoEintraege()).getKontoEintraegeCount(mContext) / LIST_ELEMENTS_PER_PAGE + 1));
    }

    private void showWarning() {
        new AlertDialog.Builder(mContext)
                .setTitle("Aktualisieren")
                .setMessage("Falls die Daten das erste Mal oder vor einiger Zeit sychronisiert wurden, kann dies zu hohen Datenmengen beim Download führen. Fortfahren?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshData();
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(mContext, "Aktualisierung abgebrochen.", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    private void refreshData() {

        //TODO: Remove; Just for debugging
        //String token = PreferenceManager.getDefaultSharedPreferences(mContext).getString(getString(R.string.pref_konto_token_key), "error");
        String token = "b38eac06f56188a4";  //b38eac06f56188a4
        if (token.equals("error"))
            Toast.makeText(mContext, "Bitte gib dein Konto-Token in den Einstellungen ein.", Toast.LENGTH_LONG).show();
        else
            mWebView.loadUrl("http://bank.kadcon.de/?token="+token);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_refresh:
                showWarning();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.konto, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {

            Log.d("Test", "OnPageFinished, URL: " + url);
            if (!url.contains("limit"))
            {
                if (url.contains("login"))
                    Toast.makeText(mContext, "Token ungültig.", Toast.LENGTH_SHORT).show();
                else
                    view.loadUrl("http://bank.kadcon.de/index.php?limit=250");
            }
            else
            {
                view.loadUrl("javascript:(function(){if (document.getElementsByClassName('Box Title Error')[0] != undefined)" +
                        "{" +
                        "WebApp.error();" +
                        "}" +
                        "else" +
                        "{" +
                        "var rawElements = document.getElementsByClassName('hover');" +
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
                        "}})()");
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(mContext, "Fehler beim Laden der Website", Toast.LENGTH_SHORT).show();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
    private class WebAppInterface {

        private int loadedPage = 1; //Seite, die gerade ausgelesen wird

        @JavascriptInterface
        public void error() {
            Toast.makeText(mContext, "Daten sind aktuell.", Toast.LENGTH_SHORT).show();
            refreshList();
        }

        @JavascriptInterface
        public void addKontoeintraegeFromJSON(String kontoeintraegeArray) throws ParseException {

            boolean isUpToDate = false;
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayList<String>>>(){}.getType();
            DataBaseKontoEintraege dbKonto = new DataBaseKontoEintraege();
            for (ArrayList<String> kontoeintrag : (ArrayList<ArrayList<String>>) gson.fromJson(kontoeintraegeArray, type))
            {
                long date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(kontoeintrag.get(0)).getTime();
                double betrag = Double.parseDouble(kontoeintrag.get(3));
                if (kontoeintrag.get(2).equals("negative"))
                    betrag *= -1;
                double newSaldo = Double.parseDouble(kontoeintrag.get(8));
                Kontoeintrag newEintrag = new Kontoeintrag(date, kontoeintrag.get(1),
                        betrag, kontoeintrag.get(4), kontoeintrag.get(5), kontoeintrag.get(6), kontoeintrag.get(7), newSaldo);
                if (dbKonto.isKontoeintragInDatabase(mContext, newEintrag))
                {
                    isUpToDate = true;
                    loadedPage = 1;
                    Toast.makeText(mContext, "Alle Daten ausgelesen", Toast.LENGTH_SHORT).show();
                    break;
                }
                else
                    dbKonto.addKontoEintrag(mContext, newEintrag);
            }
            if (!isUpToDate)
            {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Test", "Not UpToDate, Page " + loadedPage);
                        //TODO: Remove next if, just for debugging
                        if (loadedPage > 2)
                        {
                            loadedPage = 1;
                            Log.d("Test", "Debug Target");
                            return;
                        }
                        mWebView.loadUrl("http://bank.kadcon.de/index.php?limit=250&last_limit_start=" + (loadedPage-1)*250 + "&next_page=true");
                        loadedPage++;
                    }
                });
            }
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
            textView.setText(sdf.format(mKontoeintragList.get(position).getDate()) + "\n" + String.valueOf(mKontoeintragList.get(position).getType() + ": " + df.format(mKontoeintragList.get(position).getBetrag())));
            if (mKontoeintragList.get(position).getBetrag() >= 0)
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_konto_money,0,0,0);
            else
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_konto_item,0,0,0);
            textView.setCompoundDrawablePadding(10);

            return view;
            //return super.getView(position, convertView, parent);
        }
    }
}