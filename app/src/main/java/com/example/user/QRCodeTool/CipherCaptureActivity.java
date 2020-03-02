package com.example.user.QRCodeTool;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cipherlab.barcode.GeneralString;
import com.cipherlab.barcode.ReaderManager;

public class CipherCaptureActivity extends Activity {

    private ReaderManager mReaderManager;

    private IntentFilter filter;
    Button b1 = null;
    ReaderManager m_RM = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scan_test);
        b1 = (Button) findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (m_RM != null) {
                    m_RM.SoftScanTrigger();
                }
            }
        });
        m_RM = ReaderManager.InitInstance(this);
        filter = new IntentFilter();
        filter.addAction(GeneralString.Intent_SOFTTRIGGER_DATA);
        registerReceiver(myDataReceiver, filter);
    }

    private final BroadcastReceiver myDataReceiver = new BroadcastReceiver() {
    @Override
     public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)){// Fetch data from the intent
            String sDataStr = intent.getStringExtra(GeneralString.BcReaderData);
            Toast.makeText(CipherCaptureActivity.this, "Decoded data is " + sDataStr,Toast.LENGTH_SHORT).show();
        }
     }
    };





}
