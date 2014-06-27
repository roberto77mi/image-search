package rf.demo.uberimagesearch;

import com.squareup.picasso.Picasso;

import rf.demo.uberimagesearch.db.UberImagesProvider;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import static rf.demo.uberimagesearch.db.DatabaseHelper.*;

public class FavoritesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> { 
	private Cursor mCursor;
	
	FavoritesAdapter mFavoritesAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_favorites,
				container, false);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mFavoritesAdapter = new FavoritesAdapter(getActivity());
		getListView().setAdapter(mFavoritesAdapter);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	   return new CursorLoader(getActivity(), 
    			   UberImagesProvider.CONTENT_URI,
                   new String[]{FIELD_ID, FIELD_THUMBURL, FIELD_FULLURL, FIELD_FAV_DATE, FIELD_QUERY}, null, null,
                   ORDER_FAV_DATE);
    }
    
	@Override
	public void onLoaderReset(Loader<Cursor> loader) { 
		mFavoritesAdapter.swapCursor(null);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		Log.d(Constant.LOG_TAG, "Loader: "+cursor.getCount());
		
		mCursor = cursor;
		mFavoritesAdapter.changeCursor(mCursor);
		mFavoritesAdapter.notifyDataSetChanged();
		
		// TODO maybe update the position of the listView?
		//if (isAdded()) getListView().setSelectionFromTop
	}
	
	public void notifyChange(){
		
		getActivity().getContentResolver().notifyChange(UberImagesProvider.CONTENT_URI, null);

	}
	
	private class FavoritesAdapter extends CursorAdapter {
		public FavoritesAdapter(Context context) {
			super(context, mCursor, true);
		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			 return LayoutInflater.from(context).inflate(
	                    R.layout.row_favorites, parent, false);
		}
		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			final String id = cursor.getString(0);
			final String url = cursor.getString(2);
			
			// TODO viewHolder
			ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
			TextView caption = (TextView) row.findViewById(R.id.row_caption);
			
			Picasso.with(context).load(url).placeholder(R.drawable.load_placeholder).resize(480, 320).into(imageView);
			
			caption.setText(cursor.getString(4));
		}

	}
	
}
