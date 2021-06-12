package com.example.barcode_generator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Any;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class deliverymenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverymenu);

        Intent receiveIntent = getIntent();
        String coll_name = receiveIntent.getStringExtra("boxname");
        CollectionReference db = FirebaseFirestore.getInstance().collection(coll_name);
        // 택배 확인 창에 들어갈 때마다 한달이상 되었으면서 이미 받은 택배의 document를 지우기
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formatted_time = current.format(formatter);
        Long current_time = Long.parseLong(formatted_time);
        Long before_month = current_time - 100000000;
        if (formatted_time.substring(4,6) == "01"){
            before_month = current_time - 8900000000L;  //2021.01.03.12:34:56 - 89.00.00:00:00 = 2020.12.03.12:34:56 (101-89=12)
        }

        Log.d("TAG", current_time.toString());
        db.whereLessThan("Date",before_month).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getBoolean("valid") == false) {
                            Log.d("TAG", document.getId());
                            document.getReference().delete();
                        }
                        Log.d("TAG", document.getId() + " => " + document.getData());
                        //05-27_11:30_gvzx => {code=1212, valid=false, Info=gvzx, Date=2.02105274368E11}
                    }
                }
            }
        });


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