package com.lit.litnotes.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lit.litnotes.Components.DateAndTime;
import com.lit.litnotes.Interface.CheckItem;
import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Model.TasksModel;
import com.lit.litnotes.R;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    ArrayList<TasksModel> list;
    Context context;
    ItemClickListener itemClickListener;
    CheckItem checkItem;

    public TasksAdapter(ArrayList<TasksModel> list, Context context, ItemClickListener itemClickListener, CheckItem checkItem) {
        this.list = list;
        this.context = context;
        this.itemClickListener  = itemClickListener;
        this.checkItem = checkItem;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_design,parent,false);
        return new TasksViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder holder, int position) {
        TasksModel tasksModel = list.get(position);
        holder.Text.setText(tasksModel.getText());


        // on checkbox checked
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkItem.onChecked(b,tasksModel.getId(), (byte) 201);
                tasksModel.setChecked(b);
                setStatus(tasksModel,holder);
            }
        });

        //set Checkbox
        holder.checkBox.setChecked(tasksModel.isChecked());

        //set Time
        if(tasksModel.isReminder() && tasksModel.isRepeat()) holder.DateTime.setText(new DateAndTime(tasksModel.getReminderDate()).getDateFromDate()+" at "+new DateAndTime(tasksModel.getReminderTime()).getTimeFromTime());
        else if(tasksModel.isReminder()) holder.DateTime.setText("every day "+new DateAndTime(tasksModel.getReminderTime()).getTimeFromTime());
        else holder.DateTime.setText(new DateAndTime(tasksModel.getDateTime()).getDateTimeFromTS());

        // Time icon visibility change
        if(tasksModel.isReminder()) holder.Time.setVisibility(View.VISIBLE);
        else holder.Time.setVisibility(View.GONE);

        // Status color change
        setStatus(tasksModel,holder);

        // onClick cardView
        holder.cardView.setOnClickListener(view -> itemClickListener.onClick(view,tasksModel.getId(),(byte) 201));

        // longClick cardView
        holder.cardView.setOnLongClickListener(view -> {
            itemClickListener.onLongClick(view,tasksModel.getId(),(byte) 201);
            return true;
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemClickListener.onLongClick(view,list.get(position).getId(),(byte)1);
                return false;
            }
        });

    }

    void setStatus(TasksModel tasksModel,TasksViewHolder holder){
//        if(tasksModel.isChecked()) holder.status.setCardBackgroundColor(context.getResources().getColor(R.color.AppColorSec));
//        else if(tasksModel.isReminder()) holder.status.setCardBackgroundColor(context.getResources().getColor(R.color.colorWhite_600));
//        else holder.status.setCardBackgroundColor(context.getResources().getColor(R.color.colorWhite_300));

        if(tasksModel.isChecked()) holder.Text.setTextColor(context.getResources().getColor(R.color.colorBlack_600));
        else holder.Text.setTextColor(context.getResources().getColor(R.color.colorBlack_300));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class TasksViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView Text, DateTime;
        ImageView Time;
        CheckBox checkBox;

        public TasksViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            Text = itemView.findViewById(R.id.textText);
            DateTime = itemView.findViewById(R.id.textTime);
            Time = itemView.findViewById(R.id.imgTime);
            checkBox = itemView.findViewById(R.id.checkBox);
            //status = itemView.findViewById(R.id.statusLayout);
        }
    }
}
