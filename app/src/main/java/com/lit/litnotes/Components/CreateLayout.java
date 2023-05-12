package com.lit.litnotes.Components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

public class CreateLayout {

    private final Context context;
    private final Dialog dialog;

    public CreateLayout(Context context,int layout){
        this.context = context;

        dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public Dialog getDialog(){
        return dialog;
    }

    public void setCancelable(boolean b){
        dialog.setCancelable(b);
    }

    public void setCanceledOnTouchOutside(boolean b){
        dialog.setCanceledOnTouchOutside(b);
    }
}
