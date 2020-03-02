package com.example.user.QRCodeTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

public class LoginActivity extends Activity {



    private AppCompatButton btn , btntemp , btnlogout , btn_qrcode_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        btn = (AppCompatButton)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplication(), EditActivity.class);
                startActivity(intent);


            }
        });


        btntemp = (AppCompatButton)findViewById(R.id.btn_temp);
        btntemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplication(), CipherCaptureActivity.class);
//                startActivity(intent);
            }
        });



        btnlogout = (AppCompatButton)findViewById(R.id.btn_logout);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_qrcode_view = (AppCompatButton)findViewById(R.id.btn_qrcode_view);
        btn_qrcode_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), QRCodeActivity.class);
                startActivity(intent);
            }
        });


    }



}
