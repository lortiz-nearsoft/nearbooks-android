package com.nearsoft.nearbooks.di.components;

import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule;
import com.nearsoft.nearbooks.di.modules.NetModule;
import com.nearsoft.nearbooks.ws.BookService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger 2 Nearbooks application component.
 * Created by epool on 11/17/15.
 */
@Singleton
@Component(modules = {NearbooksApplicationModule.class, NetModule.class})
public interface NearbooksApplicationComponent {

    BookService providesBookService();

}
