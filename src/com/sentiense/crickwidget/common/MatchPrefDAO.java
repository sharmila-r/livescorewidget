/**
 * 
 */
package com.sentiense.crickwidget.common;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * The Class ScoreBD.
 * 
 * @author Sentiense Technologies
 * @version 1.0 Dec 6, 2011
 */
public class MatchPrefDAO extends SQLiteOpenHelper {

	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "cricket.db";

	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;

	/** The Constant TABLE_NAME. */
	public static final String TABLE_NAME = "view_pref";

	/** The Constant _ID. */
	public static final String _ID = BaseColumns._ID;

	/** The Constant USER_NAME. */
	public static final String REFRESH_RATE = "refresh_rate";

	/** The Constant USER_SCORE. */
	public static final String MATCH_NAMES = "match_names";
	
	public static final String WIDGET_ID ="widget_id";

	private static SQLiteDatabase db;
	
	/**
	 * Instantiates a new score bd.
	 * 
	 * @param context
	 *            the context
	 */
	public MatchPrefDAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Instantiates a new score bd.
	 * 
	 * @param context
	 *            the context
	 * @param name
	 *            the name
	 * @param factory
	 *            the factory
	 * @param version
	 *            the version
	 */
	public MatchPrefDAO(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	public void onCreate(SQLiteDatabase db) {
		// CREATE TABLE user_score (id INTEGER PRIMARY KEY AUTOINCREMENT,
		// user_name TEXT NOT NULL, user_Score INTEGER);

			String sql = "CREATE TABLE " + TABLE_NAME + " (" + MATCH_NAMES
					+ " TEXT NOT NULL, " + WIDGET_ID
					+ " INTEGER PRIMARY KEY,  " + REFRESH_RATE + " INTEGER" + ");";

			db.execSQL(sql);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);

	}

	/**
	 * Insert.
	 * 
	 * @param userName
	 *            the user name
	 * @param userScore
	 *            the user score
	 */
	public void insert(String matchNames, int widgetId,int refreshRate) {

			db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(MATCH_NAMES, matchNames);
			values.put(WIDGET_ID, widgetId);
			values.put(REFRESH_RATE, refreshRate);
			db.insertOrThrow(TABLE_NAME, null, values);
			db.close();

	}

	/**
	 * All.
	 * 
	 * @param activity
	 *            the activity
	 * @return the cursor
	 */
	public Cursor all(Activity activity) {
		String[] from = { _ID, WIDGET_ID,MATCH_NAMES, REFRESH_RATE };
		String order = REFRESH_RATE + " DESC";
		
		db = getReadableDatabase();
		Cursor cursor = null;

			cursor = db.query(TABLE_NAME, from, null, null, null, null, order,
					"10");
			activity.startManagingCursor(cursor);

			return cursor;


	}

	/**
	 * Count.
	 * 
	 * @return the long
	 */
	public long count() {
		long recordCount = 0;
		db = getReadableDatabase();
			recordCount = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
			return recordCount;
	}
	
	public static void closeDb(){
		if(db!=null){
			db.close();
		}
	}

	/**
	 * @param cricketScoreWidget
	 * @return
	 */
	public Cursor all(Context cricketScoreWidget) {
		String[] from = { _ID, WIDGET_ID,MATCH_NAMES, REFRESH_RATE };
		String order = REFRESH_RATE + " DESC";
		
		db = getReadableDatabase();
		Cursor cursor = null;

			cursor = db.query(TABLE_NAME, from, null, null, null, null, order,
					"10");
		

			return cursor;


	}
}
