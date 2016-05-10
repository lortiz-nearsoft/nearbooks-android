package com.nearsoft.nearbooks.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import com.nearsoft.nearbooks.di.components.BaseActivityComponent
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.view.activities.BaseActivity
import dagger.Lazy
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Base fragment.
 * Created by epool on 11/18/15.
 */
abstract class BaseFragment : Fragment() {

    private val mCompositeSubscription = CompositeSubscription()

    @Inject
    lateinit protected var mLazyUser: Lazy<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectComponent(getBaseActivity()?.baseActivityComponent!!)
    }

    protected fun getBaseActivity(): BaseActivity? = activity as BaseActivity

    protected fun injectComponent(baseActivityComponent: BaseActivityComponent) {
        baseActivityComponent.inject(this)
    }

    override fun onDestroy() {
        unSubscribeFromActivity()

        super.onDestroy()
    }

    protected fun subscribeToFragment(subscription: Subscription) {
        mCompositeSubscription.add(subscription)
    }

    protected fun unSubscribeFromActivity() {
        mCompositeSubscription.clear()
    }

}
