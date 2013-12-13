package de.dhbw.konto;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import de.dhbw.database.DataBaseHelper;
import de.dhbw.database.DataBaseKontoEintraege;
import de.dhbw.database.Kontoeintrag;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 05.12.13.
 */
public class KontoFragment extends Fragment {

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_konto, null);

        WebView webView = (WebView) view.findViewById(R.id.konto_webview);

        ListView listView = (ListView) view.findViewById(R.id.konto_list);
        List<Kontoeintrag> kontoeintragList = (new DataBaseKontoEintraege()).getAllKontoEintraege(new DataBaseHelper(mContext).getReadableDatabase());
        listView.setAdapter(new CustomKontoAdapter(mContext, R.layout.fragment_konto_element, kontoeintragList));

        return inflater.inflate(R.layout.fragment_konto, container, false);
    }

    private class CustomKontoAdapter extends ArrayAdapter<Kontoeintrag> {

        private List<Kontoeintrag> mKontoeintragList;

        private CustomKontoAdapter(Context context, int resource, List<Kontoeintrag> objects) {
            super(context, resource, objects);
            mKontoeintragList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return super.getView(position, convertView, parent);
        }
    }
}