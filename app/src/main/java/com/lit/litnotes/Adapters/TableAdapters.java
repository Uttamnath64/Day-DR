package com.lit.litnotes.Adapters;

import static com.lit.litnotes.Adapters.ColorAdapter.SELECTED_INDEX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lit.litnotes.Activity.TableView;
import com.lit.litnotes.Components.DateAndTime;
import com.lit.litnotes.Database.Notify;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Database.Table;
import com.lit.litnotes.Database.Task;
import com.lit.litnotes.Model.TableDataModel;
import com.lit.litnotes.Model.TableMode;
import com.lit.litnotes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class TableAdapters  extends RecyclerView.Adapter<TableAdapters.TableDataHolder> {

    ArrayList<TableMode> list;
    ArrayList<TableDataModel> dataList;
    SQLiteDBManager sqLiteDBManager;
    SQLiteDBHelper sqLiteDBHelpers;
    Context context;
    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    static int selected = 0;

    public TableAdapters(ArrayList<TableMode> list, Context context) {
        this.list = list;
        this.context = context;
        sqLiteDBHelpers = new SQLiteDBHelper(context);
        sqLiteDBManager = new SQLiteDBManager(context);
        sqLiteDBManager.open();
    }

    @NonNull
    @Override
    public TableDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_design,parent,false);
        return new TableDataHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull TableDataHolder holder, int position) {
        TableMode tableMode = list.get(position);
        holder.dayShortName.setText(tableMode.getShortDay());
        holder.dayDate.setText(tableMode.getDayDate());
        holder.dayFullDate.setText(tableMode.getFullDay());

        if(tableMode.isSelected()) {
            selected = holder.getAdapterPosition();
            tableMode.setSelected(false);
        }
        if(tableMode.isHaveItem()){
            dataList = new ArrayList<>();
            Cursor data = sqLiteDBManager.fetch("Select * From "+ SQLiteDBHelper.TR_TABLE+" where Day = '"+tableMode.getId()+"'  ORDER BY UDateTime DESC LIMIT 4");
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            while (data.moveToNext()){
                Cursor notify = sqLiteDBManager.fetch("Select * from "+SQLiteDBHelper.TR_NOTIFY+" Where Id = '"+ data.getString(Table.R_ID.getValue()).trim()+"' ");
                notify.moveToNext();
                try {
                    dataList.add(new TableDataModel(data.getString(Table.TITLE.getValue()),(String)DateFormat.format("hh:mm a", Objects.requireNonNull(format.parse(notify.getString(Notify.R_TIME.getValue()))).getTime())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
            holder.recyclerView.setAdapter(new TableDataAdapter(dataList,context));
            holder.recyclerView.setRecycledViewPool(viewPool);
        }
        holder.CreateLayout.setOnClickListener(view -> ((Activity)context).startActivityForResult(new Intent(context,TableView.class).putExtra("Id",tableMode.getId()),1));
        holder.viewMoreBtn.setOnClickListener(view -> {
            ((Activity)context).startActivityForResult(new Intent(context,TableView.class).putExtra("Id",tableMode.getId()),1);
        });
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        if(selected == position){
            holder.upDownIcon.setImageResource(android.R.drawable.arrow_up_float);
            holder.DataLayout.setVisibility(View.GONE);
            holder.CreateLayout.setVisibility(View.GONE);
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.SecondColor));
            if(tableMode.isHaveItem())holder.DataLayout.setVisibility(View.VISIBLE);
            else holder.CreateLayout.setVisibility(View.VISIBLE);
            holder.line.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorWhite_300)));
        }else{
            holder.upDownIcon.setImageResource(android.R.drawable.arrow_down_float);
            holder.DataLayout.setVisibility(View.GONE);
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.colorWhite));
            holder.CreateLayout.setVisibility(View.GONE);
            holder.line.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorWhite)));
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class TableDataHolder extends  RecyclerView.ViewHolder{

        CardView TopBar, DataLayout, CreateLayout;
        TextView dayShortName,dayDate,dayFullDate,viewMoreBtn;
        ImageView upDownIcon;
        FrameLayout line;
        MaterialCardView cardView;
        RecyclerView recyclerView;

        public TableDataHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            TopBar = itemView.findViewById(R.id.topBar);
            DataLayout = itemView.findViewById(R.id.dataLayout);
            CreateLayout = itemView.findViewById(R.id.createLayout);
            dayShortName = itemView.findViewById(R.id.dayShortName);
            dayDate = itemView.findViewById(R.id.dayDate);
            dayFullDate = itemView.findViewById(R.id.dayFullDate);
            upDownIcon = itemView.findViewById(R.id.upDownIcon);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            upDownIcon = itemView.findViewById(R.id.upDownIcon);
            line = itemView.findViewById(R.id.line);
            viewMoreBtn = itemView.findViewById(R.id.viewMoreBtn);

            TopBar.setOnClickListener(view -> {
                if(selected != getAdapterPosition()){

                    notifyItemChanged(selected);
                    selected = getAdapterPosition();
                    notifyItemChanged(selected);

                }else{
                    notifyItemChanged(selected);
                    selected = -1;
                    notifyItemChanged(selected);
                }
            });
        }
    }
}
