package de.dhbw.vote;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import de.dhbw.navigation.R;

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
    private ProgressBar mProgressBar;
    private ImageView mImageView;

    private EditText mCaptchaField;
    private EditText mNameField;

    private TextView mHintText;

    // http://developer.android.com/training/keyboard-input/style.html

    public VoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        mContext = getActivity();

        mView = inflater.inflate(R.layout.fragment_vote, container, false);

        mProgressBar = (ProgressBar) mView.findViewById(R.id.vote_progress);
        mProgressBar.setVisibility(View.VISIBLE);
        mImageView = (ImageView) mView.findViewById(R.id.vote_image_captcha);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setVisibility(View.GONE);

        Button voteButton = (Button) mView.findViewById(R.id.vote_button_submit);
        voteButton.setOnClickListener(new VoteOnClickListener());

        mWebView = (WebView) mView.findViewById(R.id.vote_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(mContext), "WebApp");

        mCaptchaField = (EditText) mView.findViewById(R.id.vote_edittext_captcha);
        mCaptchaField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mNameField.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });

        mNameField = (EditText) mView.findViewById(R.id.vote_edittext_name);
        mNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO)
                {
                    closeKeyboard();
                    mView.findViewById(R.id.vote_button_submit).performClick();
                    handled = true;
                }
                return handled;
            }
        });

        mHintText = (TextView) mView.findViewById(R.id.vote_text_hint);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String voteName = sharedPreferences.getString(getString(R.string.pref_vote_name_key),"");

        mNameField.setText(voteName);
        mHintText.setText(getString(R.string.vote_text_hint_default));

        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.setWebChromeClient(new CustomWebChromeClient());

        reloadPage();

        return mView;
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mNameField.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    private void reloadPage() {

        if (!isOnline())
            Toast.makeText(mContext, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        else
        {
            mHintText.setText(getString(R.string.vote_text_hint_default));
            mCaptchaField.setText("");
            submitButtonLock = true;
            mWebpageState = 1;
            mProgressBar.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
            mWebView.loadUrl("http://minecraft-server.eu/?go=servervote&id=2421");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_refresh:
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

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private class CustomWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            mProgressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {

            if (mWebpageState == 1) {
                view.loadUrl("javascript:(function(){var i=0;    \n" +
                        "var intervalID = setInterval(function()  \n" +
                        "{ \n" +
                        "if (document.getElementById('recaptcha_image') != undefined) \n" +
                        "{ \n" +
                        "WebApp.loadImage(document.getElementById('recaptcha_image').firstChild.src); \n" +
                        "clearInterval(intervalID); \n" +
                        "}\n" +
                        "else \n" +
                        "{ \n" +
                        "i++; \n" +
                        "if (i>3)\n" +
                        "{\n" +
                        "WebApp.showText('Captcha-Bild konnte nicht geladen werden');\n" +
                        "clearInterval(intervalID); \n" +
                        "}\n" +
                        "} \n" +
                        "}, 1000);})();");
            } else if (mWebpageState == 3) {
                mWebView.loadUrl("javascript:(function()\n" +
                        "{\n" +
                        "if (document.getElementsByClassName('alert alert-error')[0] == undefined)\n" +
                        "{\n" +
                        "if (document.getElementsByClassName('hero-unit')[0] == undefined)\n" +
                        "WebApp.showText('Unbekannter Fehler!');\n" +
                        "else\n" +
                        "WebApp.showText(document.getElementsByClassName('hero-unit')[0].childNodes[0].textContent.trim())\n" +
                        "}\n" +
                        "else\n" +
                        "WebApp.showText(document.getElementsByClassName('alert alert-error')[0].textContent);\n" +
                        "})()");
                mWebpageState = 4;
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mHintText.setText("Fehler beim Laden der Homepage");
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
    private class VoteOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            closeKeyboard();
            if (submitButtonLock)
                Toast.makeText(mContext, "Das Bild ist noch nicht geladen", Toast.LENGTH_SHORT).show();
            else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                String voteName = sharedPreferences.getString(getString(R.string.pref_vote_name_key),"");
                if (voteName.isEmpty())
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_vote_name_key), mNameField.getText().toString());
                    editor.commit();
                }
                mImageView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                mWebView.loadUrl("javascript:(function(){document.getElementById('recaptcha_response_field').value = '" + mCaptchaField.getText() + "';" +
                        "document.getElementsByName('minecraftname')[0].value = '" + mNameField.getText() + "';" +
                        "document.forms[1].submit();})()");
                mWebpageState = 3;
            }
        }
    }
    private class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void loadImage(String url) {
            new DownloadImageTask().execute(url);
        }

        @JavascriptInterface
        public void showText(String text) {
            //Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            final String toastText = text;
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHintText.setText(toastText);
                }
            });
        }

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

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
            submitButtonLock = false;
            mWebpageState = 2;
            mImageView.setImageBitmap(result);
            mImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}