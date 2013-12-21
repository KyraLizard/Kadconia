package de.dhbw.navigation;

import de.dhbw.infos.InfoFragment;
import de.dhbw.konto.KontoFragment;
import de.dhbw.links.LinkFragment;
import de.dhbw.player.PlayerFragment;
import de.dhbw.serverstatus.ServerStatusFragment;
import de.dhbw.settings.SettingsActivity;
import de.dhbw.vote.VoteFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;

public class NavigationActivity extends Activity {

    private Context mContext;
	private String[] mNavigationTitles;
	private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);

        //((ImageView)findViewById(android.R.id.home)).setPadding(7, 0, 11, 0);

        mContext = this;
		
		mNavigationTitles = getResources().getStringArray(R.array.nav_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.drawer_list);
        mDrawerList.setAdapter(new CustomExpandableListAdapter(this));
        
        // Set listeners for action bar drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
        	
        	@Override
        	public void onDrawerOpened(View drawerView) {
        		super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
        	}

        	@Override
        	public void onDrawerClosed(View drawerView) {
        		super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
        	}
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // Set the adapter for the list view
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.activity_navigation_drawer_list_item, mNavigationTitles));
        
        // Set the list's click listener
        mDrawerList.setOnGroupClickListener(new CustomOnGroupClickListener());
        //mDrawerList.setOnChildClickListener(new CustomOnChildClickListener());
	
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        //mDrawerList.performItemClick(mDrawerList.getAdapter().getView(0, null, null),0,mDrawerList.getAdapter().getItemId(0));
        mDrawerList.performItemClick(mDrawerList.getChildAt(0),0,mDrawerList.getAdapter().getItemId(0));
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // If the nav drawer is open, hide all action items except settings
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        for (int i=0; i<menu.size(); i++)
            menu.getItem(i).setVisible(!drawerOpen);

        MenuItem settingsMenuItem = menu.findItem(R.id.action_settings);
        if (settingsMenuItem != null)
            settingsMenuItem.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
	}

    private class CustomOnGroupClickListener implements ExpandableListView.OnGroupClickListener {

        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

            Fragment fragment = null;

            switch (i)
            {
                case 0:
                    fragment = new ServerStatusFragment();
                    break;
                case 1:
                    fragment = new VoteFragment();
                    break;
                case 2:
                    fragment = new TestFragment();
                    //fragment = new KontoFragment();
                    break;
                case 3:
                    fragment = new InfoFragment(mNavigationTitles[i]);
                    break;
                case 4:
                    fragment = new LinkFragment();
                    break;
                case 5:
                    Intent mIntent = new Intent(mContext, SettingsActivity.class);
                    startActivity(mIntent);
                    return true;
                default:
                    fragment = new TestFragment();
                    break;
            }

            // Insert the fragment by replacing any existing fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
            //mDrawerList.setItemChecked(position, true);
            setTitle(mNavigationTitles[i]);
            mDrawerLayout.closeDrawer(mDrawerList);
            mDrawerList.setItemChecked(i, false);
            return true;
        }
    }

    /*private class CustomOnChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {

            if ((expandableListView.getExpandableListAdapter()).getGroup(i).equals(getString(R.string.nav_player_online)))
            {
                Fragment fragment = new PlayerFragment(i2);

                // Insert the fragment by replacing any existing fragment
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();

                // Highlight the selected item, update the title, and close the drawer
                //mDrawerList.setItemChecked(position, true);
                setTitle(mContext.getResources().getStringArray(R.array.nav_elements_player_online)[i2]);
                mDrawerLayout.closeDrawer(mDrawerList);
                mDrawerList.setItemChecked(i, false);
                return true;
            }
            else
                return false;
        }
    }*/
	
	 @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent mIntent = new Intent(this, SettingsActivity.class);
                startActivity(mIntent);
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}
