package com.nearsoft.nearbooks.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentBookDetailBinding;
import com.nearsoft.nearbooks.models.sqlite.Book;

public class BookDetailFragment extends BaseFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_BOOK_ITEM = "ARG_BOOK_ITEM";

    private Book mBook;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    public static BookDetailFragment newInstance(Book book) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK_ITEM, book);

        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_BOOK_ITEM)) {
            // Load the dummy title specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load title from a title provider.
            mBook = getArguments().getParcelable(ARG_BOOK_ITEM);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_book_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        FragmentBookDetailBinding binding = getBinding(FragmentBookDetailBinding.class);
        binding.setBook(mBook);

        return rootView;
    }

}
