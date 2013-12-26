package com.example.todolisthw;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public static final String DB_TABLE = "todo";
	public static final String DB_COLUMN_ID = "_id";
	public static final String DB_COLUMN_TODO = "event";

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQLï∂ÇÃãLèq
		db.execSQL("CREATE TABLE " + DB_TABLE + " ( " + DB_COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_COLUMN_TODO
				+ " TEXT" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
