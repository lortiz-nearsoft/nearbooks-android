package com.nearsoft.nearbooks.view.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.modules.BaseActivityModule;

/**
 * Base activity.
 * Created by epool on 11/17/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ViewDataBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutResourceId = getLayoutResourceId();

        mBinding = DataBindingUtil.setContentView(this, layoutResourceId);

        Toolbar toolbar = (Toolbar) findViewById(getToolbarId());
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected abstract int getLayoutResourceId();

    protected int getToolbarId() {
        return R.id.toolbar;
    }

    protected <T extends ViewDataBinding> T getBinding(Class<T> clazz) {
        return clazz.cast(mBinding);
    }

    protected NearbooksApplicationComponent getNearbooksApplicationComponent() {
        return ((NearbooksApplication) getApplication()).getNearbooksApplicationComponent();
    }

    protected BaseActivityModule getBaseActivityModule() {
        return new BaseActivityModule(this);
    }

}
