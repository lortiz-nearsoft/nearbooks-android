package com.nearsoft.nearbooks.di.components;

import com.nearsoft.nearbooks.di.modules.BaseActivityModule;
import com.nearsoft.nearbooks.di.scopes.PerActivity;

import dagger.Component;

/**
 * Dagger 2 Activity component.
 * Created by epool on 11/17/15.
 */
@PerActivity
@Component(dependencies = {NearbooksApplicationComponent.class}, modules = {BaseActivityModule.class})
public interface BaseActivityComponent {
}
