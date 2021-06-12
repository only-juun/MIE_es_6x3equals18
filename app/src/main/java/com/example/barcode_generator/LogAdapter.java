package com.example.barcode_generator;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class LogAdapter extends RecyclerView.Adapter <LogAdapter.LogViewHolder> {

    ArrayList<LogContents> arrayList;
    Context context;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference ref;

    public LogAdapter(ArrayList<LogContents> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NotNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_list, parent, false);

        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LogViewHolder holder, int position) {

        LogContents logContents = arrayList.get(position);

        holder.Event.setText(logContents.getEvent());
        holder.Date.setText(logContents.getDate());
        Log.d("DATE",logContents.getDate());

        if (arrayList.get(position).getEvent().equals("도난 시도 감지")){
            holder.btn_down.setVisibility(View.VISIBLE);
        }
        else{
            holder.btn_down.setVisibility(View.INVISIBLE);
        }

        holder.btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageReference = firebaseStorage.getInstance().getReference();
                ref = storageReference.child(arrayList.get(position).getDate()+".png");
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        downloadFiles(context,arrayList.get(position).getDate(),".png",DIRECTORY_DOWNLOADS, url);
                    }
                });
            }
        });
    }

    private void downloadFiles(Context context, String fileName, String fileExtension, String destinationDirectory, String url)
    {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName+fileExtension);

        downloadManager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {
        TextView Event;
        TextView Date;
        Button btn_down;

        public LogViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            Event = itemView.findViewById(R.id.tv_event);
            Date = itemView.findViewById(R.id.tv_date);
            btn_down = itemView.findViewById(R.id.btn_down);
        }
    }
}