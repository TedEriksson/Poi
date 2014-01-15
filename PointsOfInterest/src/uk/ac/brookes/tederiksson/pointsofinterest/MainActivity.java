package uk.ac.brookes.tederiksson.pointsofinterest;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends FragmentActivity {
	
	private String[] fragmentTitles;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	
	 private CharSequence drawerTitle;
	 private CharSequence title;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.drawer);

	    title = drawerTitle = getTitle();
	    
	    fragmentTitles = getResources().getStringArray(R.array.fragment_titles);
	    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    drawerList = (ListView) findViewById(R.id.left_drawer);
	    
	    drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, fragmentTitles));
	    
	    drawerList.setOnItemClickListener(new DrawerItemClickListener());
	    
	    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_closed) {
	    	
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
	    };
	    
	    drawerLayout.setDrawerListener(drawerToggle);
	    
	    getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        if (savedInstanceState == null) {
            selectItem(0);
        }

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
          return true;
        }

        return super.onOptionsItemSelected(item);
    }
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
	
	private void selectItem(int position) {
	    // Create a new fragment and specify the planet to show based on position
	    Fragment fragment = null;
	    
	    switch(position) {
	    case 1:
	    	fragment = new PoiMapFragment();
	    	break;
	    }
	    
	    if(fragment==null)
	    	fragment = new PoiHomeFragment();
	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	    			   .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
	                   .replace(R.id.content_frame, fragment)
	                   .commit();

	    // Highlight the selected item, update the title, and close the drawer
	    drawerList.setItemChecked(position, true);
	    setTitle(fragmentTitles[position]);
	    drawerLayout.closeDrawer(drawerList);
	}
	
	@Override
	public void setTitle(CharSequence title) {
	    this.title = title;
	    getActionBar().setTitle(this.title);
	}

}
