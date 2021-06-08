package com.example.barcode_generator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Complete extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DeliveryContents> arrayList;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;    // cloud firestore
    //private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        //progressDialog = new ProgressDialog((this));
        //progressDialog.setCancelable(false);
        //progressDialog.setMessage("Fetching Data...");
        //progressDialog.show();
        mFirebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<DeliveryContents>();//DeliveryContents를 담을 어레이 리스트(어뎁터 쪽으로 보냄)

        adapter = new MyAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        EventChangeListener();
    }
    //바코드 안뜨도록 걸러내는 작업 필요함
    private void EventChangeListener() {
        db.collection("box").whereEqualTo("valid",false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value,
                                        @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error)
                    {
                        if (error != null){

                            //if (progressDialog.isShowing())
                            //    progressDialog.dismiss();
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()){

                            if (dc.getType() == DocumentChange.Type.ADDED){
                                arrayList.add(dc.getDocument().toObject(DeliveryContents.class));
                            }

                            adapter.notifyDataSetChanged();
                            //if (progressDialog.isShowing())
                            //    progressDialog.dismiss();
                        }
                    }
                });
    }
}