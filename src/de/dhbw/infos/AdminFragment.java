package de.dhbw.infos;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 27.11.13.
 */
public class AdminFragment extends ListFragment {

    private Context mContext;
    private static final String KEY_CATEGORY = "Category";
    private List<Rank> rankList = new ArrayList<Rank>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info_admin, null);

        setHasOptionsMenu(true);
        mContext = getActivity();

        rankList.add(new Rank("Besitzer","owner.txt"));
        rankList.add(new Rank("Administratoren","admins.txt"));
        rankList.add(new Rank("Super-Moderatoren", "smods.txt"));
        rankList.add(new Rank("Moderatoren","mods.txt"));
        rankList.add(new Rank("Test-Moderatoren", "tmods.txt"));

        updateList();

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
        //return inflater.inflate(R.layout.fragment_admin_element, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vote, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_refresh:
                Log.e("Test", "Enter");
                new NetworkTask().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateList() {

        List<String> listElements = new ArrayList<String>();

        for (Rank rank : rankList)
        {
            listElements.add(KEY_CATEGORY + rank.getName());

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            HashSet<String> memberNamesSet = (HashSet<String>) sharedPreferences.getStringSet(rank.getFile().split("\\.")[0] + "List", new HashSet<String>());
            if (memberNamesSet.size() > 0)
            {
                List<String> memberNames = new ArrayList<String>(memberNamesSet);
                for (String memberName : memberNames)
                    listElements.add(memberName);
            }
            else
                listElements.add("Liste leer!");
            /*for (String name : readFileToList(rank.getFile()))
                listElements.add(name);
            */
        }

        setListAdapter(new AdminListAdapter(mContext,R.layout.fragment_admin_element,listElements));
    }

    private class NetworkTask extends AsyncTask<Object,Object,Object>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("Test", "Pre");
            //TODO: Ladebalken einblenden
        }

        @Override
        protected void onPostExecute(Object o) {

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateList();
                }
            });
            //TODO: Ladebalken ausblenden
            Log.e("Test", "Post");
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object... objects) {

            Log.e("Test", "Background");
            String url = "https://dl.dropboxusercontent.com/u/62033432/Kadconia-Dateien/";
            try
            {
                for (Rank rank : rankList)
                {
                    List<String> stringList = new ArrayList<String>();
                    InputStream inputStream = new URL(url+rank.getFile()).openStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
                    String line;
                    //Read File Line By Line
                    while ((line = bufferedReader.readLine()) != null)
                        stringList.add(line);

                    //Close the input stream
                    dataInputStream.close();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                    editor.putStringSet(rank.getFile().split("\\.")[0]+"List", new HashSet<String>(stringList));
                    editor.commit();
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class AdminListAdapter extends ArrayAdapter<String> {

        private List<String> mListItems;

        public AdminListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mListItems = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_admin_element, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.admin_element_text);

            if (mListItems.get(position).contains(KEY_CATEGORY))
            {
                textView.setBackgroundResource(R.drawable.background_border);
                textView.setText(mListItems.get(position).replace(KEY_CATEGORY, ""));
                textView.setGravity(Gravity.CENTER);
            }
            else
                textView.setText(mListItems.get(position));

            return view;
            //return super.getView(position, convertView, parent);
        }
    }

    public List<String> readFileToList(String path) {

        List<String> stringList = new ArrayList<String>();
        try
        {
            // Open the file that is the first command line parameter
            InputStream fstream = getActivity().getAssets().open(path);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)
                stringList.add(strLine);
            //Close the input stream
            in.close();
        }
        catch (Exception e)
        {
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        return stringList;
    }
}