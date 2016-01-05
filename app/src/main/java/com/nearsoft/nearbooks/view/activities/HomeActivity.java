package com.nearsoft.nearbooks.view.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityHomeBinding;
import com.nearsoft.nearbooks.databinding.NavHeaderHomeBinding;
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.auth.AccountGeneral;
import com.nearsoft.nearbooks.view.activities.zxing.CaptureActivityAnyOrientation;
import com.nearsoft.nearbooks.view.fragments.BaseFragment;
import com.nearsoft.nearbooks.view.fragments.BookDetailFragment;
import com.nearsoft.nearbooks.view.fragments.LibraryFragment;

import javax.inject.Inject;

public class HomeActivity
        extends GoogleApiClientBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LibraryFragment.OnLibraryFragmentListener {

    @Inject
    User mUser;
    private ActivityHomeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getBinding(ActivityHomeBinding.class);

        mBinding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRScanner();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mBinding.drawerLayout,
                mBinding.appBarHome.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mBinding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavHeaderHomeBinding navHeaderHomeBinding = NavHeaderHomeBinding
                .inflate(getLayoutInflater());
        navHeaderHomeBinding.setUser(mUser);
        navHeaderHomeBinding.executePendingBindings();
        mBinding.navView.addHeaderView(navHeaderHomeBinding.getRoot());

        mBinding.navView.setNavigationItemSelectedListener(this);

        mBinding.navView.setCheckedItem(R.id.nav_library);
        // Insert the fragment by replacing any existing fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.content,
                        LibraryFragment.newInstance(),
                        LibraryFragment.class.getName()
                )
                .commit();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void injectComponent(GoogleApiClientComponent googleApiClientComponent) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            AccountManager accountManager = AccountManager.get(this);
            Account account = new Account(mUser.getEmail(), AccountGeneral.ACCOUNT_TYPE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccount(
                        account,
                        this,
                        new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(AccountManagerFuture<Bundle> future) {
                                if (future.isDone()) {
                                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        },
                        null
                );
            } else {
                accountManager.removeAccount(
                        account,
                        new AccountManagerCallback<Boolean>() {
                            @Override
                            public void run(AccountManagerFuture<Boolean> future) {
                                if (future.isDone()) {
                                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        },
                        null
                );
            }

            mUser.delete();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
            case R.id.nav_share:
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

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a book QR code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator
                .parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && scanResult.getContents() != null) {
            String isbn = scanResult.getContents();
            Book book = BookModel.findByIsbn(isbn);

            if (book != null) {
                goToBookDetail(book, mBinding.getRoot());
            } else {
                Snackbar.make(mBinding.getRoot(), R.string.message_book_not_found, Snackbar.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onBookSelected(Book book, View view) {
        goToBookDetail(book, view);
    }

    private void goToBookDetail(Book book, View view) {
        Intent detailIntent = new Intent(this, BookDetailActivity.class);
        detailIntent.putExtra(BookDetailFragment.ARG_BOOK_ITEM, book);

        String transitionName = getString(R.string.transition_book_cover);

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        this,
                        view,           // The view which starts the transition
                        transitionName  // The transitionName of the view we’re transitioning to
                );
        ActivityCompat.startActivity(this, detailIntent, options.toBundle());
    }
}
