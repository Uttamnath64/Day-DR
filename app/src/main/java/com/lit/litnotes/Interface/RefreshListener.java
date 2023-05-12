package com.lit.litnotes.Interface;

import java.io.Serializable;

public interface RefreshListener extends Serializable {
    void refresh(byte IF_ID);
}
