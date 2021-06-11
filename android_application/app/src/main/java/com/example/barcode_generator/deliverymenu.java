package com.example.barcode_generator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class deliverymenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverymenu);

        Intent receiveIntent = getIntent();
        String coll_name = receiveIntent.getStringExtra("boxname");
        Log.d("coll name", coll_name);

        Button btn_before = findViewById(R.id.bnt_before);
        Button btn_after = findViewById(R.id.bnt_after);

        btn_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(deliverymenu.this, Deliveries.class);
                intent.putExtra("boxname", coll_name);
                startActivity(intent);
            }
        });

        btn_after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(deliverymenu.this, Complete.class);
                intent.putExtra("boxname", coll_name);
                startActivity(intent);
            }
        });

    }
}