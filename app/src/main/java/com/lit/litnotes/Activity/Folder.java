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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.lit.litnotes.Adapters.FolderAdapter;
import com.lit.litnotes.Components.CreateLayout;
import com.lit.litnotes.Database.List;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Model.FolderModel;
import com.lit.litnotes.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Folder extends AppCompatActivity implements ItemClickListener {

    RecyclerView recyclerView;
    SQLiteDBHelper sqLiteDBHelper;
    SQLiteDBManager sqLiteDBManager;
    ArrayList<FolderModel> list;
    FolderAdapter folderAdapter;
    ImageView backBtn;

    CardView createBtn;
    Dialog dialog;

    EditText BSText;
    TextView BSTitle;
    CardView BSSaveBtn;
    TextView BSBtbText;

    ConstraintLayout alertLayout;
    TextView alertText;
    ImageView alertImage;
    Intent intent;


    String TampId = null;
    String DataText = null;

    int sizeOfData = 0;
    boolean isChanged = false;


    @Override
    protected void onResume() {
        super.onResume();
        getFolder();
    }

    @Override
    public void onBackPressed() {
        if(isChanged){
            intent.putExtra(HomeScreen.DATA_CODE,"101");
            setResult(RESULT_OK,intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        recyclerView = findViewById(R.id.recyclerView);
        createBtn = findViewById(R.id.createBtn);


        // alert message
        alertLayout = findViewById(R.id.alertLayout);
        alertText = findViewById(R.id.alertText);
        alertImage = findViewById(R.id.alertImage);
        backBtn = findViewById(R.id.backBtn);

        sqLiteDBHelper = new SQLiteDBHelper(this);
        sqLiteDBManager = new SQLiteDBManager(this);
        sqLiteDBManager.open();

        intent = new Intent();


        recyclerView.setLayoutManager(new LinearLayoutManager(Folder.this,LinearLayoutManager.VERTICAL,false));

        getFolder();
        createBottomSheet();

        createBtn.setOnClickListener(View->{
            TampId = null;
            DataText = null;
            BSTitle.setText("Create Folder");
            BSText.setText("");
            dialog.show();
        });

        backBtn.setOnClickListener(View->{
            onBackPressed();
        });

    }

    private void createBottomSheet() {
        dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.folder_add_edit_bottom_sheet);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        BSTitle = dialog.findViewById(R.id.Title);
        BSText = dialog.findViewById(R.id.TextBox);
        BSSaveBtn = dialog.findViewById(R.id.btnSave);
        BSBtbText = dialog.findViewById(R.id.saveBtnText);

        BSSaveBtn.setOnClickListener(view -> {
            if (BSText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Enter text!", Toast.LENGTH_SHORT).show();
            }else {
                if (TampId == null) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Title", BSText.getText().toString().trim());
                    contentValues.put("UDateTime", simpleDateFormat.format(new Date()));
                    sqLiteDBManager.insert(SQLiteDBHelper.TR_LIST, contentValues);
                    Toast.makeText(this, "Folder Created!", Toast.LENGTH_SHORT).show();
                } else {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Title", BSText.getText().toString().trim());
                    contentValues.put("UDateTime", simpleDateFormat.format(new Date()));
                    sqLiteDBManager.update(contentValues, SQLiteDBHelper.TR_LIST, "Id = " + TampId);
                    Toast.makeText(this, "Folder Renamed!", Toast.LENGTH_SHORT).show();
                }
                isChanged = true;
                TampId = null;
                dialog.dismiss();
                getFolder();
            }
        });

        BSText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TampId != null){
                    if(!BSText.getText().toString().trim().equals("") && !DataText.trim().equals(BSText.getText().toString().trim())){
                        BSSaveBtn.setBackgroundTintList(valueOf(getResources().getColor(R.color.AppColor)));
                        BSBtbText.setTextColor(getResources().getColor(R.color.colorWhite));
                        BSSaveBtn.setClickable(true);
                    }else{
                        BSSaveBtn.setBackgroundTintList(valueOf(getResources().getColor(R.color.colorWhite_300)));
                        BSBtbText.setTextColor(getResources().getColor(R.color.colorBlack));
                        BSSaveBtn.setClickable(false);
                    }
                }else{
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
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void getFolder() {
        list = new ArrayList<>();
        Cursor data = sqLiteDBManager.fetch("Select * From "+ SQLiteDBHelper.TR_LIST+" ORDER BY UDateTime DESC ");
        while (data.moveToNext()){
            Cursor notes = sqLiteDBManager.fetch("Select count(*) From "+ SQLiteDBHelper.TR_NOTE +" WHERE List_Id = "+data.getString(0));
            notes.moveToFirst();
            list.add(new FolderModel(data.getString(List.ID.getValue()),data.getString(List.TITLE.getValue()),notes.getString(0)));
        }

        folderAdapter = new FolderAdapter(list,this,this);
        recyclerView.setAdapter(folderAdapter);

        sizeOfData = list.size();

        if(sizeOfData <= 0){
            alertLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            alertImage.setImageResource(R.drawable.folder);
            alertText.setText("No folder here yet");
        }else{
            alertLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onClick(View view, String Id, byte IF_ID) {
        if(IF_ID == 1){
            BSText.setText("");
            TampId = Id;
            Cursor row = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_LIST +" Where Id = "+TampId);
            if(row.moveToNext()){
                DataText = row.getString(List.TITLE.getValue());
                BSText.setText(row.getString(List.TITLE.getValue()));
            }
            BSTitle.setText("Rename Folder");
            dialog.show();
        }
    }

    @Override
    public void onLongClick(View view, String Id, byte IF_ID) {
        if (IF_ID == 1){
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

            Title.setText("Delete Folder");
            Text.setText("Do you want to delete folder?");
            NoBtnText.setText("Cancel");
            YesBtnText.setText("Delete");

            NoBtn.setOnClickListener(view1 -> {
                deleteDialog.dismiss();
            });

            YesBtn.setOnClickListener(view1 -> {
                sqLiteDBManager.delete(SQLiteDBHelper.TR_LIST,"Id = "+TampId);
                sqLiteDBManager.delete(SQLiteDBHelper.TR_NOTE,"List_Id = "+TampId);
                TampId = null;
                isChanged = true;
                deleteDialog.dismiss();
                getFolder();
            });
            deleteDialog.show();
        }
    }
}