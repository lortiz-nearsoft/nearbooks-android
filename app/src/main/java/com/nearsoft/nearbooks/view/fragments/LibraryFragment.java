package com.nearsoft.nearbooks.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentLibraryBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.view.adapters.realm.BookRecyclerViewAdapter;
import com.nearsoft.nearbooks.view.helpers.RecyclerItemClickListener;
import com.nearsoft.nearbooks.view.models.BookViewModel;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLibraryFragmentListener} interface
 * to handle interaction events.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends BaseFragment implements RecyclerItemClickListener.OnItemClickListener, RealmChangeListener {
    private OnLibraryFragmentListener mListener;
    private Realm realm;
    private BookRecyclerViewAdapter bookRecyclerViewAdapter;
    private FragmentLibraryBinding binding;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        binding = getBinding(FragmentLibraryBinding.class);

        RealmResults<Book> books = BookModel.getAllBooks(realm);
        bookRecyclerViewAdapter = new BookRecyclerViewAdapter(getContext(), books, true);

        boolean isTablet = getResources().getBoolean(R.bool.isTable);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(
                getContext(),
                isTablet ? isLandscape ? 3 : 2 : 1
        );

        binding.recyclerViewBooks.setHasFixedSize(true);
        binding.recyclerViewBooks.setLayoutManager(layoutManager);
        binding.recyclerViewBooks.setAdapter(bookRecyclerViewAdapter);
        binding.recyclerViewBooks.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));

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
            throw new ClassCastException(context.toString()
                    + " must implement OnLibraryFragmentListener");
        }

        realm = Realm.getDefaultInstance();
        realm.addChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        realm.removeChangeListener(this);
        realm.close();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mListener != null) {
            Book book = bookRecyclerViewAdapter.getItem(position);
            mListener.onBookSelected(new BookViewModel(book), view.findViewById(R.id.imageViewBookCover));
        }
    }

    @Override
    public void onChange() {
        updateUI();
    }

    private void updateUI() {
        if (bookRecyclerViewAdapter != null) {
            if (bookRecyclerViewAdapter.getItemCount() == 0) {
                binding.recyclerViewBooks.setVisibility(View.GONE);
                binding.empty.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewBooks.setVisibility(View.VISIBLE);
                binding.empty.setVisibility(View.GONE);
            }
        }
    }

    public interface OnLibraryFragmentListener {
        void onBookSelected(BookViewModel bookViewModel, View view);
    }

}
