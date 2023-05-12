package com.lit.litnotes.Interface;

import android.view.View;

public interface ItemClickListener {
    void onClick(View view, String Id, byte IF_ID);
    void onLongClick(View view, String Id, byte IF_ID);
}
