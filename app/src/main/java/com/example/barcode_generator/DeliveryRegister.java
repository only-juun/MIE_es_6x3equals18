package com.example.barcode_generator;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

public class DeliveryRegister extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private EditText mEtInvoice, mEtContents;
    private Button mBtnDregister;
    private CollectionReference db;    // cloud firestore


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mEtInvoice = findViewById(R.id.et_invoice);
        mEtContents = findViewById(R.id.et_contents);
        mBtnDregister = findViewById(R.id.btn_Dregister);

        mBtnDregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strInvoice = mEtInvoice.getText().toString();
                String strContents = mEtContents.getText().toString();

                addDelivery(strInvoice, strContents);
                Toast.makeText(DeliveryRegister.this, "택배등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeliveryRegister.this, MainActivity.class);
                startActivity(intent);//현재 액티비티 파괴
                finish();
            }
        });
    }

    public void addDelivery(String Invoice, String Contents){
        DeliveryContents dc = new DeliveryContents(Invoice, Contents);
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
//        DocumentReference blank = db.whereEqualTo("valid","false").get().getResult().getDocuments().get(1).getReference();
        //blank.set(Map.of("code",Invoice,"info",Contents));

        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String formatted_time = current.format(formatter);
        db = FirebaseFirestore.getInstance().collection(firebaseUser.getUid());
        db.add(Map.of("code",Invoice,"info",Contents));
        db.document("Log").set((Map.of(Invoice , Map.of("Code",Invoice, "Date", formatted_time,"Event","택배 등록","Info", Contents))), SetOptions.merge());
        //db.whereEqualTo().get().
    }
}
