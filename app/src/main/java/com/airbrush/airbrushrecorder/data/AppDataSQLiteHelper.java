package com.airbrush.airbrushrecorder.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDataSQLiteHelper extends SQLiteOpenHelper
{
	private static final String TAG = "AppDataSQLiteHelper";
	
	public static final String TABLE_APP_DATA = "appData";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_IP = "ipAddress";
	public static final String COLUMN_COOKIE = "cookie";
	public static final String COLUMN_USER_MAIL = "userName";
	public static final String COLUMN_PASSWORD = "password";
	
	private static final String DATABASE_NAME = "flights.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_APP_DATA + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_IP + " TEXT NOT NULL, "
			+ COLUMN_COOKIE + " TEXT NOT NULL, "
			+ COLUMN_USER_MAIL + " TEXT NOT NULL, "
			+ COLUMN_PASSWORD + " TEXT NOT NULL);";
	
	private static final String DATABASE_CREATE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_APP_DATA + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_IP + " TEXT NOT NULL, "
			+ COLUMN_COOKIE + " TEXT NOT NULL, "
			+ COLUMN_USER_MAIL + " TEXT NOT NULL, "
			+ COLUMN_PASSWORD + " TEXT NOT NULL);";
	
	public AppDataSQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE);
	}
	
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " 
				+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_DATA);
		onCreate(database);
	}
	
	public void createIfNotExists(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE_IF_NOT_EXISTS);
	}
	
	public void updateIp(SQLiteDatabase database, String ip)
	{
		String[] columns = {COLUMN_ID, COLUMN_IP, COLUMN_COOKIE, COLUMN_USER_MAIL, COLUMN_PASSWORD};
		Cursor cursor = database.query(TABLE_APP_DATA, columns, null, null, null, null, null);
		cursor.moveToFirst();
		
		String query = "";
		if(cursor.getCount() > 0)
		{
			 query = "UPDATE " + TABLE_APP_DATA + " SET " + COLUMN_IP + "='" + ip + "';";
		}
		else
		{
			query = "INSERT INTO " + TABLE_APP_DATA + "(" + COLUMN_IP + ", " + COLUMN_COOKIE + ", "
					+ COLUMN_USER_MAIL + ", " + COLUMN_PASSWORD + ") VALUES ('" + ip + "', '', '', '');";
		} 
		
		database.execSQL(query);
	}
	
	public void updateCookie(SQLiteDatabase database, String cookie)
	{
		String[] columns = {COLUMN_ID, COLUMN_IP, COLUMN_COOKIE, COLUMN_USER_MAIL, COLUMN_PASSWORD};
		Cursor cursor = database.query(TABLE_APP_DATA, columns, null, null, null, null, null);
		cursor.moveToFirst();
		
		String query = "";
		if(cursor.getCount() > 0)
		{
			 query = "UPDATE " + TABLE_APP_DATA + " SET " + COLUMN_COOKIE + "='" + cookie + "';";
		}
		else
		{
			query = "INSERT INTO " + TABLE_APP_DATA + "(" + COLUMN_IP + ", " + COLUMN_COOKIE + ", "
					+ COLUMN_USER_MAIL + ", " + COLUMN_PASSWORD + ") VALUES ('', '" + cookie + "', '', '');";
		} 
		
		database.execSQL(query);
	}
	
	public void updateUserMail(SQLiteDatabase database, String userMail)
	{
		String[] columns = {COLUMN_ID, COLUMN_IP, COLUMN_COOKIE, COLUMN_USER_MAIL, COLUMN_PASSWORD};
		Cursor cursor = database.query(TABLE_APP_DATA, columns, null, null, null, null, null);
		cursor.moveToFirst();
		
		String query = "";
		if(cursor.getCount() > 0)
		{
			 query = "UPDATE " + TABLE_APP_DATA + " SET " + COLUMN_USER_MAIL + "='" + userMail + "';";
		}
		else
		{
			query = "INSERT INTO " + TABLE_APP_DATA + "(" + COLUMN_IP + ", " + COLUMN_COOKIE + ", "
					+ COLUMN_USER_MAIL + ", " + COLUMN_PASSWORD + ") VALUES ('', '', '" + userMail + "', '');";
		}
		
		database.execSQL(query);
	}
	
	public void updatePassword(SQLiteDatabase database, String password)
	{
		String[] columns = {COLUMN_ID, COLUMN_IP, COLUMN_COOKIE, COLUMN_USER_MAIL, COLUMN_PASSWORD};
		Cursor cursor = database.query(TABLE_APP_DATA, columns, null, null, null, null, null);
		cursor.moveToFirst();
		
		String query = "";
		if(cursor.getCount() > 0)
		{
			 query = "UPDATE " + TABLE_APP_DATA + " SET " + COLUMN_PASSWORD + "='" + password + "';";
		}
		else
		{
			query = "INSERT INTO " + TABLE_APP_DATA + "(" + COLUMN_IP + ", " + COLUMN_COOKIE + ", "
					+ COLUMN_USER_MAIL + ", " + COLUMN_PASSWORD + ") VALUES ('', '', '', '" + password + "');";
		}
		
		database.execSQL(query);
	}
}

