package com.nearsoft.nearbooks.view.helpers;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Simple text watcher.
 * Created by epool on 2/4/16.
 */
public abstract class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

}
