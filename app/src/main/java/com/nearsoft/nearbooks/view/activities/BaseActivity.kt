package com.nearsoft.nearbooks.view.activities

import android.app.SearchManager
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.di.components.BaseActivityComponent
import com.nearsoft.nearbooks.di.components.DaggerBaseActivityComponent
import com.nearsoft.nearbooks.di.modules.BaseActivityModule
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.sync.SyncChangeHandler
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import dagger.Lazy
import javax.inject.Inject

/**
 * Base activity.
 * Created by epool on 11/17/15.
 */
abstract class BaseActivity : RxAppCompatActivity() {

    @Inject
    lateinit var syncChangeHandler: SyncChangeHandler
    @Inject
    lateinit protected var mLazyUser: Lazy<User>
    private lateinit var mBinding: ViewDataBinding
    val baseActivityComponent: BaseActivityComponent by lazy {
        DaggerBaseActivityComponent
                .builder()
                .nearbooksApplicationComponent(NearbooksApplication.applicationComponent)
                .baseActivityModule(BaseActivityModule(this))
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initDependencies()
        super.onCreate(savedInstanceState)

        val layoutResourceId = getLayoutResourceId()

        mBinding = DataBindingUtil.setContentView<ViewDataBinding>(this, layoutResourceId)

        val toolbar = findViewById(getToolbarId()) as Toolbar?
        setSupportActionBar(toolbar)

        handleIntent(intent)
    }

    private fun initDependencies() {
        injectComponent(baseActivityComponent)
    }

    override fun onResume() {
        super.onResume()

        syncChangeHandler.onResume()
    }

    override fun onPause() {
        syncChangeHandler.onPause()

        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (this is OnSearchListener) {
                this.onSearchRequest(query)
            }
            for (fragment in supportFragmentManager.fragments) {
                if (fragment is OnSearchListener) {
                    fragment.onSearchRequest(query)
                }
            }
        }
    }

    protected abstract fun getLayoutResourceId(): Int

    protected fun getToolbarId(): Int = R.id.toolbar

    protected fun <T : ViewDataBinding> getBinding(clazz: Class<T>): T {
        return clazz.cast(mBinding)
    }

    protected open fun injectComponent(baseActivityComponent: BaseActivityComponent) {
        baseActivityComponent.inject(this)
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        if (toolbar != null) {
            super.setSupportActionBar(toolbar)
        }
    }

    protected val baseActivityModule: BaseActivityModule
        get() = BaseActivityModule(this)

    interface OnSearchListener {

        fun onSearchRequest(query: String)

    }

}
