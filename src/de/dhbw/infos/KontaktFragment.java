package de.dhbw.infos;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 28.11.13.
 */
public class KontaktFragment extends Fragment {

    public KontaktFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] kontaktArray = getResources().getStringArray(R.array.kontakt);
        String kontaktText = "";
        for (String aKontaktArray : kontaktArray)
            kontaktText += aKontaktArray + "\n\n";

        View view = inflater.inflate(R.layout.fragment_kontakt, container, false);
        TextView textView = (TextView) view.findViewById(R.id.kontakt_text);
        textView.setText(kontaktText);

        return view;
    }
}