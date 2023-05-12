package com.lit.litnotes.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.lit.litnotes.Activity.CreateAndViewNote;
import com.lit.litnotes.Activity.Folder;
import com.lit.litnotes.Adapters.NoteTabAdapter;
import com.lit.litnotes.Adapters.NotesAdapter;
import com.lit.litnotes.Components.CreateLayout;
import com.lit.litnotes.Database.List;
import com.lit.litnotes.Database.Note;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Interface.ItemClickListener;
import com.lit.litnotes.Components.LiveDataLoader;
import com.lit.litnotes.Model.NoteTabModel;
import com.lit.litnotes.Model.NotesModel;
import com.lit.litnotes.R;

import java.util.ArrayList;
import java.util.Locale;


public class NotesFragment extends Fragment implements ItemClickListener {

    // list layout
    RecyclerView noteList,tabList;

    //database class
    SQLiteDBHelper sqLiteDBHelper;
    SQLiteDBManager sqLiteDBManager;

    //list
    ArrayList<NotesModel> NoteList;
    ArrayList<NoteTabModel> TabList;

    // for load note
    boolean fullLoad = true;

    // note elements
    CardView folderBtn;
    EditText searchBox;

    //adapter
    NotesAdapter notesAdapter;
    NoteTabAdapter noteTabAdapter;

    //for alert
    ConstraintLayout alertLayout;
    TextView alertText;
    ImageView alertImage;

    //size of notes
    int sizeOfData = 0;
    Cursor data;

    //set selected item in notes
    int TabIndex = 0;

    //add new element in folder
    CardView createBtn;

    //get live data from activity
    LiveDataLoader liveDataLoader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        noteList = view.findViewById(R.id.noteList);
        searchBox = view.findViewById(R.id.searchBox);
        tabList = view.findViewById(R.id.tabList);
        createBtn = view.findViewById(R.id.createBtn);
        alertLayout = view.findViewById(R.id.alertLayout);
        alertText = view.findViewById(R.id.alertText);
        alertImage = view.findViewById(R.id.alertImage);
        folderBtn = view.findViewById(R.id.folderBtn);
        tabList.setHasFixedSize(true);
        noteList.setHasFixedSize(true);

        // set layout
        tabList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        // database
        sqLiteDBHelper = new SQLiteDBHelper(getContext());
        sqLiteDBManager = new SQLiteDBManager(getContext());
        sqLiteDBManager.open();

        //add item in folder
        createBtn.setVisibility(View.GONE);

        // getData
        getTabList();
        getDataFromDatabase();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //folder btn
        folderBtn.setOnClickListener(View -> ((Activity)requireActivity()).startActivityForResult(new Intent(getContext(), Folder.class),1));

        //add new item in folder
        createBtn.setOnClickListener(View-> ((Activity)requireActivity()).startActivityForResult(new Intent(getContext(), CreateAndViewNote.class).putExtra("Id","").putExtra("List_Id",String.valueOf(TabIndex)),1, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),createBtn,"NoteView").toBundle()));

        // get live data
        liveDataLoader = new ViewModelProvider(requireActivity()).get(LiveDataLoader.class);
        liveDataLoader.getCode().observe(requireActivity(), item ->{
            if(item == 101){
                getTabList();
            }else if(item == 102){
                fullLoad = true;
            }
            getDataFromDatabase();
        });
        return view;
    }



    private Cursor getNotesData(){
            //data = sqLiteDBManager.fetch("SELECT * FROM "+ SQLiteDBHelper.TR_NOTE+" WHERE Title Like '%"+query+"%' OR Description Like '%"+query+"%' ORDER BY UDateTime DESC ");
        return sqLiteDBManager.fetch("SELECT * FROM "+ SQLiteDBHelper.TR_NOTE+" ORDER BY UDateTime DESC");
    }

    private void getDataFromDatabase() {
        NoteList = new ArrayList<>();
        if(fullLoad) {
            data = getNotesData();
            fullLoad = false;
        }

        data.moveToPosition(-1);
        if(TabIndex == 0){
            while (data.moveToNext()){
                NoteList.add(new NotesModel(data.getString(Note.ID.getValue()),data.getString(Note.TITLE.getValue()),data.getString(Note.DESCRIPTION.getValue()),Integer.parseInt(data.getString(Note.COLOR_ID.getValue())),data.getString(Note.U_DATE_TIME.getValue())));
            }
            createBtn.setVisibility(View.GONE);
        }else{
            while (data.moveToNext()){
                if(data.getString(1) != null && Integer.parseInt(data.getString(1)) == TabIndex){
                    NoteList.add(new NotesModel(data.getString(Note.ID.getValue()),data.getString(Note.TITLE.getValue()),data.getString(Note.DESCRIPTION.getValue()),Integer.parseInt(data.getString(Note.COLOR_ID.getValue())),data.getString(Note.U_DATE_TIME.getValue())));
                }
            }
            createBtn.setVisibility(View.VISIBLE);
        }


        notesAdapter = new NotesAdapter(NoteList,requireActivity(),this);
        noteList.setAdapter(notesAdapter);
        sizeOfData = NoteList.size();

        if(sizeOfData <= 0){
            alertLayout.setVisibility(View.VISIBLE);
            noteList.setVisibility(View.GONE);
            alertImage.setImageResource(R.drawable.notes);
            alertText.setText("No notes here yet");
        }else{
            alertLayout.setVisibility(View.GONE);
            noteList.setVisibility(View.VISIBLE);
        }
    }


    void getTabList(){
        TabList = new ArrayList<>();
        Cursor data = sqLiteDBManager.fetch("SELECT * FROM "+ SQLiteDBHelper.TR_LIST);

        boolean temp = true;
        while (data.moveToNext()){
            if(Integer.parseInt(data.getString(List.ID.getValue())) == TabIndex){
                temp = false;
            }
        }

        if(temp){
            TabIndex = 0;
            fullLoad = true;
        }

        TabList.add(new NoteTabModel("0","All",temp));
        data.moveToPosition(-1);

        while (data.moveToNext()){
            TabList.add(new NoteTabModel(data.getString(List.ID.getValue()),data.getString(List.TITLE.getValue()),(Integer.parseInt(data.getString(List.ID.getValue())) == TabIndex)));
        }

        noteTabAdapter = new NoteTabAdapter(TabList,getContext(),this);
        tabList.setAdapter(noteTabAdapter);
    }


    @Override
    public void onClick(View view, String Id, byte IF_ID) {
        if(IF_ID == 0){
            TabIndex = Integer.parseInt(Id);
            getDataFromDatabase();
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

            Title.setText("Delete Note");
            Text.setText("Do you want to delete note?");
            NoBtnText.setText("Cancel");
            YesBtnText.setText("Delete");

            NoBtn.setOnClickListener(view1 -> {
                deleteDialog.dismiss();
            });

            YesBtn.setOnClickListener(view1 -> {
                sqLiteDBManager.delete(SQLiteDBHelper.TR_NOTE,"Id = "+Id);
                Toast.makeText(getContext(), "Note deleted!", Toast.LENGTH_SHORT).show();
                fullLoad = true;
                TabIndex = 0;
                getDataFromDatabase();
                deleteDialog.dismiss();
            });
            deleteDialog.show();
        }
    }
}