package com.lit.litnotes.Activity;

import static android.content.res.ColorStateList.*;

import static androidx.core.app.ActivityOptionsCompat.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lit.litnotes.Adapters.HomeViewPageAdapter;
import com.lit.litnotes.Components.CreateLayout;
import com.lit.litnotes.Components.DateAndTime;
import com.lit.litnotes.Components.ReminderHelper;
import com.lit.litnotes.Database.Notify;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Interface.RefreshListener;
import com.lit.litnotes.Components.LiveDataLoader;
import com.lit.litnotes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class HomeScreen extends AppCompatActivity  {

    public static final String DATA_CODE = "DATA_CODE";
    Dialog dialog;

    ViewPager viewPager;
    int BPosition = 0;
    ImageView btnNotes,btnTasks,btnTable;
    HomeViewPageAdapter homeViewPageAdapter;
    FloatingActionButton CreateBtn;
    IntentFilter filter;
    public static RefreshListener refreshListener = null;
    String TampId = null;
    SQLiteDBHelper sqLiteDBHelper;
    SQLiteDBManager sqLiteDBManager;



    TextView BSTitle,BSReminderText,BSBtbText;
    CardView BSSaveBtn,BSReminderBtn;
    EditText BSText;
    CheckBox BSRepeat;
    AtomicInteger Repeat;
    String DateText = null;
    String TimeText = null;


    LiveDataLoader liveDataLoader;

    CreateLayout createLayout;
    Dialog taskAddEdit,setDateTime;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        viewPager = findViewById(R.id.viewPage);
        btnNotes = findViewById(R.id.btnNotes);
        btnTasks = findViewById(R.id.btnTasks);
        btnTable = findViewById(R.id.btnTable);
        CreateBtn = findViewById(R.id.createBtnFloating);
        filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

        sqLiteDBManager = new SQLiteDBManager(this);
        sqLiteDBHelper = new SQLiteDBHelper(this);
        sqLiteDBManager.open();

        homeViewPageAdapter = new HomeViewPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(homeViewPageAdapter);

        createBottomSheet();

        CreateBtn.setOnClickListener(View->{
            switch (BPosition){
                case 0:
                    try {
                        Intent i = new Intent(HomeScreen.this,
                                CreateAndViewNote.class)
                                .putExtra("Id","");
                        startActivityForResult(i,1,makeSceneTransitionAnimation(HomeScreen.this,CreateBtn,"CreateNewNote").toBundle());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    TampId = null;
                    BSTitle.setText("New Task");
                    BSText.setText("");
                    DateText = null;
                    TimeText = null;
                    Repeat.set(0);
                    BSReminderText.setText("Set Reminder");
                    BSRepeat.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack_300)));
                    BSRepeat.setChecked(false);
                    BSRepeat.setClickable(false);
                    BSRepeat.setTextColor(getResources().getColor(R.color.colorBlack_300));
                    taskAddEdit.show();
                    break;
                case 2:
                    break;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BPosition = position;
                switch (position){
                    case 0:
                        CreateBtn.setVisibility(View.VISIBLE);
                        setBtnColor(btnNotes);
                        break;
                    case 1:
                        CreateBtn.setVisibility(View.VISIBLE);
                        setBtnColor(btnTasks);
                        break;
                    case 2:
                        CreateBtn.setVisibility(View.GONE);
                        setBtnColor(btnTable);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnNotes.setOnClickListener(View->{
            if(viewPager.getCurrentItem() != 0){
                viewPager.setCurrentItem(0);
            }
        });

        btnTasks.setOnClickListener(View->{
            if(viewPager.getCurrentItem() != 1){
                viewPager.setCurrentItem(1);
            }
        });

        btnTable.setOnClickListener(View->{
            if(viewPager.getCurrentItem() != 2){
                viewPager.setCurrentItem(2);
            }
        });




    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "SimpleDateFormat"})
    private void createBottomSheet() {

        createLayout = new CreateLayout(this,R.layout.task_add_edit_bottom_sheet);

        taskAddEdit = createLayout.getDialog();

        Repeat = new AtomicInteger();

        BSTitle = taskAddEdit.findViewById(R.id.Title);
        BSReminderText = taskAddEdit.findViewById(R.id.setReminderText);
        BSBtbText = taskAddEdit.findViewById(R.id.saveBtnText);
        BSSaveBtn = taskAddEdit.findViewById(R.id.btnSave);
        BSReminderBtn = taskAddEdit.findViewById(R.id.setReminderBtn);
        BSText = taskAddEdit.findViewById(R.id.TextBox);
        BSRepeat = taskAddEdit.findViewById(R.id.repeatCheck);

        if(DateText == null && TimeText != null){
            BSRepeat.setChecked(true);
        }
        if(TimeText != null){
            BSRepeat.setButtonTintList(valueOf(getResources().getColor(R.color.AppColor)));
            BSRepeat.setTextColor(R.color.colorBlack);
            BSReminderText.setText(new DateAndTime(DateText).getDateFromDate() + " " + new DateAndTime(TimeText).getTimeFromTime());
            BSRepeat.setClickable(true);
        }else{
            BSRepeat.setButtonTintList(valueOf(getResources().getColor(R.color.colorBlack_300)));
            BSRepeat.setTextColor(R.color.colorBlack_300);
            BSReminderText.setText("Set Reminder");
            BSRepeat.setClickable(false);
        }

        DateText = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        BSReminderBtn.setOnClickListener(view -> {
            setDateTimeDialog();
        });

        BSRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Repeat.set(1);
                }else{
                    Repeat.set(0);
                }
            }
        });

        BSSaveBtn.setOnClickListener(view -> {
            if (BSText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Enter text!", Toast.LENGTH_SHORT).show();
            }else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues taskData = new ContentValues();
                ContentValues notifyData = new ContentValues();

                String vDate,vTime;
                if( Repeat.get() == 0 && DateText == null && TimeText != null) vDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                else if(Repeat.get() == 1 || DateText == null) vDate = "";
                else vDate = DateText;

                if(TimeText == null) vTime = "";
                else vTime = TimeText;


                notifyData.put("RDate", vDate);
                notifyData.put("Type", 0);
                notifyData.put("RTime", vTime);
                sqLiteDBManager.insert(SQLiteDBHelper.TR_NOTIFY, notifyData);

                Cursor TaskNotify = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_NOTIFY +" ORDER BY Id DESC LIMIT 1");
                TaskNotify.moveToNext();

                taskData.put("Text", BSText.getText().toString().trim());
                taskData.put("Checked", "0");
                taskData.put("R_Id", TaskNotify.getString(Notify.ID.getValue()));
                taskData.put("UDateTime", simpleDateFormat.format(new Date()));
                sqLiteDBManager.insert(SQLiteDBHelper.TR_TASK, taskData);
                Calendar dateTime = Calendar.getInstance();
                if (TimeText != null){
                    try {
                        Date dDate;
                        if (vDate.trim().equals("")) {
                            vDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                        }

                        dDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(vDate + " "+ vTime);//10-01-2023 13:33
                        assert dDate != null;
                        dateTime.setTime(dDate);
                        dateTime.set(Calendar.SECOND,0);
                        dateTime.set(Calendar.MILLISECOND,0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            new ReminderHelper(this).scheduleNotification(Integer.parseInt(TaskNotify.getString(Notify.ID.getValue())),0,"Task Reminder",BSText.getText().toString(),dateTime.getTimeInMillis());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                liveDataLoader = new ViewModelProvider(HomeScreen.this).get(LiveDataLoader.class);
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
                if(!BSText.getText().toString().trim().equals("")){
                    BSSaveBtn.setBackgroundTintList(valueOf(getResources().getColor(R.color.AppColor)));
                    BSBtbText.setTextColor(getResources().getColor(R.color.colorWhite));
                    BSSaveBtn.setClickable(true);
                }else{
                    BSSaveBtn.setBackgroundTintList(valueOf(getResources().getColor(R.color.colorWhite_300)));
                    BSBtbText.setTextColor(getResources().getColor(R.color.colorBlack));
                    BSSaveBtn.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }



    void setBtnColor(ImageView btn){
        btnNotes.setImageTintList(valueOf(this.getResources().getColor(R.color.colorBlack_600)));
        btnTasks.setImageTintList(valueOf(this.getResources().getColor(R.color.colorBlack_600)));
        btnTable.setImageTintList(valueOf(this.getResources().getColor(R.color.colorBlack_600)));
        btn.setImageTintList(valueOf(this.getResources().getColor(R.color.AppColor)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                if(data != null){
                    if(Integer.parseInt(data.getStringExtra(DATA_CODE)) == 101){
                        liveDataLoader = new ViewModelProvider(HomeScreen.this).get(LiveDataLoader.class);
                        liveDataLoader.setCode(101);
                    }else if(Integer.parseInt(data.getStringExtra(DATA_CODE)) == 102){
                        liveDataLoader = new ViewModelProvider(HomeScreen.this).get(LiveDataLoader.class);
                        liveDataLoader.setCode(102);
                    }else if(Integer.parseInt(data.getStringExtra(DATA_CODE)) == 301){
                        liveDataLoader = new ViewModelProvider(HomeScreen.this).get(LiveDataLoader.class);
                        liveDataLoader.setCode(301);
                    }
                }
            }
        }
    }


    @SuppressLint({"SimpleDateFormat", "SetTextI18n", "ResourceAsColor"})
    private void setDateTimeDialog(){
        CreateLayout dateTime = new CreateLayout(this,R.layout.select_date_and_time);
        setDateTime = dateTime.getDialog();
        AtomicReference<String> TempDate = new AtomicReference<>("");
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

        if(DateText != null){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatD = new SimpleDateFormat("dd-MM-yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dDateD, mDateD, yDateD;
            dDateD = new SimpleDateFormat("dd");
            mDateD = new SimpleDateFormat("MM");
            yDateD = new SimpleDateFormat("yyyy");
            try {
                Date DateD = dateFormatD.parse(DateText);
                assert DateD != null;
                selectDate.init(Integer.parseInt(yDateD.format(DateD)),Integer.parseInt(mDateD.format(DateD))-1,Integer.parseInt(dDateD.format(DateD)),null);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



        if(TimeText != null){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatD = new SimpleDateFormat("HH:mm");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat hTimeD,mTimeD;
            hTimeD = new SimpleDateFormat("HH");
            mTimeD = new SimpleDateFormat("mm");
            try {
                Date DateD = dateFormatD.parse(TimeText);
                assert DateD != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectTime.setHour(Integer.parseInt(hTimeD.format(DateD)));
                    selectTime.setMinute(Integer.parseInt(mTimeD.format(DateD)));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        //set Text
        if(TimeText != null) removeBtnText.setText("Remove");
        else removeBtnText.setText("Cancel");
        saveBtnText.setText("Next");

        // set Date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatT = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dDateT, mDateT, yDateT;
        dDateT = new SimpleDateFormat("dd");
        mDateT = new SimpleDateFormat("MMM");
        yDateT = new SimpleDateFormat("yyyy");
        try {
            Date dateT = dateFormatT.parse(selectDate.getDayOfMonth()+"-"+(selectDate.getMonth()+1)+"-"+selectDate.getYear());
            assert dateT != null;
            showData.setText(mDateT.format(dateT)+" "+dDateT.format(dateT)+", "+yDateT.format(dateT));
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                        Date sDate = dateFormat.parse(selectDate.getDayOfMonth()+"-"+(selectDate.getMonth()+1)+"-"+selectDate.getYear());

                        assert sDate != null;
                        showData.setText(new DateAndTime(selectDate.getDayOfMonth()+"-"+(selectDate.getMonth()+1)+"-"+selectDate.getYear()).getDateFromDate());
                        if(sDate.compareTo(cDate) >= 0){
                            btnSave.setBackgroundTintList(valueOf(getResources().getColor(R.color.AppColor)));
                            btnSave.setStrokeColor(getResources().getColor(R.color.AppColor));
                            saveBtnText.setTextColor(getResources().getColor(R.color.colorWhite));
                            btnSave.setClickable(true);
                        }else{
                            btnSave.setBackgroundTintList(valueOf(getResources().getColor(R.color.colorWhite_300)));
                            btnSave.setStrokeColor(getResources().getColor(R.color.colorWhite));
                            saveBtnText.setTextColor(getResources().getColor(R.color.colorBlack));
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
                        btnSave.setBackgroundTintList(valueOf(getResources().getColor(R.color.AppColor)));
                        btnSave.setStrokeColor(getResources().getColor(R.color.AppColor));
                        saveBtnText.setTextColor(getResources().getColor(R.color.colorWhite));
                        btnSave.setClickable(true);
                    } else {
                        btnSave.setBackgroundTintList(valueOf(getResources().getColor(R.color.colorWhite_300)));
                        btnSave.setStrokeColor(getResources().getColor(R.color.colorWhite_300));
                        saveBtnText.setTextColor(getResources().getColor(R.color.colorBlack));
                        btnSave.setClickable(false);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnSave.setOnClickListener(view -> {
                if(layout.get()){
                        TempTime.set(selectTime.getHour()+":"+selectTime.getMinute());

                    DateText = TempDate.get();
                    TimeText = TempTime.get();
                    BSRepeat.setButtonTintList(valueOf(getResources().getColor(R.color.AppColor)));
                    BSRepeat.setTextColor(getResources().getColor(R.color.colorBlack));
                    BSReminderText.setText(new DateAndTime(TempDate.get()).getDateFromDate() + " " + new DateAndTime(TempTime.get()).getTimeFromTime());
                    BSRepeat.setClickable(true);
                    setDateTime.dismiss();
                }else{
                    showData.setText(new DateAndTime(selectTime.getHour()+":"+selectTime.getMinute()).getTimeFromTime());
                    TempDate.set(selectDate.getDayOfMonth() + "-" + (selectDate.getMonth() + 1) + "-" + selectDate.getYear());
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
                BSRepeat.setButtonTintList(valueOf(getResources().getColor(R.color.colorBlack_300)));
                BSRepeat.setTextColor(getResources().getColor(R.color.colorBlack_300));
                BSReminderText.setText("Set Reminder");
                BSRepeat.setChecked(false);
                BSRepeat.setClickable(false);
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
}