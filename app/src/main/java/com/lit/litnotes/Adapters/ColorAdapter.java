package com.lit.litnotes.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Model.ColorModel;
import com.lit.litnotes.R;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    Context context;
    ArrayList<ColorModel> list;
    static int SELECTED_INDEX = 0;
    ItemClickListener listener;

    public ColorAdapter( ArrayList<ColorModel> list, Context context, ItemClickListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_layout,parent,false);
        return new ColorViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        ColorModel colorModel = list.get(position);
        holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(colorModel.getColor())));
        if(colorModel.isChecked()){
            SELECTED_INDEX = holder.getAdapterPosition();
            colorModel.setChecked(false);
        }

        if(SELECTED_INDEX == position){
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.AppColor));
        }else{
            holder.cardView.setStrokeColor(context.getResources().getColor(colorModel.getColor()));
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ColorViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView cardView;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(view -> {
                if(SELECTED_INDEX != getAdapterPosition()){

                    notifyItemChanged(SELECTED_INDEX);
                    SELECTED_INDEX = getAdapterPosition();
                    notifyItemChanged(SELECTED_INDEX);

                    listener.onClick(view,list.get(getAdapterPosition()).getId(), (byte) 10);
                }
            });
        }
    }
}
