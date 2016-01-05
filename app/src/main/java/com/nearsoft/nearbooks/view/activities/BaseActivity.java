package com.nearsoft.nearbooks.view.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.di.components.BaseActivityComponent;
import com.nearsoft.nearbooks.di.components.DaggerBaseActivityComponent;
import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.modules.BaseActivityModule;
import com.nearsoft.nearbooks.sync.SyncChangeHandler;

import javax.inject.Inject;

/**
 * Base activity.
 * Created by epool on 11/17/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    protected SyncChangeHandler mSyncChangeHandler;
    private ViewDataBinding mBinding;
    private BaseActivityComponent mBaseActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutResourceId = getLayoutResourceId();

        mBinding = DataBindingUtil.setContentView(this, layoutResourceId);

        Toolbar toolbar = (Toolbar) findViewById(getToolbarId());
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mBaseActivityComponent = DaggerBaseActivityComponent
                .builder()
                .nearbooksApplicationComponent(getNearbooksApplicationComponent())
                .baseActivityModule(new BaseActivityModule(this))
                .build();
        injectComponent(mBaseActivityComponent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSyncChangeHandler.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSyncChangeHandler.onPause();
    }

    protected abstract int getLayoutResourceId();

    protected int getToolbarId() {
        return R.id.toolbar;
    }

    protected <T extends ViewDataBinding> T getBinding(Class<T> clazz) {
        return clazz.cast(mBinding);
    }

    protected void injectComponent(BaseActivityComponent baseActivityComponent) {
        baseActivityComponent.inject(this);
    }

    public BaseActivityComponent getBaseActivityComponent() {
        return mBaseActivityComponent;
    }

    public SyncChangeHandler getSyncChangeHandler() {
        return mSyncChangeHandler;
    }

    protected NearbooksApplicationComponent getNearbooksApplicationComponent() {
        return NearbooksApplication.getNearbooksApplicationComponent();
    }

    protected BaseActivityModule getBaseActivityModule() {
        return new BaseActivityModule(this);
    }

}
