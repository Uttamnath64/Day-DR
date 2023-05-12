package com.lit.litnotes.Fragment;

import static android.content.res.ColorStateList.valueOf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.lit.litnotes.Adapters.TasksAdapter;
import com.lit.litnotes.Components.CreateLayout;
import com.lit.litnotes.Components.DateAndTime;
import com.lit.litnotes.Components.ReminderHelper;
import com.lit.litnotes.Database.Notify;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Database.Table;
import com.lit.litnotes.Database.Task;
import com.lit.litnotes.Interface.CheckItem;
import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Components.LiveDataLoader;
import com.lit.litnotes.Model.TasksModel;
import com.lit.litnotes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TasksFragment extends Fragment implements CheckItem,ItemClickListener {

    RecyclerView recyclerView;
    SQLiteDBHelper sqLiteDBHelper;
    SQLiteDBManager sqLiteDBManager;
    ArrayList<TasksModel> list;
    EditText searchBox;
    TasksAdapter tasksAdapter;
    ConstraintLayout alertLayout,dataLayout;
    TextView alertText;
    ImageView alertImage;
    int sizeOfData = 0;
    LiveDataLoader liveDataLoader;

    CreateLayout createLayout;
    Dialog taskAddEdit;
    TextView BSTitle,BSReminderText,BSBtbText;
    CardView BSSaveBtn,BSReminderBtn;
    EditText BSText;
    CheckBox BSRepeat;

    Dialog setDateTime;

    String TampId = null;
    AtomicInteger Repeat = new AtomicInteger(0);
    String DateText = null;
    String TimeText = null;
    String DBDate = null;
    String DBTime = null;
    String DBText = null;
    int DBRepeat = 0;

    @Override
    public void onResume() {
        super.onResume();
        getDataFromDatabase("");
    }

    private void getDataFromDatabase(String query ) {
        list = new ArrayList<TasksModel>();
        Cursor data;
        if (query.trim().equals("")){
            data = sqLiteDBManager.fetch("SELECT * FROM "+ SQLiteDBHelper.TR_TASK+" ORDER BY UDateTime DESC");
        }else{
            data = sqLiteDBManager.fetch("SELECT * FROM "+ SQLiteDBHelper.TR_TASK+" WHERE Text Like '%"+query+"%' ORDER BY UDateTime DESC ");
        }
        while (data.moveToNext()){
            Cursor notify = sqLiteDBManager.fetch("Select * from "+SQLiteDBHelper.TR_NOTIFY+" Where Id = '"+ data.getString(Task.R_ID.getValue())+"' ");
            notify.moveToNext();
            list.add(new TasksModel(
                    data.getString(Task.ID.getValue()),
                    data.getString(Task.TEXT.getValue()),
                    notify.getString(Notify.R_DATE.getValue()),
                    notify.getString(Notify.R_TIME.getValue()),
                    data.getString(Task.CHECKED.getValue()).trim().equals("1"),
                    data.getString(Task.U_DATE_TIME.getValue())
                    ));
        }

        tasksAdapter = new TasksAdapter(list,getContext(), (ItemClickListener) this, (CheckItem) this);
        recyclerView.setAdapter(tasksAdapter);
        sizeOfData = list.size();
        if(sizeOfData <= 0){
            alertLayout.setVisibility(View.VISIBLE);
            dataLayout.setVisibility(View.GONE);
            alertImage.setImageResource(R.drawable.task);
            alertText.setText("No task here yet");
        }else{
            alertLayout.setVisibility(View.GONE);
            dataLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint({"SimpleDateFormat", "ResourceAsColor", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchBox = view.findViewById(R.id.searchBox);
        alertLayout = view.findViewById(R.id.alertLayout);
        alertText = view.findViewById(R.id.alertText);
        dataLayout = view.findViewById(R.id.dataLayout);
        alertImage = view.findViewById(R.id.alertImage);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        sqLiteDBHelper = new SQLiteDBHelper(getContext());
        sqLiteDBManager = new SQLiteDBManager(getContext());
        sqLiteDBManager.open();
        list = new ArrayList<TasksModel>();

        getDataFromDatabase("");

        searchBox.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE){
                String data = searchBox.getText().toString().trim();
                if(data.isEmpty()) {
                    getDataFromDatabase("");
                }else{
                    getDataFromDatabase(searchBox.getText().toString());
                }
                return true;
            }
            return false;
        });


        liveDataLoader = new ViewModelProvider(requireActivity()).get(LiveDataLoader.class);
        liveDataLoader.getCode().observe(requireActivity(),item->{
            if(item == 201){
                getDataFromDatabase(searchBox.getText().toString().trim());
            }
        });

        createBottomSheet();

        return view;
    }


    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onClick(View view, String Id, byte IF_ID) {
        if(IF_ID == (byte)201){

            TampId = null;
            TimeText = null;
            Repeat.set(0);
            DateText = null;
            DBDate = null;
            DBTime = null;
            DBText = null;
            DBRepeat = 0;
            BSText.setText("");

            TampId = Id;
            BSTitle.setText("Edit Task");
            Cursor row = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_TASK +" Where Id = "+TampId);
            if(row.moveToNext()){
                Cursor notify = sqLiteDBManager.fetch("Select * from "+SQLiteDBHelper.TR_NOTIFY+" Where Id = '"+ row.getString(Task.R_ID.getValue())+"' ");
                notify.moveToNext();
                BSText.setText(row.getString(Task.TEXT.getValue()));
                DBText = row.getString(Task.TEXT.getValue());
                if(notify.getString(Notify.R_DATE.getValue()).trim().equals("")){
                    DateText = null;
                    DBDate = null;
                    Repeat.set(1);
                    DBRepeat = 1;
                }else{
                    DateText = notify.getString(Notify.R_DATE.getValue());
                    DBDate = notify.getString(Notify.R_DATE.getValue());
                    Repeat.set(0);
                    DBRepeat = 0;
                }
                if(!notify.getString(Notify.R_TIME.getValue()).trim().equals("")) TimeText = notify.getString(Notify.R_TIME.getValue()).trim();
                else TimeText = null;
                if(!notify.getString(Notify.R_TIME.getValue()).trim().equals("")) DBTime = notify.getString(Notify.R_TIME.getValue()).trim();
                else DBTime = null;
            }
            BSRepeat.setChecked((DateText == null && TimeText != null));

            if(TimeText != null){
                String temp;
                if(DateText != null) temp = DateText;
                else temp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                BSRepeat.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.AppColor)));
                BSRepeat.setTextColor(getContext().getResources().getColor(R.color.colorBlack));
                BSReminderText.setText(new DateAndTime(temp).getDateFromDate() + " " + new DateAndTime(TimeText).getTimeFromTime());
                BSRepeat.setClickable(true);
            }else{
                BSRepeat.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack_300)));
                BSRepeat.setTextColor(getContext().getResources().getColor(R.color.colorBlack_300));
                BSReminderText.setText("Set Reminder");
                BSRepeat.setClickable(false);
            }
            saveBtnActive();
            taskAddEdit.show();
        }
    }

    @Override
    public void onLongClick(View view, String Id, byte IF_ID) {
        if(IF_ID == 1){
            CreateLayout layout = new CreateLayout(getContext(),R.layout.yes_no_dialog);
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
                Cursor row = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_TASK +" Where Id = "+Id);
                row.moveToNext();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    new ReminderHelper(getContext()).dismissNotification(Integer.valueOf(row.getString(Task.R_ID.getValue())));
                    new ReminderHelper(getContext()).cancelScheduleNotification(Integer.parseInt(row.getString(Task.R_ID.getValue())),row.getString(Task.TEXT.getValue()));
                }
                sqLiteDBManager.delete(SQLiteDBHelper.TR_NOTIFY, "Id = " + row.getString(Task.R_ID.getValue()));
                sqLiteDBManager.delete(SQLiteDBHelper.TR_TASK,"Id = "+Id);
                Toast.makeText(getContext(), "Task deleted!", Toast.LENGTH_SHORT).show();
                getDataFromDatabase(searchBox.getText().toString().trim());
                deleteDialog.dismiss();
            });
            deleteDialog.show();
        }
    }

    @Override
    public void onChecked(boolean Checked, String Id, byte IF_ID) {
        if(IF_ID == (byte) 201){
            ContentValues contentValues = new ContentValues();
            contentValues.put("Checked", (Checked)?1:0 );
            sqLiteDBManager.update(contentValues, SQLiteDBHelper.TR_TASK, "Id = " + Id);
        }
    }


    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void createBottomSheet() {

        createLayout = new CreateLayout(getContext(),R.layout.task_add_edit_bottom_sheet);

        taskAddEdit = createLayout.getDialog();

        BSTitle = taskAddEdit.findViewById(R.id.Title);
        BSReminderText = taskAddEdit.findViewById(R.id.setReminderText);
        BSBtbText = taskAddEdit.findViewById(R.id.saveBtnText);
        BSSaveBtn = taskAddEdit.findViewById(R.id.btnSave);
        BSReminderBtn = taskAddEdit.findViewById(R.id.setReminderBtn);
        BSText = taskAddEdit.findViewById(R.id.TextBox);
        BSRepeat = taskAddEdit.findViewById(R.id.repeatCheck);




        BSRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Repeat.set(1);
                }else{
                    Repeat.set(0);
                }
                saveBtnActive();
            }
        });

        BSSaveBtn.setOnClickListener(view -> {
            if (BSText.getText().toString().trim().equals("")) {
                Toast.makeText(getContext(), "Enter text!", Toast.LENGTH_SHORT).show();
            }else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues taskData = new ContentValues();
                ContentValues notifyData = new ContentValues();
                if (TampId == null) {
                    Toast.makeText(getContext(), "Some think, want wrong!", Toast.LENGTH_SHORT).show();
                } else {
                    String vDate,vTime;
                    if( Repeat.get() == 0 && DateText == null && TimeText != null) vDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    else if(Repeat.get() == 1 || DateText == null) vDate = "";
                    else vDate = DateText;

                    if(TimeText == null) vTime = "";
                    else vTime = TimeText;
                    Cursor row = sqLiteDBManager.fetch("Select * From "+SQLiteDBHelper.TR_TASK+" Where Id = "+TampId);
                    row.moveToNext();

                    notifyData.put("RDate",vDate);
                    notifyData.put("RTime",vTime);

                    taskData.put("Text", BSText.getText().toString().trim());
                    taskData.put("UDateTime", simpleDateFormat.format(new Date()));

                    sqLiteDBManager.update(taskData, SQLiteDBHelper.TR_TASK, "Id = " + TampId);
                    sqLiteDBManager.update(notifyData, SQLiteDBHelper.TR_NOTIFY, "Id = " + row.getString(Task.R_ID.getValue()));
                    Calendar dateTime = Calendar.getInstance();
                    if (TimeText != null) {
                        try {
                            Date dDate;
                            if (vDate.trim().equals("")) {
                                vDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                            }
                            dDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(vDate + " "+ vTime);
                            assert dDate != null;
                            dateTime.setTime(dDate);
                            dateTime.set(Calendar.SECOND, 0);
                            dateTime.set(Calendar.MILLISECOND, 0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                new ReminderHelper(getContext()).cancelScheduleNotification(Integer.parseInt(row.getString(Task.R_ID.getValue())),BSText.getText().toString());
                                new ReminderHelper(getContext()).scheduleNotification(Integer.parseInt(row.getString(Task.R_ID.getValue())), 0, "Task Reminder", BSText.getText().toString(), dateTime.getTimeInMillis());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                liveDataLoader = new ViewModelProvider((ViewModelStoreOwner) requireActivity()).get(LiveDataLoader.class);
                liveDataLoader.setCode(201);
                TampId = null;
                taskAddEdit.dismiss();

            }
        });
        BSText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveBtnActive();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        BSReminderBtn.setOnClickListener(view -> {
            setDateTimeDialog();
        });


    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n", "ResourceAsColor"})
    private void setDateTimeDialog(){
        CreateLayout dateTime = new CreateLayout(getContext(),R.layout.select_date_and_time);
        setDateTime = dateTime.getDialog();

        TextView showData,removeBtnText,saveBtnText;
        MaterialCardView removeBtn, btnSave;
        DatePicker selectDate;
        TimePicker selectTime;
        AtomicBoolean layout = new AtomicBoolean(false);
        AtomicReference<String> TempDate = new AtomicReference<>("");
        AtomicReference<String> TempTime = new AtomicReference<>("");

        showData = setDateTime.findViewById(R.id.showData);
        removeBtnText = setDateTime.findViewById(R.id.removeBtnText);
        saveBtnText = setDateTime.findViewById(R.id.saveBtnText);
        removeBtn = setDateTime.findViewById(R.id.removeBtn);
        btnSave = setDateTime.findViewById(R.id.btnSave);
        selectDate = setDateTime.findViewById(R.id.selectDate);
        selectTime = setDateTime.findViewById(R.id.selectTime);

        if(DateText != null){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatD = new SimpleDateFormat("dd-MM-yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dDateD, mDateD, yDateD;
            dDateD = new SimpleDateFormat("dd");
            mDateD = new SimpleDateFormat("MM");
            yDateD = new SimpleDateFormat("yyyy");
            try {
                Date DateD = dateFormatD.parse(DateText);
                assert DateD != null;
                selectDate.init(
                        Integer.parseInt(yDateD.format(DateD)),
                        Integer.parseInt(mDateD.format(DateD))-1,
                        Integer.parseInt(dDateD.format(DateD)),
                        null);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



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

        //set Text
        if(TimeText != null) removeBtnText.setText("Remove");
        else removeBtnText.setText("Cancel");
        saveBtnText.setText("Next");

        // set Date
        showData.setText(new DateAndTime(selectDate.getDayOfMonth()+"-"+(selectDate.getMonth()+1)+"-"+selectDate.getYear()).getDateFromDate());

        setDateTime.show();

        // -----------------------------  Button -----------------------------


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectDate.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date cDate = dateFormat.parse(dateFormat.format(new Date()));
                        Date sDate = dateFormat.parse(selectDate.getDayOfMonth() + "-" + (selectDate.getMonth() + 1) + "-" + selectDate.getYear());

                        assert sDate != null;
                        showData.setText(new DateAndTime(selectDate.getDayOfMonth() + "-" + (selectDate.getMonth() + 1) + "-" + selectDate.getYear()).getDateFromDate());
                        if (sDate.compareTo(cDate) >= 0) {
                            btnSave.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.AppColor)));
                            btnSave.setStrokeColor(getContext().getResources().getColor(R.color.AppColor));
                            saveBtnText.setTextColor(getContext().getResources().getColor(R.color.colorWhite));
                            btnSave.setClickable(true);
                        } else {
                            btnSave.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorWhite_300)));
                            btnSave.setStrokeColor(getContext().getResources().getColor(R.color.colorWhite));
                            saveBtnText.setTextColor(getContext().getResources().getColor(R.color.colorBlack));
                            btnSave.setClickable(false);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            selectTime.setOnTimeChangedListener((timePicker, i, i1) -> {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatD = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date cTime = dateFormat.parse(dateFormat.format(new Date()));
                    Date sTime = dateFormat.parse(selectTime.getHour() + ":" + selectTime.getMinute());

                    Date cDate = dateFormatD.parse(dateFormatD.format(new Date()));
                    Date sDate = dateFormatD.parse(selectDate.getDayOfMonth() + "-" + (selectDate.getMonth() + 1) + "-" + selectDate.getYear());

                    assert sTime != null;
                    showData.setText(new DateAndTime(selectTime.getHour() + ":" + selectTime.getMinute()).getTimeFromTime());
                    assert sDate != null;
                    if ((sDate.compareTo(cDate) == 0 && sTime.compareTo(cTime) >= 0) || (sDate.compareTo(cDate) > 0)) {
                        btnSave.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.AppColor)));
                        btnSave.setStrokeColor(getContext().getResources().getColor(R.color.AppColor));
                        saveBtnText.setTextColor(getContext().getResources().getColor(R.color.colorWhite));
                        btnSave.setClickable(true);
                    } else {
                        btnSave.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorWhite_300)));
                        btnSave.setStrokeColor(getContext().getResources().getColor(R.color.colorWhite_300));
                        saveBtnText.setTextColor(getContext().getResources().getColor(R.color.colorBlack));
                        btnSave.setClickable(false);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnSave.setOnClickListener(view -> {
                if (layout.get()) {
                    TempTime.set(selectTime.getHour() + ":" + selectTime.getMinute());
                    DateText = TempDate.get();
                    TimeText = TempTime.get().trim();
                    BSRepeat.setButtonTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.AppColor)));
                    BSRepeat.setTextColor(getContext().getResources().getColor(R.color.colorBlack));
                    BSReminderText.setText(new DateAndTime(TempDate.get()).getDateFromDate() + " " + new DateAndTime(TempTime.get()).getTimeFromTime());
                    BSRepeat.setClickable(true);
                    saveBtnActive();
                    setDateTime.dismiss();
                } else {
                    TempDate.set(selectDate.getDayOfMonth() + "-" + (selectDate.getMonth() + 1) + "-" + selectDate.getYear());
                    showData.setText(new DateAndTime(selectTime.getHour() + ":" + selectTime.getMinute()).getTimeFromTime());
                    selectTime.setVisibility(View.VISIBLE);
                    selectDate.setVisibility(View.GONE);
                    removeBtnText.setText("Previous");
                    saveBtnText.setText("Set Reminder");
                    layout.set(true);
                }
            });
        }

        removeBtn.setOnClickListener(view -> {
            if(!layout.get()){
                DateText = null;
                TimeText = null;
                Repeat.set(0);
                BSRepeat.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack_300)));
                BSRepeat.setTextColor(getContext().getResources().getColor(R.color.colorBlack_300));
                BSReminderText.setText("Set Reminder");
                BSRepeat.setChecked(false);
                BSRepeat.setClickable(false);
                saveBtnActive();
                setDateTime.dismiss();
            }else{
                showData.setText(new DateAndTime(selectDate.getDayOfMonth()+"-"+(selectDate.getMonth()+1)+"-"+selectDate.getYear()).getDateFromDate());

                selectDate.setVisibility(View.VISIBLE);
                selectTime.setVisibility(View.GONE);
                if(TimeText == null){
                    removeBtnText.setText("Cancel");
                }else{
                    removeBtnText.setText("Remove");
                }
                saveBtnText.setText("Next");
                layout.set(false);

            }
        });
    }

    void saveBtnActive(){
        String dbDate,dbTime,vDate,vTime;
        if (DateText != null) vDate = DateText.trim();
        else vDate = "";
        if (TimeText != null) vTime = TimeText.trim();
        else vTime = "";
        if (DBDate != null) dbDate = DBDate.trim();
        else dbDate = "";
        if (DBTime != null) dbTime = DBTime.trim();
        else dbTime = "";

        if((!BSText.getText().toString().trim().equals("") && !BSText.getText().toString().trim().equals(DBText)) ||
                Repeat.get() != DBRepeat ||
                !dbDate.equals(vDate) ||
                !dbTime.equals(vTime)){
            BSSaveBtn.setBackgroundTintList(valueOf(getResources().getColor(R.color.AppColor)));
            BSBtbText.setTextColor(getResources().getColor(R.color.colorWhite));
            BSSaveBtn.setClickable(true);
        }else{
            BSSaveBtn.setBackgroundTintList(valueOf(getResources().getColor(R.color.colorWhite_300)));
            BSBtbText.setTextColor(getResources().getColor(R.color.colorBlack));
            BSSaveBtn.setClickable(false);
        }
    }
}