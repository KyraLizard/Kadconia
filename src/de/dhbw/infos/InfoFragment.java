package de.dhbw.infos;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import de.dhbw.database.DataBaseHelper;
import de.dhbw.database.DataBaseInfos;
import de.dhbw.database.DataBaseLinks;
import de.dhbw.database.Info;
import de.dhbw.database.Link;
import de.dhbw.navigation.R;

public class InfoFragment extends Fragment{
	
	private List<Info> mInfoList;
    private Context mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_info, null);
        mContext = getActivity();

        SQLiteDatabase mDataBase = (new DataBaseHelper(mContext)).getReadableDatabase();
        DataBaseInfos mDataBaseInfos = new DataBaseInfos();

		mInfoList = mDataBaseInfos.getAllInfos(mDataBase);

		GridView mGridView = (GridView) view.findViewById(R.id.info_layout);
		mGridView.setAdapter(new InfoAdapter(mInfoList));
		
		return mGridView;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public class InfoAdapter extends BaseAdapter
	{
		private List<Info> mInfoList;
		
		public InfoAdapter(List<Info> mInfoList) {
			this.mInfoList = mInfoList;
		}
		
		@Override
		public int getCount() {
			return mInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mInfoList.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mGridViewElement = inflater.inflate(R.layout.fragment_info_item, parent, false);
			
            TextView mGridViewElementText = (TextView) mGridViewElement.findViewById(R.id.info_element_text);
            mGridViewElementText.setText(mInfoList.get(position).getName());

            //int mImageId = getResources().getIdentifier(mInfoList.get(position).getImage(), "drawable", mContext.getPackageName());
            mGridViewElementText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_link_ban, 0, 0);
            //mGridViewElement.setOnClickListener(new InfoOnClickListener(mInfoList.get(position).getId()));
            
			return mGridViewElement;
        }

        /*public class InfoOnClickListener implements View.OnClickListener
        {
            private int id;

            public InfoOnClickListener(int id) {
                this.id = id;
            }

            @Override
            public void onClick(View view) {

                Fragment fragment = null;

                switch (id)
                {
                    case 0:
                        fragment = new RulesFragment();
                    case 1:
                        fragment = new ServerIPFragment();
                    case 2:
                        fragment = new AdminListFragment();
                    case 3:
                        fragment = new DisclaimerFragment();
                    case 4:
                        fragment = new ImpressumFragment();
                    default:
                        return;
                }

                switchToFragment(fragment);
            }

            private void switchToFragment (Fragment fragment)
            {
                getFragmentManager().beginTransaction()
                                    .add(fragment, null)
                                    .addToBackStack(null)
                                    .commit();
            }
        }*/
		
	}
}
