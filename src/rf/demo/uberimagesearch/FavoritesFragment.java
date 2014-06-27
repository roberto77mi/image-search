package rf.demo.uberimagesearch;

import com.squareup.picasso.Picasso;

import rf.demo.uberimagesearch.api.WebImage;
import rf.demo.uberimagesearch.db.DatabaseHelper;
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
import android.widget.ListView;
import android.widget.TextView;
import static rf.demo.uberimagesearch.db.DatabaseHelper.*;

public class FavoritesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> { 
	private Cursor mCursor;
	
	FavoritesAdapter mFavoritesAdapter;
	View.OnClickListener mFavButtonListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_favorites,
				container, false);
		
		mFavButtonListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
					int pos = getListView().getPositionForView(v);
					if (android.widget.AdapterView.INVALID_POSITION!=pos) {
						Cursor c = mFavoritesAdapter.getCursor();
						c.moveToPosition(pos);
						int id = c.getInt(0);
						
						DatabaseHelper.getInstance(getActivity()).delete(id);
						notifyChange();
					}
				
			}
		};
		
		return rootView;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("UBER","View="+v+" "+v.getTag()+" id="+id);
		// handle clicks on the row, currently not enabled
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
			 View v = LayoutInflater.from(context).inflate(
	                    R.layout.row_favorites, parent, false);
			 
			 View btn_fav = v.findViewById(R.id.img_fav);
			 btn_fav.setTag("fav");
			 btn_fav.setOnClickListener(mFavButtonListener);
			 return v;
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
		
//		@Override
//		public boolean isEnabled(int position) {
//			return false;
//		}
//		@Override
//		public boolean areAllItemsEnabled() {
//			return false;
//		}
	}
	
}
