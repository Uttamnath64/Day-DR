package com.lit.litnotes.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Model.NoteTabModel;
import com.lit.litnotes.R;

import java.util.ArrayList;

public class NoteTabAdapter extends RecyclerView.Adapter<NoteTabAdapter.NoteTabViewHolder>{

    private final ArrayList<NoteTabModel> list;
    private final Context context;
    public int SELECTED_INDEX = -1 ;
    private ItemClickListener listener;

    public NoteTabAdapter(ArrayList<NoteTabModel> list, Context context,ItemClickListener listener){
        this.list = list;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public NoteTabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_tab_design,parent,false);

        return new NoteTabViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull NoteTabViewHolder holder, int position) {
        holder.Title.setText(list.get(position).getName());

        if(list.get(position).getClicked()){
            SELECTED_INDEX = holder.getAdapterPosition();
            list.get(position).setClicked(false);
        }


        if(SELECTED_INDEX == position) {
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.AppColor)));
            holder.Title.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }else {
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.SecondColor)));
            holder.Title.setTextColor(context.getResources().getColor(R.color.colorBlack_300));
        }
    }

    public void checkedItem(int pos){
        SELECTED_INDEX = pos;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class NoteTabViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView Title;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public NoteTabViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            Title = itemView.findViewById(R.id.textTitle);
            cardView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View view) {

                    if(SELECTED_INDEX != getAdapterPosition()){

                        notifyItemChanged(SELECTED_INDEX);
                        SELECTED_INDEX = getAdapterPosition();
                        notifyItemChanged(SELECTED_INDEX);

                        listener.onClick(view, list.get(getAdapterPosition()).getId(),(byte)0);
                    }

                }
            });
        }

    }



    private void setSelectItem(int index){
        if(index == RecyclerView.NO_POSITION) return;

        notifyItemChanged(SELECTED_INDEX);
        SELECTED_INDEX = index;
        notifyItemChanged(SELECTED_INDEX);
    }
}
