package de.dhbw.infos;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.dhbw.navigation.R;

/**
 * Created by Mark on 27.11.13.
 */
public class AdminFragment extends ListFragment {

    private Context mContext;
    private static final String KEY_CATEGORY = "Category";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();

        List<String> listElements = new ArrayList<String>();

        List<Rank> rankList = new ArrayList<Rank>();
        rankList.add(new Rank("Besitzer","owner.txt"));
        rankList.add(new Rank("Administratoren","admins.txt"));
        rankList.add(new Rank("Moderatoren","mods.txt"));

        for (Rank rank : rankList)
        {
            listElements.add(KEY_CATEGORY + rank.getName());

            for (String name : readFileToList(rank.getFile()))
                listElements.add(name);
        }

        setListAdapter(new AdminListAdapter(mContext,R.layout.fragment_admin_element,listElements));

        return super.onCreateView(inflater, container, savedInstanceState);
        //return inflater.inflate(R.layout.fragment_admin_element, container, false);
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