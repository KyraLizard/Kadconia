package de.dhbw.navigation;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

public class LinkFragment extends Fragment{
	
	private String[] mLinkList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_link, null);
		
		mLinkList = getResources().getStringArray(R.array.link_array);
		ArrayList<View> mViewList;
		
		for (int i=0; i < mLinkList.length; i++)
		{
			//TODO: Views in Liste schreiben
		}
		
		GridLayout mGridLayout = (GridLayout) view.findViewById(R.id.link_layout);
		//mGridLayout.a
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
