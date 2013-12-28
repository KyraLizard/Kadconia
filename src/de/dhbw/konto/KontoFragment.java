package de.dhbw.konto;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.dhbw.database.DataBaseHelper;
import de.dhbw.database.DataBaseKontoEintraege;
import de.dhbw.database.Kontoeintrag;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 05.12.13.
 */

/* Javascript Code zum Auslesen

    if (document.getElementsByClassName('Box Title Error')[0] != undefined)
{
	console.log("Error");
}
else
{
	var rawElements = document.getElementsByClassName('hover');
	var finalArray = new Array(rawElements.length);
	for (var i=0; i<rawElements.length; i++)
	{
		finalArray[i] = new Array(rawElements[i].children.length-1);
		finalArray[i][0] = rawElements[i].children[0].innerText;
		finalArray[i][1] = rawElements[i].children[1].innerText;
		finalArray[i][2] = rawElements[i].children[2].className;
		finalArray[i][3] = rawElements[i].children[3].innerText.replace(',','.');
		finalArray[i][4] = rawElements[i].children[5].innerText;
		finalArray[i][5] = rawElements[i].children[6].innerText;
		finalArray[i][6] = rawElements[i].children[7].innerText;
		finalArray[i][7] = rawElements[i].children[8].innerText;
		finalArray[i][8] = rawElements[i].children[9].innerText;
	}
	console.log(JSON.stringify(finalArray));
}
 */
public class KontoFragment extends Fragment {

    private Context mContext;

    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_konto, null);

        mWebView = (WebView) view.findViewById(R.id.konto_webview);

        ListView listView = (ListView) view.findViewById(R.id.konto_list);
        List<Kontoeintrag> kontoeintragList = (new DataBaseKontoEintraege()).getAllKontoEintraege(new DataBaseHelper(mContext).getReadableDatabase());
        listView.setAdapter(new CustomKontoAdapter(mContext, R.layout.fragment_konto_element, kontoeintragList));

        return view;
        //return inflater.inflate(R.layout.fragment_konto, container, false);
    }

    private void showWarning() {
        new AlertDialog.Builder(mContext)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Aktualisieren")
                .setMessage("Falls die Daten das erste Mal oder vor einiger Zeit sychronisiert wurden, kann dies zu hohen Datenmengen beim Download führen. Fortfahren?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshList();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(mContext, "Aktualisierung abgebrochen.", Toast.LENGTH_LONG);
                    }
                })
                .show();
    }

    private void refreshList() {

        String token = PreferenceManager.getDefaultSharedPreferences(mContext).getString(getString(R.string.pref_konto_token_key), "error");
        if (token.equals("error"))
        {
            Toast.makeText(mContext, "Bitte gib dein Konto-Token in den Einstellungen ein.", Toast.LENGTH_LONG);
            return;
        }
        //TODO: Konto-Token validieren
        mWebView.loadUrl("bank.kadcon.de/?token="+token);
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
            textView.setText(String.valueOf(mKontoeintragList.get(position).getType() + ": " + mKontoeintragList.get(position).getBetrag()));
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