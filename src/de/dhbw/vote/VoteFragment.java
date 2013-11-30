package de.dhbw.vote;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.dhbw.navigation.R;
import de.dhbw.settings.SettingsActivity;

/**
 * Created by Mark on 28.11.13.
 */
public class VoteFragment extends Fragment {

    private Context mContext;
    private View mView;
    private boolean submitButtonLock;
    private int mWebpageState = 0;
        /*  0: Start,
            1: Seite wird geladen,
            2: Seite (und Bild) ist geladen,
            3: Bewertungsskript wird geladen,
            4: Message ausgegeben
         */

    private WebView mWebView;

    private EditText mCaptchaField;
    private EditText mNameField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        mContext = getActivity();

        mView = inflater.inflate(R.layout.fragment_vote, container, false);

        mWebView = (WebView) mView.findViewById(R.id.vote_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(mContext), "WebApp");

        mCaptchaField = (EditText) mView.findViewById(R.id.vote_edittext_captcha);
        mNameField = (EditText) mView.findViewById(R.id.vote_edittext_name);

        mNameField.setText("Vettel1");  //TODO: Hier Name aus Einstellungen einfügen

        Button voteButton = (Button) mView.findViewById(R.id.vote_button_submit);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submitButtonLock)
                    Toast.makeText(mContext, "Das Bild ist noch nicht geladen", Toast.LENGTH_LONG).show();
                else {
                    Log.d("Test", "Submit-Button mit geladenem Bild gedrückt");
                    String test = "javascript:(function(){document.getElementById('recaptcha_response_field').value = '" + mCaptchaField.getText() + "';" +
                            "document.getElementsByName('mcname')[0].value = '" + mNameField.getText() + "';" +
                            "document.forms[1].submit();})()";
                    Log.d("TestCode", test);
                    mWebView.loadUrl("javascript:(function(){document.getElementById('recaptcha_response_field').value = '" + mCaptchaField.getText() + "';" +
                            "document.getElementsByName('mcname')[0].value = '" + mNameField.getText() + "';" +
                            "document.forms[1].submit();})()");
                    mWebpageState = 3;
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("TestOnPageFinished", url);

               if (mWebpageState == 1) {
                    view.loadUrl("javascript:(function(){var i=0;\n" +
                            "var intervalID = setInterval(function() \n" +
                            "{\n" +
                            "if (document.getElementById('recaptcha_image') != undefined)\n" +
                            "{\n" +
                            "WebApp.loadImage(document.getElementById('recaptcha_image').firstChild.src);\n" +
                            "clearInterval(intervalID);\n" +
                            "}\n" +
                            "else\n" +
                            "{\n" +
                            "i++;\n" +
                            "if (i>3)\n" +
                            "WebApp.error();\n" +
                            "}\n" +
                            "}, 1000);})()");
                } else if (mWebpageState == 3) {
                    mWebView.loadUrl("javascript:(function(){WebApp.showToast(document.getElementsByClassName('ui-state-error ui-corner-all')[0].childNodes[1].childNodes[3].textContent);})()");
                    mWebpageState = 4;
                }
                super.onPageFinished(view, url);
            }
        });

        reloadPage();
        Log.d("TestLoadUrl", "Load URL");

        return mView;
    }

    public void reloadPage() {
        submitButtonLock = true;
        mWebpageState = 1;
        mWebView.loadUrl("http://minecraft-server.eu/?go=servervote&id=2421");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_refresh:
                ImageView imageView = (ImageView) mView.findViewById(R.id.vote_image_captcha);
                imageView.setImageResource(0);
                imageView.setBackgroundResource(R.drawable.background_border);
                reloadPage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vote, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        public void loadImage(String url) {
            ImageView imageView = (ImageView) mView.findViewById(R.id.vote_image_captcha);
            new DownloadImageTask(imageView).execute(url);
            imageView.setBackgroundResource(0);
        }

        public void error() {
            ImageView imageView = (ImageView) mView.findViewById(R.id.vote_image_captcha);
            imageView.setImageResource(R.drawable.ic_link_ban);
            imageView.setBackgroundResource(0);
            Toast.makeText(mContext, "Captcha-Bild konnte nicht geladen werden", Toast.LENGTH_LONG).show();
        }

        public void showToast(String text) {
            Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView bmImage;

        public DownloadImageTask(ImageView imageView) {
            this.bmImage = imageView;
        }

        protected Bitmap doInBackground(String... urls) {

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "image download error");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            //set image of your imageview
            bmImage.setImageBitmap(result);
            submitButtonLock = false;
            mWebpageState = 2;
            Log.d("Test", "Task closed");
        }
    }
}