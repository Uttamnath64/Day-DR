package com.lit.litnotes.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lit.litnotes.Model.TableDataModel;
import com.lit.litnotes.R;

import java.util.ArrayList;

public class TableDataAdapter extends RecyclerView.Adapter<TableDataAdapter.TableDataViewHolder> {

    ArrayList<TableDataModel> list;
    Context context;

    public TableDataAdapter(ArrayList<TableDataModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public TableDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_data_design,parent,false);
        return new TableDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableDataViewHolder holder, int position) {
        TableDataModel tableDataModel = list.get(position);
        holder.text.setText(tableDataModel.getText());
        holder.time.setText(tableDataModel.getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class TableDataViewHolder extends RecyclerView.ViewHolder{

        TextView time,text;

        public TableDataViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.Time);
            text = itemView.findViewById(R.id.Text);
        }
    }
}
