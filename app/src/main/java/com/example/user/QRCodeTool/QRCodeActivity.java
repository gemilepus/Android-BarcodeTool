package com.example.user.QRCodeTool;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Random;

public class QRCodeActivity extends Activity {

    private AppCompatButton btn_save;
    private String QRCode_Str;
    EditText etContent;
    Bitmap bit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        AppCompatButton btn_login = (AppCompatButton)findViewById(R.id.btn_QRCode);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                genCode();
                btn_save.setVisibility(View.VISIBLE);;
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

        btn_save = (AppCompatButton)findViewById(R.id. btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveImage(bit);
            }
        });
        btn_save.setVisibility(View.GONE);

//        //to get the image from the ImageView (say iv)
//        BitmapDrawable draw = (BitmapDrawable) iv.getDrawable();
//        Bitmap bitmap = draw.getBitmap();
//
//        FileOutputStream outStream = null;
//        File sdCard = Environment.getExternalStorageDirectory();
//        File dir = new File(sdCard.getAbsolutePath() + "/YourFolderName");
//        dir.mkdirs();
//        String fileName = String.format("%d.jpg", System.currentTimeMillis());
//        File outFile = new File(dir, fileName);
//        outStream = new FileOutputStream(outFile);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//        outStream.flush();
//        outStream.close();
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
        File myDir = new File(root + "/saved_QRCode_images");

        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+   QRCode_Str +".jpg";
        //String fname = "Image-"+   filterDate( etContent.getText().toString()) +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_save.setVisibility(View.GONE);
    }

//    public String filterDate(String str){
//        String filter = "[^0-9/-／]"; // 指定要過濾的字元
//        Pattern p = Pattern.compile(filter);
//        Matcher m = p.matcher(str);
//
//        Toast.makeText(this, str2, Toast.LENGTH_SHORT).show();
//        return m.replaceAll("").trim(); // 將非上列所設定的字元全部replace 掉
//    }

    public void genCode() {
        ImageView ivCode = (ImageView) findViewById(R.id.imageView);
        etContent = (EditText) findViewById(R.id.editTextQRCode);
        BarcodeEncoder encoder = new BarcodeEncoder();
        try {
         QRCode_Str = etContent.getText().toString();
         bit = encoder.encodeBitmap(etContent.getText().toString(), BarcodeFormat.QR_CODE,
                    500, 500);
            ivCode.setImageBitmap(bit);
        } catch (WriterException e) {
            e.printStackTrace();
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

    /***  Hides the soft keyboard */
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
        //Toast.makeText(MainActivity.this ,   "onResume()" , Toast.LENGTH_SHORT).show(); // Crash Test
       if(!VariableEditor.ScanText.equals("")){
           etContent.setText(VariableEditor.ScanText);
           genCode();
           VariableEditor.ScanText="";
       }
    }
}