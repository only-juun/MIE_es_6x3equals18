package com.example.barcode_generator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter <CustomAdapter.CustomViewHolder> {

    ArrayList<DeliveryContents> arrayList;
    Context context;

    public CustomAdapter(ArrayList<DeliveryContents> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NotNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CustomViewHolder holder, int position) {

        DeliveryContents deliveryContents = arrayList.get(position);

        holder.code.setText(deliveryContents.getCode());
        holder.Info.setText(deliveryContents.getInfo());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
                TextView code;
                TextView Info;

        public CustomViewHolder(@NonNull @NotNull View itemView) {
                    super(itemView);
                    code = itemView.findViewById(R.id.tv_invoice);
                    Info = itemView.findViewById(R.id.tv_contents);

        }
    }
}
