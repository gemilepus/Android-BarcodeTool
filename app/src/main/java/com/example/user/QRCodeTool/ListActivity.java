package com.example.user.QRCodeTool;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cipherlab.barcode.GeneralString;
import com.cipherlab.barcode.ReaderManager;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.QRCodeTool.FeedReaderContract.FeedEntry.TABLE_NAME;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    int rows_num;

    private BaseAdapter adapter;
    ListView listView;
    private List<String> itemNames ,  itemTitle , itemID , itemTag;
    //private static final String[] scenes = {"01","02","03","04","05","06","07"};

    private  FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);


    //  CiperLab API
    private ReaderManager mReaderManager;
    private IntentFilter filter;
    ReaderManager m_RM = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        setTitle("物料盤點");// 設定 Title Text

        //  CiperLab API
        m_RM = ReaderManager.InitInstance(this);
        filter = new IntentFilter();
        filter.addAction(GeneralString.Intent_SOFTTRIGGER_DATA);
        registerReceiver(myDataReceiver, filter);


//        Dialog alertDialog = new Dialog(this);
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alertDialog.setContentView(R.layout.tabs);
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.show();


        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor= db.query(
                TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        // cursor.moveToFirst();//將指標移至第一筆資

        rows_num = cursor.getCount();	//取得資料表列數

        itemID = new ArrayList<>();
        itemTitle = new ArrayList<>();
        itemTag = new ArrayList<>();
        while (cursor.moveToNext()) {
            itemTitle.add( cursor.getString(cursor.getColumnIndex("title")) );
            itemID.add( cursor.getString(cursor.getColumnIndex("_id")) );
            itemTag.add( cursor.getString(cursor.getColumnIndex("subtitle")) );
        }
        db.close();
        cursor.close();

        //itemNames = new ArrayList<>(Arrays.asList(scenes));
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ListViewAdapter(this.getApplicationContext(), R.layout.list,  itemTitle , itemID ,itemTag);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

    }





    //  CiperLab API
    private final BroadcastReceiver myDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)){// Fetch data from the intent
                String sDataStr = intent.getStringExtra(GeneralString.BcReaderData);
                Toast.makeText(ListActivity.this, "Decoded data is " + sDataStr , Toast.LENGTH_SHORT).show();


                for(int i =0 ; i < rows_num; i++){
                    String q = itemTitle.get(i) ;
                    if(itemTitle.get(i) ==  sDataStr){

                        update( i , itemTitle.get(i) , "1" );


                    }
                }


            }
        }
    };



    public void update(int id, String title, String subtitle) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        ContentValues row = new ContentValues();
        row.put("title", title);
        row.put("subtitle", subtitle);
        db.update("entry", row, "_id = ?", new String[] { String.valueOf(id) } );


        Cursor cursor= db.query(
                TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        rows_num = cursor.getCount();	//取得資料表列數

        itemID = new ArrayList<>();
        itemTitle = new ArrayList<>();
        itemTag = new ArrayList<>();
        while (cursor.moveToNext()) {
            itemTitle.add( cursor.getString(cursor.getColumnIndex("title")) );
            itemID.add( cursor.getString(cursor.getColumnIndex("_id")) );
            itemTag.add( cursor.getString(cursor.getColumnIndex("subtitle")) );
        }
        db.close();
        cursor.close();

        adapter = new ListViewAdapter(this.getApplicationContext(), R.layout.list,  itemTitle , itemID , itemTag);
        listView.setAdapter(adapter);


    }



    public void delete(String id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete(TABLE_NAME, "_id = ?", new String[] { String.valueOf(id) });

        Cursor cursor= db.query(
                TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        rows_num = cursor.getCount();	//取得資料表列數

        itemID = new ArrayList<>();
        itemTitle = new ArrayList<>();
        itemTag = new ArrayList<>();
        while (cursor.moveToNext()) {
            itemTitle.add( cursor.getString(cursor.getColumnIndex("title")) );
            itemID.add( cursor.getString(cursor.getColumnIndex("_id")) );
            itemTag.add( cursor.getString(cursor.getColumnIndex("subtitle")) );
        }
        db.close();
        cursor.close();

        adapter = new ListViewAdapter(this.getApplicationContext(), R.layout.list,  itemTitle , itemID , itemTag);
        listView.setAdapter(adapter);

    }



    String  a ;
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String item =  itemTitle.get(position);//position
        String db_ID =  itemID.get(position);//position
        alertCheck(item ,  db_ID);

    }

    private void alertCheck(String item ,String item_position) {
        a =item_position ;

        String[] alert_menu = {"Test", "刪除",};
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(item);
        alert.setItems(alert_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int idx) {
                if (idx == 0) {

                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    db.execSQL("delete from "+ TABLE_NAME);

                    Intent intent = new Intent(getApplication(), EditActivity.class);
                    startActivity(intent);

                }
                else {

                    delete( a );
                }
            }
        });
        alert.show();
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

            m_RM.SoftScanTrigger();// Scan

        }
        if (id == R.id.action_down) {



            String sDataStr = "22";
            Toast.makeText(ListActivity.this, "Decoded data is " + sDataStr , Toast.LENGTH_SHORT).show();

            for(int i =0 ; i < rows_num; i++){
                String q = itemTitle.get(i) ;
                if(itemTitle.get(i) !=  sDataStr){

                    update( i+1 , itemTitle.get(i) , "0" );


                }
            }

            int a = 0;
            for (String s : itemTitle)
            {
                if( s ==  sDataStr){

                    update( a , s , "1" );


                }
                a++;
            }

           // update( 3 , "11651" , "1" );

        }
        return super.onOptionsItemSelected(item);
    }

}
