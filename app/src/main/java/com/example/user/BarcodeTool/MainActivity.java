package com.example.user.BarcodeTool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.BarcodeTool.View.MyAdapter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private AppCompatButton btn_save,btn_saveCode;
    private String BarcodeValue,BarcodeType;
    private EditText mEditText;
    private TextView mTextView;
    private Spinner spinner;
    private Bitmap bit;

    private boolean mBright = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        VariableEditor.ScanText = "";

        mTextView = (TextView)findViewById(R.id.textView);

        mEditText = (EditText)findViewById(R.id.editTextBarcode);
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (mEditText.getRight() - mEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Gets a handle to the clipboard service.
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // Creates a new text clip to put on the clipboard
                        ClipData clip = ClipData.newPlainText("text", mEditText.getText());
                        // Set the clipboard's primary clip.
                        clipboard.setPrimaryClip(clip);

                        Toast.makeText(MainActivity.this, "copied to clipboard", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });

        AppCompatButton btn_Barcode = (AppCompatButton)findViewById(R.id.btn_Barcode);
        btn_Barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                getCode();
            }
        });

        AppCompatButton  btn_Barcode_scan = (AppCompatButton)findViewById(R.id. btn_Barcode_scan);
        btn_Barcode_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ScannerActivity.class);
                startActivity(intent);
            }
        });

        AppCompatButton btn_screenBrightness = (AppCompatButton)findViewById(R.id.btn_screenBrightness);
        btn_screenBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams layout = getWindow().getAttributes();
                if(mBright){
                    ContentResolver contentResolver = getContentResolver();
                    layout.screenBrightness = Settings.System.getFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS, /* default value */ 0);
                }else{
                    layout.screenBrightness = 0.5F ;
                }
                getWindow().setAttributes(layout);
                mBright=!mBright;
            }
        });

        btn_save = (AppCompatButton)findViewById(R.id. btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveImage(bit);
            }
        });
        btn_save.setVisibility(View.GONE);

        btn_saveCode = (AppCompatButton)findViewById(R.id.btn_saveCode);
        btn_saveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveCode(String.join(", ", CodeArray));
            }
        });

        spinner = (Spinner)findViewById(R.id.spinner);
        List<BarcodeFormat> BarcodeEnumValues = Arrays.asList(BarcodeFormat.values());
        ArrayAdapter<BarcodeFormat>  BarcodeList = new ArrayAdapter<BarcodeFormat>(this, android.R.layout.simple_spinner_dropdown_item, BarcodeEnumValues);
        spinner.setAdapter(BarcodeList);
        spinner.setSelection(11);

        SetRecyclerView();

    }

    private ArrayList<String> CodeArray = new ArrayList<String>();
    private MyAdapter adapter;
    private void SetRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rLayoutManager);

        adapter= new MyAdapter(CodeArray);
        recyclerView.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.ACTION_STATE_IDLE) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAbsoluteAdapterPosition();
                final int toPos = target.getAbsoluteAdapterPosition();
                //adapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);

              if(viewHolder != null){
                  TextView textView= viewHolder.itemView.findViewById(R.id.mtext);
                  Log.d("debug", "Value : " + textView.getText().toString());
                  mEditText.setText(textView.getText().toString());
                  getCode();
              }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void SaveImage(Bitmap finalBitmap) {
        if (android.os.Build.VERSION.SDK_INT <=32) {
            // permission check
            String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            int grant = ContextCompat.checkSelfPermission(this, permission);
            if (grant != PackageManager.PERMISSION_GRANTED) {
                String[] permission_list = new String[1];
                permission_list[0] = permission;
                ActivityCompat.requestPermissions(this, permission_list, 1);

                return;
            }
        }

        String root = Environment.getExternalStorageDirectory().toString();
        File FileDir = new File(root + "/Pictures/Barcode");
        FileDir.mkdirs();

        Date now = new Date();
        CharSequence mNow = android.text.format.DateFormat.format("yyyy-MM-dd HHmmss", now);
        String filename = BarcodeValue + "_" + BarcodeType + "_" + mNow.toString() +".jpg";
        filename= filename.replaceAll("[;\\/:*?\"<>|&']","");

        File file = new File (FileDir, filename);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            Toast.makeText(MainActivity.this, "saved " + filename, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error... " , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        btn_save.setVisibility(View.GONE);
    }

    private void SaveCode(String txt) {
        if (android.os.Build.VERSION.SDK_INT <=32) {
            // permission check
            String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            int grant = ContextCompat.checkSelfPermission(this, permission);
            if (grant != PackageManager.PERMISSION_GRANTED) {
                String[] permission_list = new String[1];
                permission_list[0] = permission;
                ActivityCompat.requestPermissions(this, permission_list, 1);

                return;
            }
        }

        String root = Environment.getExternalStorageDirectory().toString();
        File FileDir = new File(root + "/Download/Barcode");
        FileDir.mkdirs();

        Date now = new Date();
        CharSequence mNow = android.text.format.DateFormat.format("yyyy-MM-dd HHmmss", now);
        String filename = mNow.toString() +".txt";
        File file = new File (FileDir, filename);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(txt.getBytes());
            out.flush();
            out.close();

            Toast.makeText(MainActivity.this, "saved " + filename, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error... " , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void getCode() {
        ImageView ivCode = (ImageView) findViewById(R.id.imageView);

        BarcodeEncoder encoder = new BarcodeEncoder();
        try {
            BarcodeValue = mEditText.getText().toString();
            BarcodeType = spinner.getSelectedItem().toString();
            int mHeight = 200,mWidth = 800;
            switch(BarcodeType) {
                case "DATA_MATRIX":
                case "QR_CODE":
                    mHeight = 500;
                    mWidth = 500;
                    break;
                default:
            }
            bit = encoder.encodeBitmap(mEditText.getText().toString(), (BarcodeFormat)spinner.getSelectedItem(), mWidth, mHeight);
            ivCode.setImageBitmap(bit);


            btn_save.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "It doesn't work", Toast.LENGTH_SHORT).show();
        }

        if(!CodeArray.contains(BarcodeValue)){
            CodeArray.add(BarcodeValue);
            if(CodeArray.size() > 200){
                CodeArray.remove(0);
            }
            // notify adapter
            adapter.notifyDataSetChanged();
        }

        mTextView.setText(CodeArray.size()+"/200");
    }
    
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug","onResume()");

        if(!VariableEditor.ScanText.isEmpty()){
            mEditText.setText(VariableEditor.ScanText);
            getCode();
            VariableEditor.ScanText="";
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}