package com.nearsoft.nearbooks.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.nearsoft.nearbooks.di.components.BaseActivityComponent;
import com.nearsoft.nearbooks.models.view.User;
import com.nearsoft.nearbooks.view.activities.BaseActivity;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Base fragment.
 * Created by epool on 11/18/15.
 */
public abstract class BaseFragment extends Fragment {

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Inject
    protected Lazy<User> mLazyUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectComponent(getBaseActivity().getBaseActivityComponent());
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected void injectComponent(BaseActivityComponent baseActivityComponent) {
        baseActivityComponent.inject(this);
    }

    @Override
    public void onDestroy() {
        unSubscribeFromActivity();

        super.onDestroy();
    }

    protected void subscribeToFragment(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected void unSubscribeFromActivity() {
        mCompositeSubscription.clear();
    }

}
