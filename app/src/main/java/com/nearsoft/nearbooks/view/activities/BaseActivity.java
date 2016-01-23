package com.nearsoft.nearbooks.view.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.di.components.BaseActivityComponent;
import com.nearsoft.nearbooks.di.components.DaggerBaseActivityComponent;
import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.modules.BaseActivityModule;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.SyncChangeHandler;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Base activity.
 * Created by epool on 11/17/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    @Inject
    protected SyncChangeHandler mSyncChangeHandler;
    @Inject
    protected Lazy<User> mLazyUser;
    private ViewDataBinding mBinding;
    private BaseActivityComponent mBaseActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initDependencies();
        super.onCreate(savedInstanceState);

        int layoutResourceId = getLayoutResourceId();

        mBinding = DataBindingUtil.setContentView(this, layoutResourceId);

        Toolbar toolbar = (Toolbar) findViewById(getToolbarId());
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        handleIntent(getIntent());
    }

    private void initDependencies() {
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
        mSyncChangeHandler.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unSubscribeFromActivity();

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (this instanceof OnSearchListener) {
                OnSearchListener onSearchListener = (OnSearchListener) this;
                onSearchListener.onSearchRequest(query);
            }
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof OnSearchListener) {
                    OnSearchListener onSearchListener = (OnSearchListener) fragment;
                    onSearchListener.onSearchRequest(query);
                }
            }
        }
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

    protected void subscribeToActivity(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected void unSubscribeFromActivity() {
        mCompositeSubscription.clear();
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

    public interface OnSearchListener {

        void onSearchRequest(String query);

    }

}
