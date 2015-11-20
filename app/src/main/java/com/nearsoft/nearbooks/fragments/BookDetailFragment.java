package com.nearsoft.nearbooks.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentBookDetailBinding;
import com.nearsoft.nearbooks.models.viewmodels.BookViewModel;

public class BookDetailFragment extends BaseFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_BOOK_ITEM = "ARG_BOOK_ITEM";

    /**
     * The dummy title this fragment is presenting.
     */
    private BookViewModel bookViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    public static BookDetailFragment newInstance(BookViewModel bookViewModel) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK_ITEM, bookViewModel);

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
            bookViewModel = getArguments().getParcelable(ARG_BOOK_ITEM);
            if (bookViewModel != null) {
                setTitle(bookViewModel.getTitle());
            }
        }
    }

    public void setTitle(String title) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(title);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_book_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        FragmentBookDetailBinding binding = getBinding(FragmentBookDetailBinding.class);
        binding.setBook(bookViewModel);

        return rootView;
    }

}
