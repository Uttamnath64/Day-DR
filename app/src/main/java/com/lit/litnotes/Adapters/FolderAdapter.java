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
import com.lit.litnotes.Model.FolderModel;
import com.lit.litnotes.R;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    ArrayList<FolderModel> list = new ArrayList<>();
    Context context;
    ItemClickListener listener;

    public FolderAdapter(ArrayList<FolderModel> list, Context context,ItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_design,parent,false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        holder.Name.setText(list.get(position).getName());
        holder.NumberOfNotes.setText(list.get(position).getNumberOfNotes());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onClick(view,list.get(position).getId(), (byte) 1);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(view,list.get(position).getId(),(byte)1);
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView Name,NumberOfNotes;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            Name = itemView.findViewById(R.id.nameOfFolder);
            NumberOfNotes = itemView.findViewById(R.id.numberOfNotes);

        }
    }
}
