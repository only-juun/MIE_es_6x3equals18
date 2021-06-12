package com.example.barcode_generator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LogAdapter extends RecyclerView.Adapter <LogAdapter.LogViewHolder> {

    ArrayList<LogContents> arrayList;
    Context context;

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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {
                TextView Event;
                TextView Date;

        public LogViewHolder(@NonNull @NotNull View itemView) {
                    super(itemView);
                    Event = itemView.findViewById(R.id.tv_event);
                    Date = itemView.findViewById(R.id.tv_date);

        }
    }
}
