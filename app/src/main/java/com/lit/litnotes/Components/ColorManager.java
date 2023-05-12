package com.lit.litnotes.Components;

import android.graphics.Color;

import com.lit.litnotes.R;

public class ColorManager {
     int[] colors;
    public ColorManager() {
        colors = new int[]{
                R.color.CardColor1,
                R.color.CardColor2,
                R.color.CardColor3,
                R.color.CardColor4,
                R.color.CardColor5,
                R.color.CardColor6,
                R.color.CardColor7
        };
    }

    public int[] getColors(){
        return colors;
    }

    public int getColor(int index){
        return colors[index];
    }
    public int getSize(){
        return colors.length;
    }
}
