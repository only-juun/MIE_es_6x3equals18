package com.example.barcode_generator;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private EditText mEtEmail, mEtPwd;          //로그인 입력필드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent receiveIntent = getIntent();
        String coll_name = receiveIntent.getStringExtra("boxname");
        Log.d("coll name", coll_name);

        Button btn_Dreister = findViewById(R.id.btn_Dregister);
        Button btn_qr = findViewById(R.id.btn_qr);
        Button btn_log = findViewById(R.id.btn_log);
        Button btn_delete = findViewById(R.id.btn_delete);
        Button btn_Dlist = findViewById(R.id.btn_Dlist);

        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GenerateQR.class);
                intent.putExtra("boxname", coll_name);
                startActivity(intent);
            }
        });

        btn_Dreister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeliveryRegister.class);
                intent.putExtra("boxname", coll_name);
                startActivity(intent);
            }
        });

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogCheck.class);
                intent.putExtra("boxname", coll_name);
                startActivity(intent);
            }
        });

        btn_Dlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, deliverymenu.class);
                intent.putExtra("boxname", coll_name);
                startActivity(intent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserDelete.class);
                intent.putExtra("boxname", coll_name);
                startActivity(intent);
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserDelete.class);
                startActivity(intent);
            }
        });
    }
}