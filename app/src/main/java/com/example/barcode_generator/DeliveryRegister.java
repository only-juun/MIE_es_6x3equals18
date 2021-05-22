package com.example.barcode_generator;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeliveryRegister extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mEtInvoice, mEtContents;
    private Button mBtnDregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("BigBox");

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
        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid())
                .child("DeliveryContents").child(Invoice).setValue(dc);
    }
}
