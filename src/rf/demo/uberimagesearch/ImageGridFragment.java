package rf.demo.uberimagesearch;

import static rf.demo.uberimagesearch.Constant.LOG_TAG;

import java.util.ArrayList;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rf.demo.uberimagesearch.api.GoogleImageApi;
import rf.demo.uberimagesearch.api.GoogleImageApi.GoogleImageApiResponse;
import rf.demo.uberimagesearch.api.WebImage;
import rf.demo.uberimagesearch.db.DatabaseHelper;
import rf.demo.uberimagesearch.db.UberImagesProvider;
import rf.demo.uberimagesearch.history.SearchHistoryProvider;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class ImageGridFragment extends Fragment implements OnScrollListener, OnItemClickListener , OnItemLongClickListener {
	ProgressActivity activity;
	
	GridView mGridView;
	ImagesAdapter mAdapter;
	
	TextView mMessageError;
	TextView mMessage;
	
	RestAdapter mImageIpiAdapter;
	GoogleImageApi mImageApi;
	SearchRecentSuggestions mSuggestions;
	
	ArrayList<WebImage> mResultList;
	
	String mQuery; // the keyword from the text view 
	int mPage = 1; // current page
	
	// the api call is in progress
	boolean mLoading = false;
	
	// does not try to load again until refresh is clicked
	boolean mError = false; 
	
	// set to false when the end of the available results is reached
	boolean mMoreResultsAvailable = true;
	
	final static int PAGE_SIZE = 8; // 8 is max allowed by the api
	final static String KEY_LIST = "list";
	final static String KEY_PAGE = "page";
	final static String KEY_ERROR = "error";
	final static String KEY_MORE = "more";
	final static String KEY_QUERY = "query";
	
	public ImageGridFragment() {}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (ProgressActivity) activity;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mImageIpiAdapter = new RestAdapter.Builder()
			.setEndpoint("https://ajax.googleapis.com")
			.build();

		mSuggestions = new SearchRecentSuggestions(getActivity(),
	                SearchHistoryProvider.AUTHORITY, SearchHistoryProvider.MODE);
		
		mImageApi = mImageIpiAdapter.create(GoogleImageApi.class);		
	
		if (savedInstanceState!=null){
			mPage = savedInstanceState.getInt(KEY_PAGE);
			mQuery = savedInstanceState.getString(KEY_QUERY);
			mError = savedInstanceState.getBoolean(KEY_ERROR);
			mMoreResultsAvailable = savedInstanceState.getBoolean(KEY_MORE);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_image_grid,
				container, false);
		
		mGridView = (GridView) rootView.findViewById(R.id.image_grid_grid);
		
		mMessage = (TextView) rootView.findViewById(R.id.image_grid_message);
		
		mMessageError = (TextView) rootView.findViewById(R.id.image_grid_message_error);
		
		// click to retry
		mMessageError.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadImagesAsynch();
			}
		});
		
		if (savedInstanceState!=null){
			this.mResultList = (ArrayList<WebImage>) savedInstanceState.getSerializable("list");
		}

		resetAdapter();
		
		mGridView.setOnScrollListener( this );
		mGridView.setOnItemClickListener( this );
		mGridView.setOnItemLongClickListener( this );
		
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mAdapter!=null) {
			ArrayList<WebImage> list = new ArrayList<WebImage>(mAdapter.getCount());
			for (int i=0; i<mAdapter.getCount(); i++) {
				list.add(mAdapter.getItem(i));
			}
			outState.putSerializable(KEY_LIST, list);
			outState.putInt(KEY_PAGE, mPage);
			outState.putString(KEY_QUERY, mQuery);
			outState.putBoolean(KEY_ERROR, mError);
			outState.putBoolean(KEY_MORE, mMoreResultsAvailable);
		}
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mError || !mMoreResultsAvailable) return;
		
		int lastVisibleItem = firstVisibleItem + visibleItemCount;
		
		if ((lastVisibleItem == totalItemCount) && !(mLoading)){
			loadImagesAsynch();
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		WebImage webImage = mAdapter.getItem(position);
		
		if (webImage!=null) {
			if (webImage.fav) {
				webImage.fav = false;
				DatabaseHelper.getInstance(getActivity()).delete(webImage.id);
				
			} else {
				webImage.fav = true;
				webImage.favDate = new Date();
				webImage.foundInQuery = mQuery;
				
				// 
				webImage.id = DatabaseHelper.getInstance(getActivity()).saveWebImage(webImage);		
			}
			getActivity().getContentResolver().notifyChange(UberImagesProvider.CONTENT_URI, null);
			
			mAdapter.notifyDataSetChanged(); // show / hide the heart
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		WebImage webImage = mAdapter.getItem(position);
		
		// Open Image in Browser
		getActivity().startActivity(new Intent(
				Intent.ACTION_VIEW, 
				Uri.parse(webImage.url)));
		return true;

	}
	
	private void resetAdapter(){
		if (mResultList==null){
			mResultList = new ArrayList<WebImage>();
		}
		
		mAdapter = new ImagesAdapter(getActivity(), mResultList);
		mGridView.setAdapter(mAdapter);
	}
	
	public void startNewSearch(String query) {
		mResultList.clear();
		
		resetAdapter();
		hideEmptyMessage();
		
		this.mMoreResultsAvailable = true;
		this.mQuery = query;
		this.mPage = 1;
		
	    mSuggestions.saveRecentQuery(query, null);
		
		loadImagesAsynch();
	}
	
	private void showErrorMessage() {
		mMessageError.setVisibility(View.VISIBLE);
	}
	private void hideErrorMessage() {
		mMessageError.setVisibility(View.GONE);
	}	
	private void showEmptyMessage() {
		mMessage.setVisibility(View.VISIBLE);
	}
	private void hideEmptyMessage() {
		mMessage.setVisibility(View.GONE);
	}	
	
	private void loadImagesAsynch() {
		if (mQuery==null) return;
		
		mLoading = true;
		mError = false;
		hideErrorMessage();
		
		activity.startProgress();
		
		Log.d(LOG_TAG, "Loading '"+mQuery+"' page="+mPage);
		
		mImageApi.listImages(mQuery, PAGE_SIZE, (mPage-1)*PAGE_SIZE, new Callback<GoogleImageApi.GoogleImageApiResponse>() {
			
			@Override
			public void success(GoogleImageApiResponse apiResponse, Response retrofitResponse) {
				if (!isAdded()) return;
				
				if (apiResponse.getImages()!=null) {
					if (mPage == 1 && apiResponse.getImages().isEmpty()) {
						// zero results
						showEmptyMessage();
					} else {
						mAdapter.addAll(apiResponse.getImages());
						mPage++;
					}
				}
				
				mMoreResultsAvailable = apiResponse.isMoreDataAvailable();
				
				mLoading = false;
				activity.stopProgress();
			}
			
			@Override
			public void failure(RetrofitError error) {
				Log.e(LOG_TAG, "Networking error: "+error.getMessage());
				if (!isAdded()) return;
				
				mError = true;
				
				showErrorMessage();
				
				mLoading = false;
				activity.stopProgress();
			}
		});

	}
	
}