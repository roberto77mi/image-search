package rf.demo.uberimagesearch;

import static rf.demo.uberimagesearch.Constant.LOG_TAG;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
public class ImageGridActivity extends Activity implements ProgressActivity {

	ImageGridFragment gridFragment;
	SearchView searchView;
	MenuItem menuProgress;
	
	String mLastQuery = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_image_grid);

		gridFragment = (ImageGridFragment) getFragmentManager().findFragmentById(R.id.fragment_grid);
		
		if (savedInstanceState!=null) {
			mLastQuery = savedInstanceState.getString("query");
		}
		
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
		searchView.setIconified(false);
		
		if (mLastQuery!=null) {
			// from saved instance state, reset the query and force the keyboard to close
			searchView.setQuery(mLastQuery, false);
			searchView.clearFocus();
		} else {
			// workaround to open search suggestions before start typing
			searchView.postDelayed(new Runnable() {
					@Override
					public void run() {
						searchView.setQuery("", false);
					}
				}, 300);
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
	
}
