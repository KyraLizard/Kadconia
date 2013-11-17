package de.dhbw.links;

import java.util.ArrayList;

import de.dhbw.navigation.R;
import de.dhbw.navigation.R.array;
import de.dhbw.navigation.R.id;
import de.dhbw.navigation.R.layout;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

public class LinkFragment extends Fragment{
	
	private String[] mLinkList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_link, null);
		
		mLinkList = getResources().getStringArray(R.array.link_array);
		GridView mGridView = (GridView) view.findViewById(R.id.link_layout);
		mGridView.setAdapter(new LinkAdapter(mLinkList));
		
		return mGridView;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public class LinkAdapter extends BaseAdapter
	{
		private String[] mLinkList;
		
		public LinkAdapter(String[] mLinkList) {
			this.mLinkList = mLinkList;
		}
		
		@Override
		public int getCount() {
			return mLinkList.length;
		}

		@Override
		public Object getItem(int position) {
			return mLinkList[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mGridViewElement = inflater.inflate(R.layout.fragment_link_list_item, parent, false);
			
            TextView mGridViewElementText = (TextView) mGridViewElement.findViewById(R.id.link_list_element_text);
            mGridViewElementText.setText(mLinkList[position]);
            mGridViewElementText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_launcher, 0, 0);
            
			return mGridViewElement;
		}
		
	}
}
