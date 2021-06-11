package com.example.barcode_generator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder> {

    ArrayList<DeliveryContents> arrayList;
    Context context;

    public MyAdapter(ArrayList<DeliveryContents> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.after_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        DeliveryContents deliveryContents = arrayList.get(position);

        holder.code.setText(deliveryContents.getCode());
        holder.Info.setText(deliveryContents.getInfo());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
                TextView code;
                TextView Info;

        public MyViewHolder(@NonNull @NotNull View itemView) {
                    super(itemView);
                    code = itemView.findViewById(R.id.tv_invoice);
                    Info = itemView.findViewById(R.id.tv_contents);

        }
    }
}
