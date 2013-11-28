package de.dhbw.vote;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 28.11.13.
 */
public class VoteFragment extends Fragment {

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();

        View view = inflater.inflate(R.layout.fragment_vote, container, false);

        WebView webView = (WebView) view.findViewById(R.id.vote_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        EditText nameField = (EditText) view.findViewById(R.id.vote_edittext_name);
        EditText captchaField = (EditText) view.findViewById(R.id.vote_edittext_captcha);

        nameField.setText("Name");  //TODO: Hier Name aus Einstellungen einfügen

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("TestOnPageFinished", url);

                view.loadUrl("javascript:(function(){var intervalID = setInterval(function() {\n" +
                        "\tif (document.getElementById('recaptcha_image') != undefined)\n" +
                        "\t{\n" +
                        "\t\talert(document.getElementById('recaptcha_image').firstChild.src);\n" +
                        "\t\tclearInterval(intervalID);\n" +
                        "\t}\n" +
                        "}, 1000);})()");
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                try
                {
                    Log.d("TestOnJSAlert", url);
                    ImageView imageView = (ImageView) view.findViewById(R.id.vote_image_captcha);
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(message).getContent());
                    imageView.setImageBitmap(bitmap);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.loadUrl("http://minecraft-server.eu/?go=servervote&id=2421");

        Log.d("TestLoadUrl", "Load URL");

        return view;
    }
}