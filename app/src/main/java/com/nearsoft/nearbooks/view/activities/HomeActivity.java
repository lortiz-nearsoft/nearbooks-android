package com.nearsoft.nearbooks.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityHomeBinding;
import com.nearsoft.nearbooks.databinding.NavHeaderHomeBinding;
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.UserModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.view.activities.zxing.CaptureActivityAnyOrientation;
import com.nearsoft.nearbooks.view.fragments.BaseFragment;
import com.nearsoft.nearbooks.view.fragments.LibraryFragment;

public class HomeActivity
        extends GoogleApiClientBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LibraryFragment.OnLibraryFragmentListener {

    private final static int ACTION_REQUEST = 0;
    private final static int ACTION_CHECK_IN = 1;
    private final static int ACTION_CHECK_OUT = 2;

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

        mBinding.linearLayoutSignOutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel.signOut(HomeActivity.this, mLazyUser.get(), mGoogleApiClient, new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        NavHeaderHomeBinding navHeaderHomeBinding = NavHeaderHomeBinding
                .inflate(getLayoutInflater());
        navHeaderHomeBinding.setUser(mLazyUser.get());
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Create a new fragment and specify the planet to show based on
        // position
        BaseFragment baseFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_library:
                baseFragment = LibraryFragment.newInstance();
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
        integrator.setPrompt(getString(R.string.message_scan_book_qr_code));
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
            String qrCode = scanResult.getContents();
            showBookActions(qrCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showBookActions(@NonNull final String qrCode) {
        String[] qrCodeParts = qrCode.split("-");
        if (qrCodeParts.length == 2) {
            Book book = BookModel.findByBookId(qrCodeParts[0]);
            if (book != null) {
                new AlertDialog.Builder(this)
                        .setTitle(book.getTitle())
                        .setItems(R.array.actions_book, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case ACTION_REQUEST:
                                        BookModel.requestBookToBorrow(mBinding, mLazyUser.get(),
                                                qrCode, null);
                                        break;
                                    case ACTION_CHECK_IN:
                                        BookModel.doBookCheckIn(mBinding, mLazyUser.get(), qrCode);
                                        break;
                                    case ACTION_CHECK_OUT:
                                        BookModel.doBookCheckOut(mBinding, mLazyUser.get(), qrCode);
                                        break;
                                }
                            }
                        })
                        .show();
            } else {
                Snackbar
                        .make(
                                mBinding.getRoot(),
                                getResources()
                                        .getQuantityString(R.plurals.message_books_not_found, 1),
                                Snackbar.LENGTH_LONG
                        )
                        .show();
            }
        } else {
            Snackbar
                    .make(
                            mBinding.getRoot(),
                            getString(R.string.error_invalid_qr_code),
                            Snackbar.LENGTH_LONG
                    )
                    .show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onBookSelected(Book book, View view) {
        BookDetailActivity.openWith(this, book, view);
    }

}
