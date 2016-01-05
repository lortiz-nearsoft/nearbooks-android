package com.nearsoft.nearbooks.di.components;

import com.nearsoft.nearbooks.di.modules.BaseActivityModule;
import com.nearsoft.nearbooks.di.scopes.PerActivity;
import com.nearsoft.nearbooks.view.activities.BaseActivity;
import com.nearsoft.nearbooks.view.activities.MainActivity;
import com.nearsoft.nearbooks.view.fragments.BaseFragment;

import dagger.Component;

/**
 * Dagger 2 Activity component.
 * Created by epool on 11/17/15.
 */
@PerActivity
@Component(
        dependencies = {NearbooksApplicationComponent.class},
        modules = {BaseActivityModule.class}
)
public interface BaseActivityComponent {

    void inject(BaseActivity baseActivity);

    void inject(MainActivity mainActivity);

    void inject(BaseFragment baseFragment);

}
