package com.example.keranjangpetani;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterRecyclerview extends RecyclerView.Adapter<AdapterRecyclerview.ViewHolder>{

    Context context;
    ArrayList<Product> dataItem;

    public AdapterRecyclerview(Context context, ArrayList<Product> dataItem) {
        this.context = context;
        this.dataItem = dataItem;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvPrice;
        ImageView ivThumbnail;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

    AdapterRecyclerview(ArrayList<Product> data) {
        this.dataItem = data;
    }

    @NonNull
    @Override
    public AdapterRecyclerview.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecyclerview.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvTitle.setText(dataItem.get(position).getTitle());
        holder.tvPrice.setText(dataItem.get(position).getPrice());
        Uri url = dataItem.get(position).getImage();
        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(holder.ivThumbnail);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailproductActivity.class);
                String uuid = dataItem.get(position).getUuid();
                Log.d("Tes UUID", uuid);
                intent.putExtra("productId", uuid);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return dataItem.size();
    }
}
