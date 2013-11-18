package de.dhbw.links;

import de.dhbw.database.DataBaseHelper;
import de.dhbw.database.DataBaseLinks;
import de.dhbw.database.Link;
import de.dhbw.navigation.R;

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

public class LinkFragment extends Fragment{
	
	private List<Link> mLinkList;
    private Context mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_link, null);
        mContext = getActivity();

        SQLiteDatabase mDataBase = (new DataBaseHelper(mContext)).getReadableDatabase();
        DataBaseLinks mDataBaseLinks = new DataBaseLinks();

		mLinkList = mDataBaseLinks.getAllLinks(mDataBase);

		GridView mGridView = (GridView) view.findViewById(R.id.link_layout);
		mGridView.setAdapter(new LinkAdapter(mLinkList));
		
		return mGridView;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public class LinkAdapter extends BaseAdapter
	{
		private List<Link> mLinkList;
		
		public LinkAdapter(List<Link> mLinkList) {
			this.mLinkList = mLinkList;
		}
		
		@Override
		public int getCount() {
			return mLinkList.size();
		}

		@Override
		public Object getItem(int position) {
			return mLinkList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mLinkList.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mGridViewElement = inflater.inflate(R.layout.fragment_link_list_item, parent, false);
			
            TextView mGridViewElementText = (TextView) mGridViewElement.findViewById(R.id.link_list_element_text);
            mGridViewElementText.setText(mLinkList.get(position).getName());

            int mImageId = getResources().getIdentifier(mLinkList.get(position).getImage(), "drawable", mContext.getPackageName());
            mGridViewElementText.setCompoundDrawablesWithIntrinsicBounds(0, mImageId, 0, 0);

            mGridViewElement.setOnClickListener(new LinkOnClickListener(mLinkList.get(position).getUrl()));
            
			return mGridViewElement;
		}

        public class LinkOnClickListener implements View.OnClickListener
        {
            private String mUrl;

            public LinkOnClickListener(String mUrl) {
                this.mUrl = mUrl;
            }

            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(Intent.ACTION_VIEW);
                mIntent.setData(Uri.parse(mUrl));
                startActivity(mIntent);
            }
        }
		
	}
}
