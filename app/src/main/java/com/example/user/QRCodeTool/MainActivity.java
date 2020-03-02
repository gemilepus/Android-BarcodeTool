package com.example.user.QRCodeTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VariableEditor.ScanText = "";

        AppCompatButton btn_login = (AppCompatButton)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);


            }
        });



        ProgressBar progressBar = findViewById(R.id.progressbar);
        //progressBar.setVisibility(android.widget.ProgressBar.VISIBLE);
        progressBar.setVisibility(android.widget.ProgressBar.INVISIBLE);

        Intent intent = new Intent(getApplication(), QRCodeActivity.class);
        startActivity(intent);

        finish();

    }

}
