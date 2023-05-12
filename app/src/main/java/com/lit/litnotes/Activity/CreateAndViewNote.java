package com.lit.litnotes.Activity;

import static android.content.res.ColorStateList.valueOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lit.litnotes.Adapters.ColorAdapter;
import com.lit.litnotes.Components.ColorManager;
import com.lit.litnotes.Components.CreateLayout;
import com.lit.litnotes.Components.DateAndTime;
import com.lit.litnotes.Database.Note;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Components.LiveDataLoader;
import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Model.ColorModel;
import com.lit.litnotes.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CreateAndViewNote extends AppCompatActivity implements ItemClickListener {

    private ImageView btnBack,colorBtn;
    private CardView btnSave;
    private TextView saveBtnText;
    private EditText textTitle,textText;
    private SQLiteDBManager sqLiteDBManager;
    private SQLiteDBHelper sqLiteDBHelper;
    ConstraintLayout relativeLayout;
    private boolean view = false;
    Intent intent;
    String Color_Id = "0";

    String dataTitle,dataText,dataId = "",dataColor;
    LiveDataLoader liveDataLoader;
    Dialog colorDialog;
    ArrayList<ColorModel> list;
    String DateTime = null;


    TextView NoteData;
    CardView BSCardView;
    RecyclerView BSRecyclerView;

    byte SELECT_COLOR = -1;
    ColorManager colorManager;

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_and_view_note);

        btnBack = findViewById(R.id.btnBack);
        colorBtn = findViewById(R.id.colorBtn);
        btnSave = findViewById(R.id.btnSave);
        textTitle = findViewById(R.id.textTitle);
        textText = findViewById(R.id.textText);
        saveBtnText = findViewById(R.id.saveBtnText);
        NoteData = findViewById(R.id.noteData);
        relativeLayout = findViewById(R.id.relativeLayout);
        sqLiteDBManager = new SQLiteDBManager(CreateAndViewNote.this);
        sqLiteDBManager.open();
        sqLiteDBHelper =new SQLiteDBHelper(CreateAndViewNote.this);

        intent = getIntent();
        dataId = intent.getStringExtra("Id");

        colorManager = new ColorManager();


        liveDataLoader = new ViewModelProvider(this).get(LiveDataLoader.class);


        if(dataId.trim().equals("")){
            view = false;
        }else{
            Cursor row = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_NOTE +" Where id = "+dataId);
            if(row.moveToNext()){
                dataTitle = row.getString(Note.TITLE.getValue());
                dataText = row.getString(Note.DESCRIPTION.getValue());
                Color_Id = row.getString(Note.COLOR_ID.getValue());
                DateTime = new DateAndTime(row.getString(Note.U_DATE_TIME.getValue())).getDateTimeFromTS();
                dataColor = Color_Id;
                textTitle.setText(dataTitle);
                textText.setText(dataText);
            }
            view  = true;
        }


        textTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveButtonActive();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        textText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setNoteData();
                saveButtonActive();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnBack.setOnClickListener(View->{
            onBackPressed();
        });

        btnSave.setOnClickListener(View->{
            if(validation()){
                String title = textTitle.getText().toString().trim();
                String text = textText.getText().toString();
                long time = System.currentTimeMillis()/1000;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Intent intent2 = new Intent();
                if(!view){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Title",title);
                    contentValues.put("Description",text);
                    contentValues.put("Color_Id",Integer.parseInt(Color_Id));
                    if(intent.getStringExtra("List_Id") != null){
                        contentValues.put("List_Id",intent.getStringExtra("List_Id"));
                    }
                    contentValues.put("UDateTime", simpleDateFormat.format(new Date()));
                    sqLiteDBManager.insert(SQLiteDBHelper.TR_NOTE,contentValues);
                    intent2.putExtra(HomeScreen.DATA_CODE,"102");
                    liveDataLoader.setCode(102);
                    Toast.makeText(CreateAndViewNote.this,"Note Created!",Toast.LENGTH_SHORT).show();
                }else{
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Title",title);
                    contentValues.put("Description",text);
                    if(intent.getStringExtra("List_Id") != null ){
                        contentValues.put("List_Id",intent.getStringExtra("List_Id"));
                    }
                    contentValues.put("UDateTime", simpleDateFormat.format(new Date()));
                    sqLiteDBManager.update(contentValues,SQLiteDBHelper.TR_NOTE,"Id = "+dataId);
                    intent2.putExtra(HomeScreen.DATA_CODE,"102");
                    liveDataLoader.setCode(102);
                    Toast.makeText(CreateAndViewNote.this,"Note Saved!",Toast.LENGTH_SHORT).show();
                }

                setResult(RESULT_OK,intent2);
                onBackPressed();
            }
        });

        colorBtn.setOnClickListener(view1 -> {
            colorDialog.show();
        });


        setTopBarColor();
        setNoteData();
        saveButtonActive();
        colorLayout();

    }

    void colorLayout(){
        CreateLayout createLayout = new CreateLayout(this,R.layout.color_picker_note);
        colorDialog = createLayout.getDialog();


        BSCardView = colorDialog.findViewById(R.id.cardView);
        BSRecyclerView = colorDialog.findViewById(R.id.recyclerView);

        BSRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        list = new ArrayList<>();

        for (int i = 0; i<=colorManager.getSize()-1; i++){
            list.add(new ColorModel(String.valueOf(i),colorManager.getColor(i),(Integer.parseInt(Color_Id) == i)));
        }

        ColorAdapter colorAdapter = new ColorAdapter(list,this,this);
        BSRecyclerView.setAdapter(colorAdapter);

        colorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setTopBarColor();
            }
        });

    }

    void setTopBarColor(){
        getWindow().setStatusBarColor(this.getResources().getColor(colorManager.getColor(Integer.parseInt(Color_Id))));
        relativeLayout.setBackgroundColor(this.getResources().getColor(colorManager.getColor(Integer.parseInt(Color_Id))));
    }
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void setNoteData() {
        if(DateTime == null) DateTime = new SimpleDateFormat("MMM dd, yyyy h:mm a").format(new Date());
        String car = textText.getText().toString().length() == 1 ? " character" : " characters";
        NoteData.setText(DateTime+" | "+textText.getText().toString().length()+car);
    }

    boolean validation(){
        if(view){
            return ((!textTitle.getText().toString().trim().isEmpty() && !textText.getText().toString().trim().isEmpty()) &&
                    (!textTitle.getText().toString().trim().equals(dataTitle) || !textText.getText().toString().equals(dataText)));
        }else{
            return !(textTitle.getText().toString().trim().isEmpty()) && !(textText.getText().toString().trim().isEmpty());
        }
    }

    void saveButtonActive(){
        if(validation()){
            btnSave.setBackgroundTintList(valueOf(this.getResources().getColor(R.color.AppColor)));
            saveBtnText.setTextColor(this.getResources().getColor(R.color.colorWhite));
            btnSave.setClickable(true);
        }else{
            btnSave.setBackgroundTintList(valueOf(this.getResources().getColor(R.color.colorWhite_300)));
            saveBtnText.setTextColor(this.getResources().getColor(R.color.colorBlack));

            btnSave.setClickable(false);
        }
    }

    @Override
    public void onClick(View view1, String Id, byte IF_ID) {
        if(IF_ID == 10){
            Color_Id = Id;
            if(view){
                Intent intent2 = new Intent();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues contentValues = new ContentValues();
                contentValues.put("Color_Id",Integer.parseInt(Color_Id));
                contentValues.put("UDateTime", simpleDateFormat.format(new Date()));
                sqLiteDBManager.update(contentValues,SQLiteDBHelper.TR_NOTE,"Id = "+dataId);
                intent2.putExtra(HomeScreen.DATA_CODE,"102");
                setResult(RESULT_OK,intent2);
                setTopBarColor();
            }
        }
    }

    @Override
    public void onLongClick(View view, String Id, byte IF_ID) {

    }
}