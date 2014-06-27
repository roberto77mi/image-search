package rf.demo.uberimagesearch;

import static rf.demo.uberimagesearch.Constant.LOG_TAG;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;


/**
 * Shows one fragment that loads images from Google Image API
 * 
 * @author Roberto Fonti
 */
public class HomeActivity extends Activity implements ProgressActivity {

	ImageGridFragment gridFragment;
	
	SearchView searchView;
	MenuItem menuProgress;
	
	String mLastQuery = null;
	
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setContentView(R.layout.activity_image_grid);
		setContentView(R.layout.activity_main_pager);

		// gridFragment = (ImageGridFragment) getFragmentManager().findFragmentById(R.id.fragment_grid);
		
		if (savedInstanceState!=null) {
			mLastQuery = savedInstanceState.getString("query");
		}
		
		setupTabs();
		
	}
	
	
	
	private void setupTabs() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(getFragmentManager());
		mViewPager.setAdapter(mTabsAdapter);
		setupActionBar();
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int page) {
				getActionBar().setSelectedNavigationItem(page);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }
			
			@Override
			public void onPageScrollStateChanged(int arg0) { }
			
		});
	}



	private void setupActionBar() {

		final ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				 mViewPager.setCurrentItem(tab.getPosition());
				
			}
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				 
			}
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

		};

		actionBar.addTab(
				actionBar.newTab().setText(R.string.tab_image_grid).setTabListener(tabListener)
				);

		actionBar.addTab(
				actionBar.newTab().setText(R.string.tab_map).setTabListener(tabListener)
				);
		actionBar.addTab(
				actionBar.newTab().setText(R.string.tab_favorites).setTag("fav").setTabListener(tabListener).setIcon(R.drawable.favorite_on)
				);
	}



	/**
	 * This is a singleTop activity, so running a new search
	 * just sends a new Intent
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		handleSearchIntent(intent);
	}
	
	protected void handleSearchIntent(Intent intent) {
		if (intent==null) return;
		
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mLastQuery = intent.getStringExtra(SearchManager.QUERY);
			
			Log.d(LOG_TAG, "intent: "+intent.getAction()+" q="+mLastQuery);
			
			// if picked from the history, makes sure it's set in the TextView
			searchView.setQuery(mLastQuery, false);

			// tell the fragment to start the search...
			gridFragment.startNewSearch(mLastQuery);
			
			mViewPager.setCurrentItem(0);
			
			// closes the search suggestions and clear focus from the TextView
			searchView.clearFocus();
			
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mLastQuery!=null) {
			outState.putString("query", mLastQuery);
		}
		if (gridFragment!=null) {
	        // Note, This is just persisting a reference to the current Fragment
			getFragmentManager().putFragment(
					outState, 
					"grid_fragment",
					gridFragment); 
		}
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (gridFragment == null) {
			gridFragment = (ImageGridFragment) getFragmentManager()
					.getFragment(savedInstanceState, "grid_fragment");
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.image_grid, menu);
		menuProgress = menu.findItem(R.id.menu_progress);
		menuProgress.getActionView().setVisibility(View.INVISIBLE);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchMenu = menu.findItem(R.id.menu_search);
		searchView = (SearchView) searchMenu.getActionView();
		searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()) );
		searchView.setQueryRefinementEnabled(true);
		
		// expand the SearchView and shows the keyboard
		// searchView.setIconified(false);
		
		if (mLastQuery!=null) {
			// from saved instance state, reset the query and force the keyboard to close
			searchView.setQuery(mLastQuery, false);
			searchView.clearFocus();
		} else {
			// workaround to open search suggestions before start typing
//			searchView.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						searchView.setQuery("", false);
//					}
//				}, 300);
		}
		return true;
	}
	
	@Override
	public void startProgress() {
		if (menuProgress!=null) {
			menuProgress.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			menuProgress.getActionView().setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void stopProgress() {
		if (menuProgress!=null) {
			menuProgress.getActionView().setVisibility(View.INVISIBLE);
		}
	}
	
	
	private class TabsAdapter extends FragmentPagerAdapter {
		
		public TabsAdapter(FragmentManager fm) {
			super(fm);
		}
		
	    @Override
	    public Fragment getItem(int page) {
	    	switch (page) {
			case 0:
				gridFragment = new ImageGridFragment();
				return gridFragment;
			case 1:
				MapFragment mapFragment = new MapFragment();
				return mapFragment;				
			case 2:
				FavoritesFragment favFragment = new FavoritesFragment();
				return favFragment;
				
			default:
				break;
			}
	    	return null; // should never go here
	    }

		@Override
		public int getCount() {
			return 3;
		}
		  @Override
		    public CharSequence getPageTitle(int position) {
		        return "PAGE " + (position + 1);
		    }

	}
	
}
