package com.lit.litnotes.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Model.TableViewModel;
import com.lit.litnotes.R;

import java.util.ArrayList;

public class TableViewAdapter extends RecyclerView.Adapter<TableViewAdapter.TableViewDataHolder> {

    ArrayList<TableViewModel> list;
    Context context;
    ItemClickListener listener;

    public TableViewAdapter(ArrayList<TableViewModel> list, Context context,ItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TableViewDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_view_item_design,parent,false);
        return new TableViewDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewDataHolder holder, int position) {
        TableViewModel tableViewModel = list.get(position);

        holder.Text.setText(tableViewModel.getText());
        holder.Time.setText(tableViewModel.getDate());

        holder.cardView.setOnClickListener(view -> {
            if (listener != null) listener.onClick(view, String.valueOf(tableViewModel.getId()), (byte) 0);
        });
        holder.cardView.setOnLongClickListener(view -> {
            listener.onLongClick(view, String.valueOf(tableViewModel.getId()),(byte)0);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class TableViewDataHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView Text, Time;

        public TableViewDataHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            Text = itemView.findViewById(R.id.Text);
            Time = itemView.findViewById(R.id.Time);
        }
    }
}
