package com.example.barcode_generator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ArrayListMultimap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import kotlin.jvm.internal.MagicApiIntrinsics;

import static java.lang.Thread.sleep;

public class LogCheck extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<LogContents> arrayList;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;    // cloud firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_check);

        mFirebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<LogContents>();

        adapter = new LogAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        Intent receiveIntent = getIntent();
        String coll_name = receiveIntent.getStringExtra("boxname");
        CollectionReference db = FirebaseFirestore.getInstance().collection(coll_name);


//        Map map = db.document("Log").;
//        map.size();
////        db.document("Log").get().getResult().getData()
//        Log.d("TAG", String.valueOf(map.size()));
//        Log.d("TAG2",db.document("Log").get().getResult().getData().entrySet().toString());
//        ..addOnSuccessListener(new OnSuccessListener<DocumentSnapshot> () {
//            @Override
//            public void onSuccess(@NonNull @NotNull Task<DocumentSnapshot> task){
//                task.getResult().getData();
//                Log.d("TAG",task.getResult().getData().toString());
//                Log.d("TAG2",task.getResult().getData().entrySet().toString());
//
//            }
//        });


        EventChangeListener();
        // 로그 확인 창에 들어갈 때 마다 일정 개수의 로그만을 남기고 지우기..
    }

    private void EventChangeListener() {
        Intent receiveIntent = getIntent();
        String coll_name = receiveIntent.getStringExtra("boxname");

        db.collection(coll_name).document("Log").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    //if (progressDialog.isShowing())
                    //    progressDialog.dismiss();
                    Log.e("Firestore error", error.getMessage());
                    return;
                }

                value.getReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> LogsMap = document.getData();
                                Object[] mapkey = LogsMap.keySet().toArray();
                                Arrays.sort(mapkey);
                                Integer log_num = 0;
                                for (Object num : mapkey){
                                    Map<String, Object> Logs = (Map<String, Object>)LogsMap.get(num);
                                    Log.d("map", LogsMap.get(num).toString());
                                    log_num += 1;
                                    Log.d("lognum", log_num.toString());
                                    if(mapkey.length > 10 && log_num <= mapkey.length-10) {
                                        LogsMap.remove(num);
                                    }
                                    else {
                                        boolean none = false;
                                        String info = "";
                                        String event = "";
                                        LogContents log = new LogContents();
                                        for (Map.Entry<String, Object> Contents : Logs.entrySet()) {
                                            Log.d("TAG", Contents.getKey().toString());
//                                        arrayList.add(value.toObject(LogContents.class));
                                            if (Contents.getKey().equals("Code")) {
                                                if (Contents.getValue().toString().equals("None")) {
                                                    none = true;
                                                    log.setEvent(event);
                                                } else {
                                                    log.setEvent(info + "    " + event);

                                                }
                                            }
                                            if (Contents.getKey().equals("Info")) {
                                                info = Contents.getValue().toString();
                                            }
                                            if (Contents.getKey().equals("Date")) {
                                                log.setDate(Contents.getValue().toString());
                                                Log.d("Event", Contents.getValue().toString());
//                                            arrayList.add );
//                                            arrayList.get(0).setDate(Contents.getValue().toString());
                                            }
                                            if (Contents.getKey().equals("Event")) {
                                                event = Contents.getValue().toString();
                                                //log.setEvent(code_info + Contents.getValue().toString());
//                                            arrayList.get(1).setEvent(Contents.getValue().toString());
                                            }
                                        }
                                        arrayList.add(log);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });

            }
        });
    }
}