package com.example.todolisthw;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ListView listView;
	private DBHelper dbHelper;
	private SimpleCursorAdapter adapter;
	private SQLiteDatabase db;
	private Cursor cursor;
	private EditText editText;
	private String events;
	private String[] columns = new String[] { DBHelper.DB_COLUMN_ID,
			DBHelper.DB_COLUMN_TODO };
	private String[] items = new String[] { "編集", "削除" };
	private static final int ITEM_EDIT = 0;
	private static final int ITEM_DELETE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.listView1);

		/*--------------DBから値を取得する処理--------------------------*/
		// DBHelperクラスのインスタンス
		dbHelper = new DBHelper(this, "app.db", null, 1);
		// getReadableDatabaseでデータベースの読み込み
		db = dbHelper.getReadableDatabase();
		// queryを発行し、DBからCursorを返す。
		cursor = db.query(DBHelper.DB_TABLE, columns, null, null, null, null,
				null);

		/*------------カーソルを処理するAdapterを実装する-----------------------------*/
		String[] from = new String[] { DBHelper.DB_COLUMN_TODO };
		int[] to = new int[] { android.R.id.text1 };
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, from, to);
		listView.setAdapter(adapter);

		/*------------ListViewのアイテムをクリックした時の処理---------------------------*/
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// カーソルをクリックしたアイテムの位置に移動する
				cursor.moveToPosition(position);
				// リスト形式のダイアログを生成
				AlertDialog.Builder bld = new AlertDialog.Builder(
						MainActivity.this).setTitle("TODOメニュー").setItems(items,
						new DialogInterface.OnClickListener() {
							int index = 0;

							@Override
							public void onClick(DialogInterface dialog, int item) {

								// 選択されたアイテムで処理を分岐
								switch (item) {

								/*------------------------編集を選択した時の処理。---------------------------------------*/
								case ITEM_EDIT:
									// EditTextを動的に生成し、必要な指定をする。
									final EditText editData = new EditText(
											MainActivity.this);
									// InputTypeをTEXTに指定することで改行できないようにする
									editData.setInputType(InputType.TYPE_CLASS_TEXT);
									// キーボードのenterキーの表示を変更
									editData.setImeOptions(EditorInfo.IME_ACTION_DONE);
									// 入力完了時の処理
									editData.setOnEditorActionListener(new OnEditorActionListener() {
										@Override
										public boolean onEditorAction(
												TextView v, int actionId,
												KeyEvent event) {
											hideInput(v);
											return false;
										}
									});
									// 現在のTODOの値を取得しEditTextに表示する。
									index = cursor
											.getColumnIndex(DBHelper.DB_COLUMN_TODO);
									String todo = cursor.getString(index);
									editData.setText(todo);
									index = cursor
											.getColumnIndex(DBHelper.DB_COLUMN_ID);
									// 変更条件として使用するIDを取得。
									final long editId = cursor.getLong(index);
									// EditText付のダイアログを表示
									new AlertDialog.Builder(MainActivity.this)
											.setTitle("編集中")
											.setView(editData)

											// 更新ボタンクリック時の処理
											.setPositiveButton(
													"更新",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															// 更新時に文字列が空じゃなければ
															if (editData
																	.getText()
																	.toString()
																	.isEmpty() == false) {
																ContentValues values = new ContentValues();
																values.put(
																		DBHelper.DB_COLUMN_TODO,
																		editData.getText()
																				.toString());
																db = dbHelper
																		.getWritableDatabase();
																db.update(
																		DBHelper.DB_TABLE,
																		values,
																		DBHelper.DB_COLUMN_ID
																				+ " = '"
																				+ editId
																				+ "'",
																		null);
																editListView();
															} else {
																//editText内が空の時の処理
																editData.setOnFocusChangeListener(new OnFocusChangeListener() {
																	//editTextからフォーカスが外れたらキーボードを隠す
																	@Override
																	public void onFocusChange(
																			View v,
																			boolean hasFocus) {
																		if (hasFocus == false) {
																			hideInput(v);
																		}
																	}
																});
															}
														}
													})
											// キャンセルボタンクリック時の処理。
											.setNegativeButton(
													"キャンセル",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
														}
													}).show();
									break;

								/*-------------------------------- 削除を選択した時の処理。---------------------------------------*/
								case ITEM_DELETE:
									// 選択したアイテムのIDを取得し、IDを条件にDBデータを削除。
									index = cursor
											.getColumnIndex(DBHelper.DB_COLUMN_ID);
									final long deleteId = cursor.getLong(index);
									new AlertDialog.Builder(MainActivity.this)
											.setTitle("確認")
											.setMessage("選択した予定を削除しますか？")
											.setPositiveButton(
													"削除する",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															db = dbHelper
																	.getWritableDatabase();
															db.delete(
																	DBHelper.DB_TABLE,
																	DBHelper.DB_COLUMN_ID
																			+ " = '"
																			+ deleteId
																			+ "'",
																	null);
															editListView();
															Toast.makeText(
																	MainActivity.this,
																	"削除しました",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													})
											.setNegativeButton(
													"キャンセル",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
														}
													}).show();
									break;
								}
							}
						});
				bld.show();
			}
		});
	}

	@Override
	// ActionBarにaddボタンを追加
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_menu, menu);
		View view = menu.findItem(R.id.menu_item).getActionView();
		editText = (EditText) view.findViewById(R.id.editText1);
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			// EditText入力完了時（enter）の処理
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				events = editText.getText().toString();
				// editText内文字列が空でなければ
				if (events.isEmpty() == false) {
					db = dbHelper.getWritableDatabase();

					ContentValues values = new ContentValues();
					values.put(DBHelper.DB_COLUMN_TODO, events);
					db.insert(DBHelper.DB_TABLE, null, values);

					editListView();
					// editTextを空にしてキーボード表示を消す。
					editText.setText(null);
					hideInput(v);
				} else {
					Toast.makeText(MainActivity.this, "予定が入力されていません",
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
		return true;
	}

	// カーソルを取り直してアダプターに新しい情報を紐づける処理
	private void editListView() {
		cursor = db.query(DBHelper.DB_TABLE, columns, null, null, null, null,
				null);
		adapter.changeCursor(cursor);
		adapter.notifyDataSetChanged();
	}

	// キーボード表示を隠すメソッド
	private void hideInput(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
