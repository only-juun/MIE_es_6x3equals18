package com.example.barcode_generator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Deliveries extends AppCompatActivity {

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
        setContentView(R.layout.activity_deliverylist);

        //progressDialog = new ProgressDialog((this));
        //progressDialog.setCancelable(false);
        //progressDialog.setMessage("Fetching Data...");
        //progressDialog.show();
        mFirebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<DeliveryContents>();//DeliveryContents를 담을 어레이 리스트(어뎁터 쪽으로 보냄)

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);



        EventChangeListener();
    }

    private void EventChangeListener() {
        Intent receiveIntent = getIntent();
        String coll_name = receiveIntent.getStringExtra("boxname");
        db.collection(coll_name).whereEqualTo("valid",true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (error != null){

                            //if (progressDialog.isShowing())
                            //    progressDialog.dismiss();
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()){

                            if (dc.getType() == DocumentChange.Type.ADDED){
                                if(dc.getDocument().contains("Info")) {
                                    arrayList.add(dc.getDocument().toObject(DeliveryContents.class));
                                }
                            }

                            adapter.notifyDataSetChanged();
                            //if (progressDialog.isShowing())
                            //    progressDialog.dismiss();
                        }
                    }
                });
    }
}