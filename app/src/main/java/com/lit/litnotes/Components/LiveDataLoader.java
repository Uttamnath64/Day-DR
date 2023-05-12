package com.lit.litnotes.Components;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveDataLoader extends ViewModel {
    private MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Integer> getCode(){
        return mutableLiveData;
    }

    public void setCode(Integer code){
        mutableLiveData.setValue(code);
    }

    public void deleteCode(){
        mutableLiveData = new MutableLiveData<>();
    }
}
