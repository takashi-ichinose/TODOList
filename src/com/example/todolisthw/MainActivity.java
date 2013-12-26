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
	private String[] items = new String[] { "�ҏW", "�폜" };
	private static final int ITEM_EDIT = 0;
	private static final int ITEM_DELETE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.listView1);

		/*--------------DB����l���擾���鏈��--------------------------*/
		// DBHelper�N���X�̃C���X�^���X
		dbHelper = new DBHelper(this, "app.db", null, 1);
		// getReadableDatabase�Ńf�[�^�x�[�X�̓ǂݍ���
		db = dbHelper.getReadableDatabase();
		// query�𔭍s���ADB����Cursor��Ԃ��B
		cursor = db.query(DBHelper.DB_TABLE, columns, null, null, null, null,
				null);

		/*------------�J�[�\������������Adapter����������-----------------------------*/
		String[] from = new String[] { DBHelper.DB_COLUMN_TODO };
		int[] to = new int[] { android.R.id.text1 };
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, from, to);
		listView.setAdapter(adapter);

		/*------------ListView�̃A�C�e�����N���b�N�������̏���---------------------------*/
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// �J�[�\�����N���b�N�����A�C�e���̈ʒu�Ɉړ�����
				cursor.moveToPosition(position);
				// ���X�g�`���̃_�C�A���O�𐶐�
				AlertDialog.Builder bld = new AlertDialog.Builder(
						MainActivity.this).setTitle("TODO���j���[").setItems(items,
						new DialogInterface.OnClickListener() {
							int index = 0;

							@Override
							public void onClick(DialogInterface dialog, int item) {

								// �I�����ꂽ�A�C�e���ŏ����𕪊�
								switch (item) {

								/*------------------------�ҏW��I���������̏����B---------------------------------------*/
								case ITEM_EDIT:
									// EditText�𓮓I�ɐ������A�K�v�Ȏw�������B
									final EditText editData = new EditText(
											MainActivity.this);
									// InputType��TEXT�Ɏw�肷�邱�Ƃŉ��s�ł��Ȃ��悤�ɂ���
									editData.setInputType(InputType.TYPE_CLASS_TEXT);
									// �L�[�{�[�h��enter�L�[�̕\����ύX
									editData.setImeOptions(EditorInfo.IME_ACTION_DONE);
									// ���͊������̏���
									editData.setOnEditorActionListener(new OnEditorActionListener() {
										@Override
										public boolean onEditorAction(
												TextView v, int actionId,
												KeyEvent event) {
											hideInput(v);
											return false;
										}
									});
									// ���݂�TODO�̒l���擾��EditText�ɕ\������B
									index = cursor
											.getColumnIndex(DBHelper.DB_COLUMN_TODO);
									String todo = cursor.getString(index);
									editData.setText(todo);
									index = cursor
											.getColumnIndex(DBHelper.DB_COLUMN_ID);
									// �ύX�����Ƃ��Ďg�p����ID���擾�B
									final long editId = cursor.getLong(index);
									// EditText�t�̃_�C�A���O��\��
									new AlertDialog.Builder(MainActivity.this)
											.setTitle("�ҏW��")
											.setView(editData)

											// �X�V�{�^���N���b�N���̏���
											.setPositiveButton(
													"�X�V",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															// �X�V���ɕ����񂪋󂶂�Ȃ����
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
																//editText������̎��̏���
																editData.setOnFocusChangeListener(new OnFocusChangeListener() {
																	//editText����t�H�[�J�X���O�ꂽ��L�[�{�[�h���B��
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
											// �L�����Z���{�^���N���b�N���̏����B
											.setNegativeButton(
													"�L�����Z��",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
														}
													}).show();
									break;

								/*-------------------------------- �폜��I���������̏����B---------------------------------------*/
								case ITEM_DELETE:
									// �I�������A�C�e����ID���擾���AID��������DB�f�[�^���폜�B
									index = cursor
											.getColumnIndex(DBHelper.DB_COLUMN_ID);
									final long deleteId = cursor.getLong(index);
									new AlertDialog.Builder(MainActivity.this)
											.setTitle("�m�F")
											.setMessage("�I�������\����폜���܂����H")
											.setPositiveButton(
													"�폜����",
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
																	"�폜���܂���",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													})
											.setNegativeButton(
													"�L�����Z��",
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
	// ActionBar��add�{�^����ǉ�
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_menu, menu);
		View view = menu.findItem(R.id.menu_item).getActionView();
		editText = (EditText) view.findViewById(R.id.editText1);
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			// EditText���͊������ienter�j�̏���
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				events = editText.getText().toString();
				// editText�������񂪋�łȂ����
				if (events.isEmpty() == false) {
					db = dbHelper.getWritableDatabase();

					ContentValues values = new ContentValues();
					values.put(DBHelper.DB_COLUMN_TODO, events);
					db.insert(DBHelper.DB_TABLE, null, values);

					editListView();
					// editText����ɂ��ăL�[�{�[�h�\���������B
					editText.setText(null);
					hideInput(v);
				} else {
					Toast.makeText(MainActivity.this, "�\�肪���͂���Ă��܂���",
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
		return true;
	}

	// �J�[�\������蒼���ăA�_�v�^�[�ɐV��������R�Â��鏈��
	private void editListView() {
		cursor = db.query(DBHelper.DB_TABLE, columns, null, null, null, null,
				null);
		adapter.changeCursor(cursor);
		adapter.notifyDataSetChanged();
	}

	// �L�[�{�[�h�\�����B�����\�b�h
	private void hideInput(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
