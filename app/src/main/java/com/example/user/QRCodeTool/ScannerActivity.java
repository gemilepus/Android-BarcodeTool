package com.example.user.QRCodeTool;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private int OnResult  = 0;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);

        // permission check
        String permission = Manifest.permission.CAMERA;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void handleResult(Result rawResult) {

        if(OnResult ==0){
            OnResult=1;

            // Note:
            // * Wait 2 seconds to resume the preview.
            // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
            // * I don't know why this is the case but I don't have the time to figure out.

            // mScannerView.stopCameraPreview();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScannerView.resumeCameraPreview(ScannerActivity.this);
                }
            }, 10);
            //mScannerView.getRootView();

//            Dialog settingsDialog = new Dialog(this);
//            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
//
//            ImageView im = (ImageView) findViewById(R.id.image);
//            im.setImageBitmap( loadBitmapFromView(mScannerView.getRootView())  );
//            settingsDialog.show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Info");
            builder.setMessage("Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString() );

            VariableEditor.ScanText =  rawResult.getText();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO
                    dialog.dismiss();

                    OnResult=0;
                    //mScannerView.resumeCameraPreview(ScannerActivity.this);
                    finish();
                }
            });
//            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                //TODO
//                dialog.dismiss();
//            }
//        });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}