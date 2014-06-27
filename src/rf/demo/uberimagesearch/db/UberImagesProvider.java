package rf.demo.uberimagesearch.db;

import static rf.demo.uberimagesearch.db.DatabaseHelper.*;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Provides access to the WebImages DB (for favorites images and more)
 * 
 * Uri:
 * content://rf.demo.uberimagesearch.db.
 * 
 */
public class UberImagesProvider extends ContentProvider {
	//private static final String AUTHORITY = UberImagesProvider.class.getCanonicalName();
	  
	public static final Uri CONTENT_URI = Uri.parse("content://rf.demo.uberimagesearch.db.webimages/webimages");
	  
	DatabaseHelper mDbHelper;
	
	private static final String BASE_PATH = "webimages";

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/webimages";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/webimage";


	/* Uri Matching , return values on match */
	final static int WEBIMAGE  = 1;
	final static int WEBIMAGE_ID  = 2;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(CONTENT_URI.getAuthority(), BASE_PATH, WEBIMAGE);
		sURIMatcher.addURI(CONTENT_URI.getAuthority(), BASE_PATH + "/#", WEBIMAGE_ID);
	}


	@Override
	public boolean onCreate() {
		mDbHelper = DatabaseHelper.getInstance(getContext());
		return false;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mDbHelper.getReadableDatabase();
		// get from uri what you want to load
	    int result = sURIMatcher.match(uri);
	    
	    switch (result) {
		case WEBIMAGE:
			Cursor c = db.query(DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);		
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		case WEBIMAGE_ID:
			throw new RuntimeException("not implemented yet");
		default:
			break;
		}
		return null;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
