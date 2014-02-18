package de.dhbw.infos;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.dhbw.database.DataBaseInfos;
import de.dhbw.database.Info;
import de.dhbw.navigation.R;

public class InfoFragment extends Fragment{

    private String mTitle;
	private List<Info> mInfoList;
    private Context mContext;

    public InfoFragment() {
    }

    public InfoFragment(String title) {
        mTitle = title;
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_info, null);
        mContext = getActivity();

		mInfoList = (new DataBaseInfos()).getAllInfos(mContext);

		GridView mGridView = (GridView) view.findViewById(R.id.info_layout);
		mGridView.setAdapter(new InfoAdapter(mInfoList));
		
		return mGridView;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}

    @Override
    public void onResume() {
        //((Activity) mContext).getActionBar().setTitle(mTitle);
        super.onResume();
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

            ImageView mGridViewElementIcon = (ImageView) mGridViewElement.findViewById(R.id.info_element_icon);
            int mImageId = getResources().getIdentifier(mInfoList.get(position).getImage(), "drawable", mContext.getPackageName());
            mGridViewElementIcon.setImageResource(mImageId);

            mGridViewElement.setOnClickListener(new InfoOnClickListener(mInfoList.get(position).getId()));
            
			return mGridViewElement;
        }

        public class InfoOnClickListener implements View.OnClickListener
        {
            private int id;

            public InfoOnClickListener(int id) {
                this.id = id;
            }

            @Override
            public void onClick(View view) {

                Fragment fragment;

                switch (id)
                {
                    case 1:
                        fragment = new RulesFragment();
                        break;
                    case 2:
                        fragment = new ServerInfoFragment();
                        break;
                    case 3:
                        fragment = new AdminFragment();
                        break;
                    case 4:
                        fragment = new KontaktFragment();
                        break;
                    default:
                        fragment = new RulesFragment();
                        break;
                }

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack(((Activity) mContext).getActionBar().getTitle().toString());
                fragmentTransaction.commit();

                for (Info info : mInfoList)
                    if (info.getId() == id)
                        ((Activity) mContext).getActionBar().setTitle(info.getName());
            }
        }
	}
}
