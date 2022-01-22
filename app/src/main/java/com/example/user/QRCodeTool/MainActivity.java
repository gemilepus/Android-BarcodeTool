package com.example.user.QRCodeTool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.example.user.QRCodeTool.View.MyAdapter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    private AppCompatButton btn_save;
    private String QRCode_Str;
    private EditText mEditText;
    private Bitmap bit;

    private boolean mBright = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        VariableEditor.ScanText = "";

        mEditText = (EditText)findViewById(R.id.editTextQRCode);
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

        AppCompatButton btn_QRCode = (AppCompatButton)findViewById(R.id.btn_QRCode);
        btn_QRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                getCode();
            }
        });

        AppCompatButton  btn_QRCode_scan = (AppCompatButton)findViewById(R.id. btn_QRCode_scan);
        btn_QRCode_scan.setOnClickListener(new View.OnClickListener() {
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
                    layout.screenBrightness = 0F ;
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
        // permission check
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }

        String root = Environment.getExternalStorageDirectory().toString();
        File FileDir = new File(root + "/Pictures/QRCode");
        FileDir.mkdirs();

        Date now = new Date();
        CharSequence mNow = android.text.format.DateFormat.format("yyyy-MM-dd", now);
        String filename = QRCode_Str + "_" + mNow.toString() +".jpg";
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

    public void getCode() {
        ImageView ivCode = (ImageView) findViewById(R.id.imageView);

        BarcodeEncoder encoder = new BarcodeEncoder();
        try {
            QRCode_Str = mEditText.getText().toString();
            bit = encoder.encodeBitmap(mEditText.getText().toString(), BarcodeFormat.QR_CODE, 500, 500);
            ivCode.setImageBitmap(bit);

            btn_save.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        if(!CodeArray.contains(QRCode_Str)){
            CodeArray.add(QRCode_Str);
            if(CodeArray.size() > 8){
                CodeArray.remove(0);
            }
            // notify adapter
            adapter.notifyDataSetChanged();
        }
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

        if(!VariableEditor.ScanText.equals("")){
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