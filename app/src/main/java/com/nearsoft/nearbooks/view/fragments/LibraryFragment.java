package com.nearsoft.nearbooks.view.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentLibraryBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.sync.SyncChangeHandler;
import com.nearsoft.nearbooks.util.SyncUtil;
import com.nearsoft.nearbooks.view.activities.BaseActivity;
import com.nearsoft.nearbooks.view.adapters.BookRecyclerViewCursorAdapter;
import com.nearsoft.nearbooks.view.helpers.RecyclerItemClickListener;
import com.nearsoft.nearbooks.view.helpers.SpacingDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLibraryFragmentListener} interface
 * to handle interaction events.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment
        extends BaseFragment
        implements RecyclerItemClickListener.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        SyncChangeHandler.OnSyncChangeListener,
        BaseActivity.OnSearchListener,
        Filter.FilterListener {

    private OnLibraryFragmentListener mListener;
    private BookRecyclerViewCursorAdapter mBookRecyclerViewCursorAdapter;
    private FragmentLibraryBinding mBinding;
    private SearchView mSearchView;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibraryFragment.
     */
    public static LibraryFragment newInstance() {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mBookRecyclerViewCursorAdapter =
                new BookRecyclerViewCursorAdapter(BookModel.getAllBooks());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        mBinding.recyclerViewBooks.setHasFixedSize(true);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);
        boolean isTablet = getResources().getBoolean(R.bool.isTable);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),
                isTablet ? isLandscape ? 6 : 4 : isLandscape ? 4 : 2);
        mBinding.recyclerViewBooks.setLayoutManager(layoutManager);
        int margin = getResources().getDimensionPixelSize(R.dimen.books_margin);
        mBinding.recyclerViewBooks.addItemDecoration(new SpacingDecoration(margin, margin, true));
        mBinding.recyclerViewBooks.setAdapter(mBookRecyclerViewCursorAdapter);
        mBinding.recyclerViewBooks
                .addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));

        mBinding.swipeRefreshLayout.setOnRefreshListener(this);
        mBinding.textViewEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLibraryFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnLibraryFragmentListener");
        }

        BaseActivity baseActivity = getBaseActivity();
        SyncChangeHandler syncChangeHandler = baseActivity.getSyncChangeHandler();
        syncChangeHandler.addOnSyncChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        getBaseActivity()
                .getSyncChangeHandler()
                .removeOnSyncChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the options menu from XML
        inflater.inflate(R.menu.menu_library, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager)
                getContext().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        // Assumes current activity is the searchable activity
        mSearchView
                .setSearchableInfo(
                        searchManager
                                .getSearchableInfo(
                                        getBaseActivity().getComponentName()
                                )
                );
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mSearchView.setIconified(true);
                    mSearchView.setQuery("", false);
                }
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mBookRecyclerViewCursorAdapter.getFilter().filter(query, LibraryFragment.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mBookRecyclerViewCursorAdapter.getFilter().filter(newText, LibraryFragment.this);
                return false;
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mListener != null) {
            Book book = mBookRecyclerViewCursorAdapter.getItem(position);
            mListener.onBookSelected(book, view);
        }
    }

    @Override
    public void onRefresh() {
        if (!SyncUtil.isSyncing(mUser)) {
            SyncUtil.triggerRefresh(mUser);
        }
    }

    @Override
    public void onSyncChange(final boolean isSyncing) {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            mBinding.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (!mBinding.swipeRefreshLayout.isRefreshing() && isSyncing) {
                        mBinding.swipeRefreshLayout.setRefreshing(true);
                    } else if (mBinding.swipeRefreshLayout.isRefreshing() && !isSyncing) {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                    updateUI();
                }
            });
        }
    }

    private void updateUI() {
        if (mBookRecyclerViewCursorAdapter.getItemCount() == 0) {
            mBinding.recyclerViewBooks.setVisibility(View.GONE);
            mBinding.textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            mBinding.recyclerViewBooks.setVisibility(View.VISIBLE);
            mBinding.textViewEmpty.setVisibility(View.GONE);
        }
        mBookRecyclerViewCursorAdapter.notifyDataChanged();
    }

    @Override
    public void onSearchRequest(String query) {
        if (mSearchView != null) mSearchView.setQuery(query, false);
    }

    @Override
    public void onFilterComplete(int count) {
        updateUI();
    }

    public interface OnLibraryFragmentListener {

        void onBookSelected(Book book, View view);

    }

}
