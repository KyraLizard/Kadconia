package de.dhbw.konto;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import de.dhbw.database.Kontoeintrag;
import de.dhbw.navigation.R;

/**
 * Created by Mark on 23.02.14.
 */
public class KontoeintragFragment extends Fragment {

    private Kontoeintrag mKontoeintrag;

    public KontoeintragFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kontoeintrag, null);
        TextView textView = (TextView) view.findViewById(R.id.kontoeintrag_text);

        Type type = new TypeToken<Kontoeintrag>(){}.getType();
        if (getArguments().getString(getString(R.string.bundle_key_kontoeintrag)) != null)
            mKontoeintrag = (new Gson()).fromJson(getArguments().getString(getString(R.string.bundle_key_kontoeintrag)), type);
        else
            mKontoeintrag = (new Gson()).fromJson(savedInstanceState.getString(getString(R.string.bundle_key_kontoeintrag)), type);


        String text = "Datum: " + new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(mKontoeintrag.getDate());
        text += "\nEigenes Konto: " + mKontoeintrag.getUserKontoName();
        text += "\nBetrag: " + (new DecimalFormat("#0.00")).format(mKontoeintrag.getBetrag()) + " Kadis";
        text += "\nFremdes Konto: " + mKontoeintrag.getPartnerKontoName();
        text += "\nTyp: " + mKontoeintrag.getType();
        text += "\nBetreff: " + mKontoeintrag.getItem();
        text += "\nServer: " + mKontoeintrag.getServer();
        text += "\nNeuer Kontostand: " + (new DecimalFormat("#0.00")).format(mKontoeintrag.getNewSaldo()) + " Kadis";
        textView.setText(text);

        return view;
        //return inflater.inflate(R.layout.fragment_kontoeintrag, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.bundle_key_kontoeintrag), (new Gson()).toJson(mKontoeintrag));
    }
}