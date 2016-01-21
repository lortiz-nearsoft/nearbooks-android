package com.nearsoft.nearbooks.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.nearsoft.nearbooks.di.components.BaseActivityComponent;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.view.activities.BaseActivity;

import javax.inject.Inject;

/**
 * Base fragment.
 * Created by epool on 11/18/15.
 */
public abstract class BaseFragment extends Fragment {

    @Inject
    protected User mUser;

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

}
