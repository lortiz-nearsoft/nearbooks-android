package com.nearsoft.nearbooks.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.common.Constants;
import com.nearsoft.nearbooks.databinding.ActivityHomeBinding;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.databinding.NavHeaderHomeBinding;
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent;
import com.nearsoft.nearbooks.models.SharedPreferenceModel;
import com.nearsoft.nearbooks.models.UserModel;
import com.nearsoft.nearbooks.util.TapsEasterEggHandler;
import com.nearsoft.nearbooks.view.adapters.BookRecyclerViewCursorAdapter;
import com.nearsoft.nearbooks.view.fragments.BaseFragment;
import com.nearsoft.nearbooks.view.fragments.LibraryFragment;
import com.nearsoft.nearbooks.view.fragments.RegisterBookFragment;

public class HomeActivity
        extends GoogleApiClientBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BookRecyclerViewCursorAdapter.OnBookItemClickListener {

    private ActivityHomeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getBinding(ActivityHomeBinding.class);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mBinding.drawerLayout,
                mBinding.appBarHome.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavHeaderHomeBinding navHeaderHomeBinding = NavHeaderHomeBinding
                .inflate(getLayoutInflater());
        setupEasterEgg(navHeaderHomeBinding.imageView);
        navHeaderHomeBinding.setUser(mLazyUser.get());
        navHeaderHomeBinding.executePendingBindings();
        mBinding.navView.addHeaderView(navHeaderHomeBinding.getRoot());

        mBinding.navView.setNavigationItemSelectedListener(this);

        mBinding.navView.setCheckedItem(R.id.nav_library);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.content,
                            LibraryFragment.newInstance(),
                            LibraryFragment.class.getName()
                    )
                    .commit();
        }
    }

    private void setupEasterEgg(ImageView imageView) {
        TapsEasterEggHandler.Companion.with(
                this,
                imageView,
                SharedPreferenceModel.PREFERENCE_IS_UPLOAD_BOOKS_MENU_SHOWN
        )
                .enableMessageRes(R.string.message_upload_books_menu_enabled)
                .counterMessageRes(R.string.message_steps_to_enable_upload_books_menu)
                .password(Constants.NEARBOOKS_MANAGER_PASSWORD)
                .actionToHandle(this::showRegisterBookMenu)
                .build();
    }

    private void showRegisterBookMenu() {
        mBinding
                .navView
                .getMenu()
                .findItem(R.id.nav_register_book)
                .setVisible(true);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void injectComponent(@NonNull GoogleApiClientComponent googleApiClientComponent) {
        super.injectComponent(googleApiClientComponent);
        googleApiClientComponent.inject(this);
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Create a new fragment and specify the planet to show based on
        // position
        BaseFragment baseFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_library:
                baseFragment = LibraryFragment.newInstance();
                break;
            case R.id.nav_register_book:
                baseFragment = RegisterBookFragment.newInstance();
                break;
        }

        if (baseFragment != null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager
                    .findFragmentByTag(baseFragment.getClass().getName());
            fragmentManager
                    .beginTransaction()
                    .replace(
                            R.id.content,
                            fragment != null ? fragment : baseFragment,
                            baseFragment.getClass().getName()
                    )
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
            item.setChecked(true);
            setTitle(item.getTitle());
        }

        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mBinding.linearLayoutSignOutMenu.setOnClickListener(v ->
                UserModel.INSTANCE.signOut(this, mLazyUser.get(), mGoogleApiClient, () -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }));
    }

    @Override
    public void onBookItemClicked(BookItemBinding binding) {
        BookDetailActivity.openWith(this, binding);
    }
}
