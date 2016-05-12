package com.nearsoft.nearbooks.view.fragments

import android.os.Bundle
import com.nearsoft.nearbooks.di.components.BaseActivityComponent
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.view.activities.BaseActivity
import com.trello.rxlifecycle.components.support.RxFragment
import dagger.Lazy
import javax.inject.Inject

/**
 * Base fragment.
 * Created by epool on 11/18/15.
 */
abstract class BaseFragment : RxFragment() {

    @Inject
    lateinit protected var mLazyUser: Lazy<User>
    protected val baseActivity: BaseActivity by lazy { activity!! as BaseActivity }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectComponent(baseActivity.baseActivityComponent)
    }

    protected fun injectComponent(baseActivityComponent: BaseActivityComponent) {
        baseActivityComponent.inject(this)
    }

}
