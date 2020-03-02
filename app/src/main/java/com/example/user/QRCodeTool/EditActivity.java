package com.example.user.QRCodeTool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cipherlab.barcode.GeneralString;
import com.cipherlab.barcode.ReaderManager;


import static com.example.user.QRCodeTool.FeedReaderContract.FeedEntry.TABLE_NAME;


public class EditActivity extends Activity {

    private  FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
    int rows_num;
    EditText edittext;

    //  CiperLab API
    private ReaderManager mReaderManager;
    private IntentFilter filter;
    ReaderManager m_RM = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("物料盤點");// 設定 Title Text
        edittext = (EditText) findViewById(R.id.editText);



       //  CiperLab API
        m_RM = ReaderManager.InitInstance(this);
        filter = new IntentFilter();


//        ReaderOutputConfiguration settings = new ReaderOutputConfiguration();
//        settings.enableKeyboardEmulation = KeyboardEmulationType.InputMethod;
//        settings.autoEnterWay = OutputEnterWay.SuffixData;
//        settings.autoEnterChar = OutputEnterChar.Return;
//        settings.showCodeLen = Enable_State.TRUE;
//        settings.showCodeType = Enable_State.TRUE;
//        settings.szPrefixCode = "PreStr";
//        settings.szSuffixCode = "SufStr";
//        settings.useDelim = ':';
//        settings.szCharsetName = "shift_JIS";
//        settings.clearPreviousData = Enable_State.TRUE;
//        m_RM.Set_ReaderOutputConfiguration(settings);



       // filter.addAction(GeneralString.Intent_SOFTTRIGGER_DATA);

        filter.addAction(GeneralString.Intent_PASS_TO_APP );

        registerReceiver(myDataReceiver, filter);

        // m_RM.SoftScanTrigger();// Scan








        // Gets the data repository in write mode
        //SQLiteDatabase db = mDbHelper.getWritableDatabase();


        // Create a new map of values, where column names are the keys
//        ContentValues values = new ContentValues();
//        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, "001-1");
//        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "001-2");
//        // Insert the new row, returning the primary key value of the new row
//        long newRowId =  db.insert(TABLE_NAME, null, values);

        
        //SQLiteDatabase  mDbHelperReadableDatabase = mDbHelper.getReadableDatabase();

         // Define a projection that specifies which columns from the database
          // you will actually use after this query.
//        String[] projection = {
//                BaseColumns._ID,
//                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
//                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE
//        };

         // Filter results WHERE "title" = 'My Title'
//        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " = ?";
//        String[] selectionArgs = { "My Title" };
//
//          // How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";
//
//        Cursor cursor = db.query(
//                TABLE_NAME,   // The table to query
//                projection,             // The array of columns to return (pass null to get all)
//                selection,              // The columns for the WHERE clause
//                selectionArgs,          // The values for the WHERE clause
//                null,                   // don't group the rows
//                null,                   // don't filter by row groups
//                sortOrder               // The sort order
//        );

      //  mDbHelperReadableDatabase.getPageSize();


       // db.execSQL("insert into entry(title, subtitle) values('測試資料2', 2)");
        //db.execSQL("insert into entry(title, subtitle) values('測試資料3', 3)");
//      db.close();

//        Cursor cursor2 = db.query(
//                TABLE_NAME,   // The table to query
//                null,             // The array of columns to return (pass null to get all)
//                null,              // The columns for the WHERE clause
//                null,          // The values for the WHERE clause
//                null,                   // don't group the rows
//                null,                   // don't filter by row groups
//                null               // The sort order
//        );

        //rows_num = cursor2.getCount();	//取得資料表列數
       // cursor2.moveToFirst();			//將指標移至第一筆資
//        cursor2.getString(cursor.getColumnIndex("title"));
//        Toast.makeText(this,  cursor2.getString(cursor.getColumnIndex("title")), Toast.LENGTH_LONG).show();
//        Toast.makeText(this,  cursor2.getString(cursor.getColumnIndex("subtitle")), Toast.LENGTH_LONG).show();
//
//        cursor2.moveToNext();			//將指標移至第2筆資
//        Toast.makeText(this,  cursor2.getString(cursor.getColumnIndex("title")), Toast.LENGTH_LONG).show();
//        Toast.makeText(this,  cursor2.getString(cursor.getColumnIndex("subtitle")), Toast.LENGTH_LONG).show();
        //cursor2.close();
//        Cursor cursor2 = db.query(TABLE_NAME, new String[] { "title", "subtitle"}, "title like " + "'%0%'", null, null, null, null);
//        Toast.makeText(this, "", Toast.LENGTH_LONG).show();

        AppCompatButton  btnenter = (AppCompatButton) findViewById(R.id.btnenter);
        btnenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               // m_RM.SoftScanTrigger();

//                boolean bRet=  m_RM.GetActive();
//                if (bRet==false){
//                    ClResult clRet =  m_RM.SetActive(true);
//                }
//
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, edittext.getText().toString());
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "0");
                // Insert the new row, returning the primary key value of the new row
                db.insert(TABLE_NAME, null, values);
                db.close();

                edittext.setText("");

                Intent intent = new Intent(getApplication(), ListActivity.class);
                startActivity(intent);

//                 SQLiteDatabase db = mDbHelper.getWritableDatabase();
//                 db.execSQL("DROP TABLE IF EXISTS  entry");
//                 db.execSQL("delete from "+ TABLE_NAME);

            }
        });

        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                m_RM.SoftScanTrigger();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_up) {



        }
        if (id == R.id.action_down) {


            m_RM.SoftScanTrigger();


        }
        return super.onOptionsItemSelected(item);
    }





    //  CiperLab API
    private final BroadcastReceiver myDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)){// Fetch data from the intent


                String sDataStr = intent.getStringExtra(GeneralString.BcReaderData);
                Toast.makeText(EditActivity.this, "Decoded data is " + sDataStr , Toast.LENGTH_SHORT).show();


                edittext.setText(sDataStr);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, sDataStr );
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "0");
                // Insert the new row, returning the primary key value of the new row
                db.insert(TABLE_NAME, null, values);
                db.close();

                Intent intent2 = new Intent(getApplication(), ListActivity.class);
                startActivity(intent2);


            }
        }
    };






}