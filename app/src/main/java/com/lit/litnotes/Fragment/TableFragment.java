package com.lit.litnotes.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lit.litnotes.Adapters.TableAdapters;
import com.lit.litnotes.Components.DateAndTime;
import com.lit.litnotes.Components.LiveDataLoader;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Model.TableMode;
import com.lit.litnotes.R;

import org.w3c.dom.Entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TableFragment extends Fragment {

    LinkedHashMap<Integer,String> MainList = new LinkedHashMap<>();
    LinkedHashMap<Integer,String> Days = new LinkedHashMap<>();

    ArrayList<TableMode> DaysList;
    RecyclerView tableList;
    SQLiteDBManager sqLiteDBManager;
    SQLiteDBHelper sqLiteDBHelper;
    LiveDataLoader liveDataLoader;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);

        // set recycleView
        tableList = view.findViewById(R.id.tableList);
        tableList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        // set database class
        sqLiteDBHelper = new SQLiteDBHelper(getContext());
        sqLiteDBManager = new SQLiteDBManager(getContext());
        sqLiteDBManager.open();

        MainList.put(0,"Sunday");
        MainList.put(1,"Monday");
        MainList.put(2,"Tuesday");
        MainList.put(3,"Wednesday");
        MainList.put(4,"Thursday");
        MainList.put(5,"Friday");
        MainList.put(6,"Saturday");


        // get live data
        liveDataLoader = new ViewModelProvider(requireActivity()).get(LiveDataLoader.class);
        liveDataLoader.getCode().observe(requireActivity(), item ->{
            if(item == 301){
                setDataInRV();
            }
        });

        getCurrentList();
        setDataInRV();
        return view;
    }

    @SuppressLint("SimpleDateFormat")
    private void setDataInRV() {
        DaysList = new ArrayList<>();
        String DayData = "";
        int i = 0;
        for (Map.Entry<Integer,String> entry : Days.entrySet()){
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DAY_OF_MONTH,i);
            if(i == 0) DayData = " | Today";
            else if(i == 1) DayData = " | Tomorrow";
            else DayData = "";
            Cursor data = sqLiteDBManager.fetch("select count(*) from "+ SQLiteDBHelper.TR_TABLE +" where Day = '"+ entry.getKey()+"' ");
            data.moveToNext();
            DaysList.add(new TableMode(
                    entry.getKey(),
                    new SimpleDateFormat("EEE").format(calendar.getTime()),
                    new SimpleDateFormat("EEEE").format(calendar.getTime())+DayData,
                    new SimpleDateFormat("MMM dd").format(calendar.getTime()),
                    (i == 0),
                    (Integer.parseInt(data.getString(0)) >= 1)
            ));
            i++;
        }
        TableAdapters tableAdapters = new TableAdapters(DaysList,getContext());
        tableList.setAdapter(tableAdapters);
    }

    @SuppressLint("NewApi")
    private void getCurrentList() {

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,1);

        LinkedHashMap<Integer,String> list1 = new LinkedHashMap<>();
        LinkedHashMap<Integer,String> list2 = new LinkedHashMap<>();
        int id = 0;
        for (Map.Entry<Integer,String> data : MainList.entrySet()){
            if (new DateAndTime("").getDay(new Date(), true).toString().trim().equals(data.getValue().trim())){
                id = data.getKey();
                break;
            }
        }

        for (int i=0; i <= MainList.size()-1; i++) {
            if (id <= i) {
                list1.put(i, MainList.get(i));
            } else {
                list2.put(i, MainList.get(i));
            }
        }
        Days.putAll(list1);
        Days.putAll(list2);
    }
}