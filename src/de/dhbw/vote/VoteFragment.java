package de.dhbw.vote;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
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
import android.widget.Toast;

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
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();

        mView = inflater.inflate(R.layout.fragment_vote, container, false);

        WebView webView = (WebView) mView.findViewById(R.id.vote_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(mContext), "WebApp");

        EditText nameField = (EditText) mView.findViewById(R.id.vote_edittext_name);
        EditText captchaField = (EditText) mView.findViewById(R.id.vote_edittext_captcha);

        nameField.setText("Name");  //TODO: Hier Name aus Einstellungen einfügen

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("TestOnPageFinished", url);

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
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl("http://minecraft-server.eu/?go=servervote&id=2421");
        Log.d("TestLoadUrl", "Load URL");

        return mView;
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
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
            Log.d("Test", "Task closed");
        }
    }
}