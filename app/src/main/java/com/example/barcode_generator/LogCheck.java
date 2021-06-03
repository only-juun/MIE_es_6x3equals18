package com.example.barcode_generator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LogCheck extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_check);


        Intent receiveIntent = getIntent();
        String coll_name = receiveIntent.getStringExtra("boxname");
        CollectionReference db = FirebaseFirestore.getInstance().collection(coll_name);

        // 로그 확인 창에 들어갈 때마다 한달이상 된 document를 지우기
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String formatted_time = current.format(formatter);
        Long current_time = Long.parseLong(formatted_time);
        Long before_month = current_time - 1000000;
        if (formatted_time.substring(4,6) == "01"){
            before_month = current_time - 89000000;  //2021.01.03.12:34 - 89.00.00:00 = 2020.12.03.12:34 (101-89=12)
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


        // 로그 확인 창에 들어갈 때 마다 일정 개수의 로그만을 남기고 지우기..
    }
}