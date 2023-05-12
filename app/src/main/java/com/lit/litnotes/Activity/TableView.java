package com.lit.litnotes.Activity;

import static android.content.res.ColorStateList.valueOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.lit.litnotes.Adapters.TableViewAdapter;
import com.lit.litnotes.Components.CreateLayout;
import com.lit.litnotes.Components.DateAndTime;
import com.lit.litnotes.Components.ReminderHelper;
import com.lit.litnotes.Database.Notify;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Database.Table;
import com.lit.litnotes.Database.Task;
import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Model.TableViewModel;
import com.lit.litnotes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TableView extends AppCompatActivity implements ItemClickListener {

    // components
    RecyclerView recyclerView;
    ImageView backBtn;
    CardView createBtn;
    TextView Title;

    // alert message
    ConstraintLayout alertLayout;
    TextView alertText;
    ImageView alertImage;

    // database
    SQLiteDBHelper sqLiteDBHelper;
    SQLiteDBManager sqLiteDBManager;

    // variables
    String TampId = null;
    int sizeOfData = 0;
    int DayId = 0;
    boolean isChanged = false;
    Intent intent,dataIntent;
    ArrayList<TableViewModel> list;
    LinkedHashMap<Integer,String> MainList = new LinkedHashMap<>();
    String TimeText = null;

    //create BS
    TextView BSTitle,BSReminderText,BSBtbText;
    CardView BSSaveBtn,BSReminderBtn;
    EditText BSText;
    CreateLayout createLayout;
    Dialog tableAddEdit,setDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);

        // components
        recyclerView = findViewById(R.id.recyclerView);
        createBtn = findViewById(R.id.createBtn);
        backBtn = findViewById(R.id.backBtn);
        Title = findViewById(R.id.Title);

        // alert message
        alertLayout = findViewById(R.id.alertLayout);
        alertText = findViewById(R.id.alertText);
        alertImage = findViewById(R.id.alertImage);

        // database
        sqLiteDBHelper = new SQLiteDBHelper(this);
        sqLiteDBManager = new SQLiteDBManager(this);
        sqLiteDBManager.open();

        // set Data in list
        MainList.put(0,"Sunday");
        MainList.put(1,"Monday");
        MainList.put(2,"Tuesday");
        MainList.put(3,"Wednesday");
        MainList.put(4,"Thursday");
        MainList.put(5,"Friday");
        MainList.put(6,"Saturday");

        // variables
        intent = new Intent();
        dataIntent = getIntent();
        DayId = dataIntent.getIntExtra("Id",0);
        Title.setText(MainList.get(DayId));

        // set RecycleView Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });


        getTableView();
        createBottomSheet();

        createBtn.setOnClickListener(view -> {
            TampId = null;
            TimeText = null;
            BSTitle.setText("Create Task");
            BSReminderText.setText("Set Reminder");
            BSText.setText("");
            setSubmitBtn();
            tableAddEdit.show();
        });

    }


    @SuppressLint("SimpleDateFormat")
    private void getTableView() {
        list = new ArrayList<>();
        Cursor data = sqLiteDBManager.fetch("Select * From "+ SQLiteDBHelper.TR_TABLE+" where Day = '"+DayId+"' ORDER BY UDateTime DESC");
        while (data.moveToNext()){
            Cursor notify = sqLiteDBManager.fetch("Select * from "+SQLiteDBHelper.TR_NOTIFY+" Where Id = '"+ data.getString(Table.R_ID.getValue())+"' ");
            notify.moveToNext();
            list.add(new TableViewModel(
                    Integer.parseInt(data.getString(Table.ID.getValue())),
                    data.getString(Table.TITLE.getValue()),
                    new DateAndTime(notify.getString(Notify.R_TIME.getValue())).getTimeFromTime()));
        }

        TableViewAdapter tableViewAdapter = new TableViewAdapter(list,this,this);
        recyclerView.setAdapter(tableViewAdapter);

        sizeOfData = list.size();

        if(sizeOfData <= 0){
            alertLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            alertImage.setImageResource(R.drawable.list);
            alertText.setText("No task here yet");
        }else{
            alertLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        if(isChanged){
            intent.putExtra(HomeScreen.DATA_CODE,"301");
            setResult(RESULT_OK,intent);
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view, String Id, byte IF_ID) {
        if(IF_ID == 0){
            BSText.setText("");
            TampId = Id;
            TimeText = null;
            Cursor row = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_TABLE +" Where Id = '"+TampId+"' ");
            if(row.moveToNext()){
                Cursor notify = sqLiteDBManager.fetch("Select * from "+SQLiteDBHelper.TR_NOTIFY+" Where Id = '"+ row.getString(Table.R_ID.getValue())+"' ");
                notify.moveToNext();
                BSText.setText(row.getString(Table.TITLE.getValue()));
                TimeText = notify.getString(Notify.R_TIME.getValue());
            }
            BSTitle.setText("Edit Task");
            if(TimeText != null) BSReminderText.setText(new DateAndTime(TimeText).getTimeFromTime());
            else BSReminderText.setText("Set Reminder");
            tableAddEdit.show();
        }
    }

    @Override
    public void onLongClick(View view, String Id, byte IF_ID) {
        if(IF_ID == 0){
            CreateLayout layout = new CreateLayout(this,R.layout.yes_no_dialog);
            Dialog deleteDialog = layout.getDialog();

            TextView Title, Text, YesBtnText,NoBtnText;
            MaterialCardView YesBtn,NoBtn;

            Title = deleteDialog.findViewById(R.id.Title);
            Text = deleteDialog.findViewById(R.id.Text);
            NoBtnText = deleteDialog.findViewById(R.id.noBtnText);
            YesBtnText = deleteDialog.findViewById(R.id.yesBtnText);
            NoBtn = deleteDialog.findViewById(R.id.noBtn);
            YesBtn = deleteDialog.findViewById(R.id.yesBtn);

            Title.setText("Delete Task");
            Text.setText("Do you want to delete task?");
            NoBtnText.setText("Cancel");
            YesBtnText.setText("Delete");

            NoBtn.setOnClickListener(view1 -> {
                deleteDialog.dismiss();
            });

            YesBtn.setOnClickListener(view1 -> {
                Cursor row = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_TABLE +" Where Id = "+Id);
                row.moveToNext();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    new ReminderHelper(this).dismissNotification(Integer.valueOf(row.getString(Table.R_ID.getValue())));
                    new ReminderHelper(this).cancelScheduleNotification(Integer.parseInt(row.getString(Table.R_ID.getValue())),row.getString(Table.TITLE.getValue()));
                }
                sqLiteDBManager.delete(SQLiteDBHelper.TR_NOTIFY, "Id = " + row.getString(Table.R_ID.getValue()));
                sqLiteDBManager.delete(SQLiteDBHelper.TR_TABLE,"Id = "+Id);
                Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show();
                getTableView();
                isChanged = true;
                deleteDialog.dismiss();
            });
            deleteDialog.show();
        }
    }


    @SuppressLint({"SetTextI18n", "ResourceAsColor", "SimpleDateFormat"})
    private void createBottomSheet() {

        createLayout = new CreateLayout(this,R.layout.table_add_edit_design);

        tableAddEdit = createLayout.getDialog();


        BSTitle = tableAddEdit.findViewById(R.id.Title);
        BSReminderText = tableAddEdit.findViewById(R.id.setReminderText);
        BSBtbText = tableAddEdit.findViewById(R.id.saveBtnText);
        BSSaveBtn = tableAddEdit.findViewById(R.id.btnSave);
        BSReminderBtn = tableAddEdit.findViewById(R.id.setReminderBtn);
        BSText = tableAddEdit.findViewById(R.id.TextBox);

        if(TimeText != null) BSReminderText.setText(new DateAndTime(TimeText).getTimeFromTime());
        else BSReminderText.setText("Set Reminder");


        BSReminderBtn.setOnClickListener(view -> {
            setDateTimeDialog();
        });


        BSSaveBtn.setOnClickListener(view -> {
            if (BSText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Enter text!", Toast.LENGTH_SHORT).show();
            }else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues tableData = new ContentValues();
                ContentValues notifyData = new ContentValues();
                if(TampId == null){

                    notifyData.put("Type", 1);
                    notifyData.put("RTime", TimeText);
                    sqLiteDBManager.insert(SQLiteDBHelper.TR_NOTIFY, notifyData);

                    Cursor TaskNotify = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_NOTIFY +" ORDER BY Id DESC LIMIT 1");
                    TaskNotify.moveToNext();

                    tableData.put("Title", BSText.getText().toString().trim());
                    tableData.put("R_Id", TaskNotify.getString(Notify.ID.getValue()));
                    tableData.put("Day", DayId);
                    tableData.put("UDateTime", simpleDateFormat.format(new Date()));
                    sqLiteDBManager.insert(SQLiteDBHelper.TR_TABLE, tableData);
                    Calendar dateTime = Calendar.getInstance();
                    if (TimeText != null) {
                        try {
                            Date dDate;
                            int i=0;
                            for (i=0; i<=6; i++){
                                Calendar calendar = new GregorianCalendar();
                                calendar.add(Calendar.DAY_OF_MONTH,i);
                                if(new SimpleDateFormat("EEEE").format(calendar.getTime()).equals(MainList.get(DayId))){
                                    break;
                                }
                            }

                            Calendar calendar = new GregorianCalendar();
                            calendar.add(Calendar.DAY_OF_MONTH,i);

                            dDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse((new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()) + " " + TimeText));
                            assert dDate != null;
                            dateTime.setTime(dDate);
                            dateTime.set(Calendar.SECOND, 0);
                            dateTime.set(Calendar.MILLISECOND, 0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                new ReminderHelper(this).scheduleNotification(Integer.parseInt(TaskNotify.getString(Notify.ID.getValue())), 1, "Task Reminder", BSText.getText().toString(), dateTime.getTimeInMillis());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    Cursor row = sqLiteDBManager.fetch("Select * From "+SQLiteDBHelper.TR_TASK+" Where Id = "+TampId);
                    row.moveToNext();
                    notifyData.put("RTime",TimeText);

                    tableData.put("Title", BSText.getText().toString().trim());
                    tableData.put("UDateTime", simpleDateFormat.format(new Date()));

                    sqLiteDBManager.update(tableData,SQLiteDBHelper.TR_TABLE, "Id = '"+TampId+"' ");
                    sqLiteDBManager.update(notifyData, SQLiteDBHelper.TR_NOTIFY, "Id = " + row.getString(Task.R_ID.getValue()));
                    Calendar dateTime = Calendar.getInstance();
                    if (TimeText != null) {
                        try {
                            Date dDate;
                            int i=0;
                            for (i=0; i<=6; i++){
                                Calendar calendar = new GregorianCalendar();
                                calendar.add(Calendar.DAY_OF_MONTH,i);
                                if(new SimpleDateFormat("EEEE").format(calendar.getTime()).equals(MainList.get(DayId))){
                                    break;
                                }
                            }

                            Calendar calendar = new GregorianCalendar();
                            calendar.add(Calendar.DAY_OF_MONTH,i);

                            dDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse((new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()) + " " + TimeText));
                            assert dDate != null;
                            dateTime.setTime(dDate);
                            dateTime.set(Calendar.SECOND, 0);
                            dateTime.set(Calendar.MILLISECOND, 0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                new ReminderHelper(this).cancelScheduleNotification(Integer.parseInt(row.getString(Task.R_ID.getValue())),BSText.getText().toString());
                                new ReminderHelper(this).scheduleNotification(Integer.parseInt(row.getString(Task.R_ID.getValue())), 1, "Task Reminder", BSText.getText().toString(), dateTime.getTimeInMillis());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                TampId = null;
                isChanged = true;
                getTableView();
                tableAddEdit.dismiss();

            }
        });
        BSText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSubmitBtn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    void setSubmitBtn(){
        if(!BSText.getText().toString().trim().equals("") && TimeText != null){
            BSSaveBtn.setBackgroundTintList(valueOf(getResources().getColor(R.color.AppColor)));
            BSBtbText.setTextColor(getResources().getColor(R.color.colorWhite));
            BSSaveBtn.setClickable(true);
        }else{
            BSSaveBtn.setBackgroundTintList(valueOf(getResources().getColor(R.color.colorWhite_300)));
            BSBtbText.setTextColor(getResources().getColor(R.color.colorBlack));
            BSSaveBtn.setClickable(false);
        }
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n", "ResourceAsColor"})
    private void setDateTimeDialog(){
        CreateLayout dateTime = new CreateLayout(this,R.layout.select_date_and_time);
        setDateTime = dateTime.getDialog();
        AtomicReference<String> TempTime = new AtomicReference<>("");

        TextView showData,removeBtnText,saveBtnText;
        MaterialCardView removeBtn, btnSave;
        DatePicker selectDate;
        TimePicker selectTime;
        AtomicBoolean layout = new AtomicBoolean(false);

        showData = setDateTime.findViewById(R.id.showData);
        removeBtnText = setDateTime.findViewById(R.id.removeBtnText);
        saveBtnText = setDateTime.findViewById(R.id.saveBtnText);
        removeBtn = setDateTime.findViewById(R.id.removeBtn);
        btnSave = setDateTime.findViewById(R.id.btnSave);
        selectDate = setDateTime.findViewById(R.id.selectDate);
        selectTime = setDateTime.findViewById(R.id.selectTime);

        selectDate.setVisibility(View.GONE);
        selectTime.setVisibility(View.VISIBLE);
        removeBtn.setVisibility(View.GONE);
        saveBtnText.setText("Set Reminder");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (TimeText != null) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatD = new SimpleDateFormat("HH:mm");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat hTimeD, mTimeD;
                hTimeD = new SimpleDateFormat("HH");
                mTimeD = new SimpleDateFormat("mm");
                try {
                    Date DateD = dateFormatD.parse(TimeText);
                    assert DateD != null;
                    selectTime.setHour(Integer.parseInt(hTimeD.format(DateD)));
                    selectTime.setMinute(Integer.parseInt(mTimeD.format(DateD)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }



        // set Date

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showData.setText(new DateAndTime(selectTime.getHour()+":"+selectTime.getMinute()).getTimeFromTime());
        }

        setDateTime.show();

        // -----------------------------  Button -----------------------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            selectTime.setOnTimeChangedListener((timePicker, i, i1) -> {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat hTime, mTime;
                hTime = new SimpleDateFormat("HH");
                mTime = new SimpleDateFormat("mm");
                try {
                    Date sTime = dateFormat.parse(selectTime.getHour() + ":" + selectTime.getMinute());

                    assert sTime != null;
                    showData.setText(new DateAndTime(hTime.format(sTime) + ":" + mTime.format(sTime)).getTimeFromTime());
                    btnSave.setBackgroundTintList(valueOf(getResources().getColor(R.color.AppColor)));
                    btnSave.setStrokeColor(getResources().getColor(R.color.AppColor));
                    saveBtnText.setTextColor(getResources().getColor(R.color.colorWhite));
                    btnSave.setClickable(true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnSave.setOnClickListener(view -> {
                TempTime.set(selectTime.getHour() + ":" + selectTime.getMinute());
                TimeText = TempTime.get();
                BSReminderText.setText(new DateAndTime(TempTime.get()).getTimeFromTime());
                setSubmitBtn();
                setDateTime.dismiss();
            });
        }

    }

}