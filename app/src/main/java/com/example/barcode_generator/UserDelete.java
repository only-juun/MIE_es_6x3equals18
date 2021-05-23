package com.example.barcode_generator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class UserDelete extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private FirebaseFirestore db = FirebaseFirestore.getInstance();     // cloud firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_delete);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Button btn_delete = findViewById(R.id.btn_delete);


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원탈퇴
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                mFirebaseAuth.getCurrentUser().delete();
                db.collection(firebaseUser.getUid()).document("UserAccount").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection(firebaseUser.getUid()).document("Log").delete();
                        db.collection(firebaseUser.getUid()).document("QRcode").delete();
                    Toast.makeText(UserDelete.this, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserDelete.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(UserDelete.this, "회원탈퇴가 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserDelete.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });

        Button btn_no = findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 화면으로 이동
                Intent intent = new Intent(UserDelete.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}