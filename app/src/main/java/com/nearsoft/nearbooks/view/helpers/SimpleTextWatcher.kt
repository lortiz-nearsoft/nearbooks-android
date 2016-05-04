package com.nearsoft.nearbooks.view.helpers

import android.text.Editable
import android.text.TextWatcher

/**
 * Simple text watcher.
 * Created by epool on 2/4/16.
 */
abstract class SimpleTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable) {
    }

}
