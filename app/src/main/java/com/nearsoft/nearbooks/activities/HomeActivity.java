package com.nearsoft.nearbooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.activities.zxing.CaptureActivityAnyOrientation;
import com.nearsoft.nearbooks.databinding.ActivityHomeBinding;
import com.nearsoft.nearbooks.fragments.BaseFragment;
import com.nearsoft.nearbooks.fragments.BookDetailFragment;
import com.nearsoft.nearbooks.fragments.LibraryFragment;
import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.models.viewmodels.BookViewModel;
import com.nearsoft.nearbooks.models.viewmodels.UserViewModel;

import io.realm.Realm;

public class HomeActivity extends GoogleApiClientBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LibraryFragment.OnLibraryFragmentListener {
    public final static String USER_KEY = "USER_KEY";
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding(ActivityHomeBinding.class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRScanner();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        UserViewModel userViewModel = getIntent().getParcelableExtra(USER_KEY);
        TextView textViewEmail = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.textViewEmail);
        textViewEmail.setText(userViewModel.getEmail());

        binding.navView.setNavigationItemSelectedListener(this);

        binding.navView.setCheckedItem(R.id.nav_library);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, LibraryFragment.newInstance(), LibraryFragment.class.getName())
                .commit();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            Auth.GoogleSignInApi.signOut(googleApiClient);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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
            Fragment fragment = fragmentManager.findFragmentByTag(baseFragment.getClass().getName());
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment != null ? fragment : baseFragment, baseFragment.getClass().getName())
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
            item.setChecked(true);
            setTitle(item.getTitle());
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
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
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && scanResult.getContents() != null) {
            String isbn = scanResult.getContents();
            Realm realm = Realm.getDefaultInstance();
            Book book = realm.where(Book.class)
                    .equalTo(Book.ISBN, isbn)
                    .findFirst();
            realm.close();

            if (book != null) {
                goToBookDetail(new BookViewModel(book), binding.getRoot());
            } else {
                Snackbar.make(binding.getRoot(), R.string.message_book_not_found, Snackbar.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onBookSelected(BookViewModel bookViewModel, View view) {
        goToBookDetail(bookViewModel, view);
    }

    private void goToBookDetail(BookViewModel bookViewModel, View view) {
        Intent detailIntent = new Intent(this, BookDetailActivity.class);
        detailIntent.putExtra(BookDetailFragment.ARG_BOOK_ITEM, bookViewModel);

        String transitionName = getString(R.string.transition_book_cover);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        view,   // The view which starts the transition
                        transitionName    // The transitionName of the view we’re transitioning to
                );
        ActivityCompat.startActivity(this, detailIntent, options.toBundle());
    }
}
