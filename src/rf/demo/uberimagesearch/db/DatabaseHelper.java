package rf.demo.uberimagesearch.db;

import java.text.SimpleDateFormat;

import rf.demo.uberimagesearch.api.WebImage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper  { 
	final static String TAG = "UberDB";
	
	private static DatabaseHelper sInstance;

	static final String DATABASE_NAME = "uberimages";
	static final String DATABASE_TABLE = "images";
	static final int DATABASE_VERSION = 1;

	public static final String FIELD_ID = "_id";
	public static final String FIELD_THUMBURL = "thumbUrl";
	public static final String FIELD_FULLURL = "fullUrl";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_FAV_DATE = "favDate";
	
	public static final String FIELD_EXTRA = "extra";
	public static final String FIELD_IMAGE_JSON = "imageJson";
	public static final String FIELD_QUERY = "query";
	
	public static final String ORDER_FAV_DATE = FIELD_FAV_DATE + " DESC";
	
	
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
		// it will be called the first time getWritableDb is called.
		// it may not be in a background task!
		
		db.execSQL("CREATE TABLE "+DATABASE_TABLE+
				"("+
				" "+FIELD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
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
		 values.put(FIELD_FULLURL, webImage.url);
		 
		 values.put(FIELD_QUERY, webImage.foundInQuery);
		 // values.put(FIELD_, webImage.contentNoFormatting);
		 
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
	public void dumpAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(DATABASE_TABLE, new String[]{FIELD_ID,FIELD_THUMBURL,FIELD_QUERY,FIELD_FAV_DATE}, null, null, null, null, null);
		c.moveToFirst();
		do {
			
			Log.d(TAG, "row: id="+c.getInt(0)+" q="+c.getString(2)+" date="+c.getString(3)+" thumb="+c.getString(1));
			
		} while (c.moveToNext());
		
		c.close();
	}

	public void delete(long id) {
		if (id==0) return; // 
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DATABASE_TABLE, FIELD_ID+" = "+id, null);
	}
}
