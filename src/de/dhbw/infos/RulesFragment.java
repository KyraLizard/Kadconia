package de.dhbw.infos;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 22.11.13.
 */
public class RulesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rules, container, false);

        WebView rulesWebView = (WebView) view.findViewById(R.id.rules_web);
        rulesWebView.loadUrl("file:///android_asset/rules.html");

        return view;
    }

}