package com.nearsoft.nearbooks.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentLibraryBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.sync.SyncChangeHandler;
import com.nearsoft.nearbooks.util.SyncUtil;
import com.nearsoft.nearbooks.view.activities.BaseActivity;
import com.nearsoft.nearbooks.view.adapters.BookRecyclerViewCursorAdapter;
import com.nearsoft.nearbooks.view.helpers.RecyclerItemClickListener;
import com.raizlabs.android.dbflow.sql.language.Where;

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
        SyncChangeHandler.OnSyncChangeListener {

    private OnLibraryFragmentListener mListener;
    private BookRecyclerViewCursorAdapter mBookRecyclerViewCursorAdapter;
    private FragmentLibraryBinding mBinding;

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
    protected int getLayoutResourceId() {
        return R.layout.fragment_library;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mBinding = getBinding(FragmentLibraryBinding.class);

        Where<Book> bookWhere = BookModel.getAllBooks();
        if (bookWhere.hasData()) {
            mBookRecyclerViewCursorAdapter = new BookRecyclerViewCursorAdapter(bookWhere);
        }

        mBinding.recyclerViewBooks.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerViewBooks.setLayoutManager(layoutManager);
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

        return rootView;
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

        getBaseActivity()
                .getSyncChangeHandler()
                .addOnSyncChangeListener(this);
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
    public void onItemClick(View view, int position) {
        if (mListener != null) {
            Book book = mBookRecyclerViewCursorAdapter.getItem(position);
            mListener.onBookSelected(book, view.findViewById(R.id.imageViewBookCover));
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
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mBinding.swipeRefreshLayout.isRefreshing() && isSyncing) {
                        mBinding.swipeRefreshLayout.setRefreshing(true);
                    } else if (mBinding.swipeRefreshLayout.isRefreshing() && !isSyncing) {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }
    }

    private void updateUI() {
        if (mBookRecyclerViewCursorAdapter != null) {
            if (mBookRecyclerViewCursorAdapter.getItemCount() == 0) {
                mBinding.recyclerViewBooks.setVisibility(View.GONE);
                mBinding.textViewEmpty.setVisibility(View.VISIBLE);
            } else {
                mBinding.recyclerViewBooks.setVisibility(View.VISIBLE);
                mBinding.textViewEmpty.setVisibility(View.GONE);
            }
            mBookRecyclerViewCursorAdapter.notifyDataSetChanged();
        }
    }

    public interface OnLibraryFragmentListener {

        void onBookSelected(Book book, View view);

    }

}
