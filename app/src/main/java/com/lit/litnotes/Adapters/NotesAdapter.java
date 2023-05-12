package com.lit.litnotes.Adapters;

import static androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lit.litnotes.Activity.CreateAndViewNote;
import com.lit.litnotes.Components.ColorManager;
import com.lit.litnotes.Components.DateAndTime;
import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Model.NotesModel;
import com.lit.litnotes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>{

    private static List<NotesModel> list = new ArrayList<>();
    private static List<NotesModel> ListSecond = new ArrayList<>();
    private final Context context;
    String[] arr;
    ColorManager colorManager;
    ItemClickListener listener = null;

    public  NotesAdapter(ArrayList<NotesModel> list, Context context, ItemClickListener listener){
        this.list = list;
        this.ListSecond = list;
        this.context = context;
        this.listener = listener;
        arr = new String[] {
                "#FFFFFF",
                "#FFD6C0",
                "#9FF5CE",
                "#F5BDE7",
                "#DEBEFF",
                "#A4E2FB",
                "#FFBDBE"
        };
        colorManager = new ColorManager();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_design,parent,false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NotesModel notesModel = list.get(position);
        holder.Title.setText(notesModel.getTitle());
        holder.Text.setText(notesModel.getText());
        holder.Time.setText(new DateAndTime(notesModel.getTime()).getDateTimeFromTS());


        holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(colorManager.getColor(notesModel.getColorId()))));
        if(notesModel.getColorId() != 0){
            holder.Title.setTextColor(context.getResources().getColor(R.color.Black));
            holder.Text.setTextColor(context.getResources().getColor(R.color.Black_300));
            holder.Time.setTextColor(context.getResources().getColor(R.color.Black_600));
        }

        holder.cardView.setOnClickListener(view -> {
            try{
                ((Activity)context).startActivityForResult(new Intent(context,
                                CreateAndViewNote.class)
                                .putExtra("Id",notesModel.getId()),1,
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,holder.cardView,"NoteView").toBundle());
            }catch (Exception e){
                e.printStackTrace();
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


    public static class NotesViewHolder extends RecyclerView.ViewHolder{

        TextView Title,Text,Time;
        CardView cardView;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            Title = itemView.findViewById(R.id.textTitle);
            Text = itemView.findViewById(R.id.textText);
            Time = itemView.findViewById(R.id.textTime);
        }
    }

}
