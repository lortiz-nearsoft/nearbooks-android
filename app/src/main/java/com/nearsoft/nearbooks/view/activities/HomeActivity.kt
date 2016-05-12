package com.nearsoft.nearbooks.view.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.ImageView
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.common.Constants
import com.nearsoft.nearbooks.databinding.ActivityHomeBinding
import com.nearsoft.nearbooks.databinding.NavHeaderHomeBinding
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent
import com.nearsoft.nearbooks.models.SharedPreferenceModel
import com.nearsoft.nearbooks.models.UserModel
import com.nearsoft.nearbooks.util.TapsEasterEggHandler
import com.nearsoft.nearbooks.view.fragments.BaseFragment
import com.nearsoft.nearbooks.view.fragments.LibraryFragment
import com.nearsoft.nearbooks.view.fragments.RegisterBookFragment

class HomeActivity : GoogleApiClientBaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getBinding(ActivityHomeBinding::class.java)

        val toggle = ActionBarDrawerToggle(
                this,
                mBinding.drawerLayout,
                mBinding.appBarHome.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navHeaderHomeBinding = NavHeaderHomeBinding.inflate(layoutInflater)
        setupEasterEgg(navHeaderHomeBinding.imageView)
        navHeaderHomeBinding.user = mLazyUser.get()
        navHeaderHomeBinding.executePendingBindings()
        mBinding.navView.addHeaderView(navHeaderHomeBinding.root)

        mBinding.navView.setNavigationItemSelectedListener(this)

        mBinding.navView.setCheckedItem(R.id.nav_library)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                    R.id.content,
                    LibraryFragment.newInstance(),
                    LibraryFragment::class.java.name).commit()
        }
    }

    private fun setupEasterEgg(imageView: ImageView) {
        TapsEasterEggHandler.with(
                this,
                imageView,
                SharedPreferenceModel.PREFERENCE_IS_UPLOAD_BOOKS_MENU_SHOWN).enableMessageRes(R.string.message_upload_books_menu_enabled).counterMessageRes(R.string.message_steps_to_enable_upload_books_menu).password(Constants.NEARBOOKS_MANAGER_PASSWORD).actionToHandle(Runnable { this.showRegisterBookMenu() }).build()
    }

    private fun showRegisterBookMenu() {
        mBinding.navView.menu.findItem(R.id.nav_register_book).isVisible = true
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_home
    }

    override fun injectComponent(googleApiClientComponent: GoogleApiClientComponent) {
        super.injectComponent(googleApiClientComponent)
        googleApiClientComponent.inject(this)
    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Create a new fragment and specify the planet to show based on
        // position
        var baseFragment: BaseFragment? = null

        when (item.itemId) {
            R.id.nav_library -> baseFragment = LibraryFragment.newInstance()
            R.id.nav_register_book -> baseFragment = RegisterBookFragment.newInstance()
        }

        if (baseFragment != null) {
            // Insert the fragment by replacing any existing fragment
            val fragmentManager = supportFragmentManager
            val fragment = fragmentManager.findFragmentByTag(baseFragment.javaClass.name)
            fragmentManager.beginTransaction().replace(
                    R.id.content,
                    fragment ?: baseFragment,
                    baseFragment.javaClass.name).commit()

            // Highlight the selected item, update the title, and close the drawer
            item.isChecked = true
            title = item.title
        }

        mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onConnected(bundle: Bundle?) {
        mBinding.linearLayoutSignOutMenu.setOnClickListener { v ->
            UserModel.signOut(this, mLazyUser.get(), mGoogleApiClient, Runnable {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
        }
    }

}