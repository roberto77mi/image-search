package rf.demo.uberimagesearch.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import rf.demo.uberimagesearch.api.WebImage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper { 
	final static String TAG = "UberDB";
	
	private static DatabaseHelper sInstance;

	private static final String DATABASE_NAME = "uberimages";
	private static final String DATABASE_TABLE = "images";
	private static final int DATABASE_VERSION = 1;

	private static final String FIELD_THUMBURL = "thumbUrl";
	private static final String FIELD_FULLURL = "fullUrl";
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_FAV_DATE = "favDate";
	private static final String FIELD_THUMB = "thumbUrl";
	
	private static final String FIELD_EXTRA = "extra";
	private static final String FIELD_IMAGE_JSON = "imageJson";
	private static final String FIELD_QUERY = "query";
	
	public static DatabaseHelper getInstance(Context context) {

		// Use the application context, which will ensure that you 
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new DatabaseHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+DATABASE_TABLE+
				"("+
				" _id INTEGER PRIMARY KEY,"+
				" "+FIELD_THUMBURL+" TEXT,"+
				" "+FIELD_FULLURL+" TEXT,"+
				" "+FIELD_TYPE+" INTEGER,"+ // 0=saved   1=favorite   2=??
				" orderNum INTEGER,"+
				" "+FIELD_FAV_DATE+" DATE,"+
				" lastClickDate DATE,"+
				" "+FIELD_QUERY+" TEXT,"+
				" title TEXT,"+
				" comment TEXT,"+
				" "+FIELD_IMAGE_JSON+" TEXT,"+
				" "+FIELD_EXTRA+" TEXT" +
				");"
				);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion==newVersion) return;
		
		Log.i(TAG, "Upgrading from version ["+oldVersion+"] to version ["+newVersion+"] ");
	}
	
	public long saveWebImage(WebImage webImage) {
		SQLiteDatabase db = this.getWritableDatabase();
		 ContentValues values = new ContentValues();
		 values.put(FIELD_THUMBURL, webImage.tbUrl);
		 
		 values.put(FIELD_QUERY, webImage.foundInQuery);
		 
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 
		 values.put(FIELD_FAV_DATE, format.format(webImage.favDate));
		 
		 return db.insert(DATABASE_TABLE, null, values);
		 
	}

	public int countAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(DATABASE_TABLE, new String[]{"COUNT(*)"}, null, null, null, null, null);
		c.moveToFirst();
		int r = c.getInt(0);
		c.close();
		return r;
	}
	
}
