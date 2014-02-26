package de.dhbw.navigation;

import de.dhbw.infos.InfoFragment;
import de.dhbw.konto.KontoFragment;
import de.dhbw.links.LinkFragment;
import de.dhbw.serverstatus.ServerStatusFragment;
import de.dhbw.settings.SettingsFragment;
import de.dhbw.vote.VoteFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

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

        findViewById(android.R.id.home).setPadding(7, 0, 11, 0);

        mContext = this;
		
		mNavigationTitles = getResources().getStringArray(R.array.nav_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.drawer_list);
        mDrawerList.setAdapter(new CustomExpandableListAdapter(this));

        collapseAllGroups();
        
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
                collapseAllGroups();
                invalidateOptionsMenu();
        	}
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // Set the list's click listener
        mDrawerList.setOnGroupClickListener(new CustomOnGroupClickListener());
        mDrawerList.setOnChildClickListener(new CustomOnChildClickListener());
	
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerList.performItemClick(null,0,0); //Click to open Serverstatus Group
        mDrawerList.performItemClick(null,1,0); //Click to choose Kadcon
        collapseAllGroups();
	}

    private void collapseAllGroups() {

        if (mDrawerList != null)
        {
            for (int i=0; i<mDrawerList.getExpandableListAdapter().getGroupCount(); i++)
                mDrawerList.collapseGroup(i);
        }
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

            if ((expandableListView.getExpandableListAdapter()).getGroup(i).equals(getString(R.string.nav_serverstatus)))
                return false;
            else
            {
                Fragment fragment;

                switch (i)
                {
                    /*case 0:
                        fragment = new ServerStatusFragment();
                        break;*/
                    case 1:
                        fragment = new VoteFragment();
                        break;
                    case 2:
                        fragment = new KontoFragment();
                        break;
                    case 3:
                        fragment = new InfoFragment();
                        break;
                    case 4:
                        fragment = new LinkFragment();
                        break;
                    case 5:
                        fragment = new SettingsFragment();
                        break;
                    default:
                        fragment = new VoteFragment();
                        break;
                }

                // Insert the fragment by replacing any existing fragment
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                if (getFragmentManager().findFragmentById(R.id.content_frame) != null && getActionBar().getTitle() != null)  //Prevents empty activity window from being added to BackStack
                    ft.addToBackStack(getActionBar().getTitle().toString());
                ft.commit();

                // Highlight the selected item, update the title, and close the drawer
                //mDrawerList.setItemChecked(position, true);
                getActionBar().setTitle(mNavigationTitles[i]);
                mDrawerLayout.closeDrawer(mDrawerList);
                mDrawerList.setItemChecked(i, false);
                return true;
            }
        }
    }
    private class CustomOnChildClickListener implements ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {

            if ((expandableListView.getExpandableListAdapter()).getGroup(i).equals(getString(R.string.nav_serverstatus)))
            {
                Fragment fragment = new ServerStatusFragment();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.bundle_key_serverstatus_owner), expandableListView.getExpandableListAdapter().getChild(i,i2).toString());
                fragment.setArguments(bundle);

                String[] mChildNavigationTitles = mContext.getResources().getStringArray(R.array.nav_elements_serverstatus);

                // Insert the fragment by replacing any existing fragment
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                if (getFragmentManager().findFragmentById(R.id.content_frame) != null)  //Prevents empty activity window from being added to BackStack
                    ft.addToBackStack(getActionBar().getTitle().toString());
                ft.commit();

                // Highlight the selected item, update the title, and close the drawer
                //mDrawerList.setItemChecked(position, true);
                getActionBar().setTitle(mChildNavigationTitles[i2]);
                mDrawerLayout.closeDrawer(mDrawerList);
                mDrawerList.setItemChecked(i2, false);
                return true;
            }
            else
                return false;
        }
    }

    @Override
    public void onBackPressed() {

        int backStackCount = getFragmentManager().getBackStackEntryCount();
        if (backStackCount >= 1)
            getActionBar().setTitle(getFragmentManager().getBackStackEntryAt(backStackCount-1).getName());
        super.onBackPressed();
    }

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

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}
