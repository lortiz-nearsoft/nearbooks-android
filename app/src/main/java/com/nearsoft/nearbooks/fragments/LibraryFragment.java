package com.nearsoft.nearbooks.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.adapters.BookListAdapter;
import com.nearsoft.nearbooks.databinding.FragmentLibraryBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.models.viewmodels.BookViewModel;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLibraryFragmentListener} interface
 * to handle interaction events.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private OnLibraryFragmentListener mListener;
    private Realm realm;

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
        FragmentLibraryBinding binding = getBinding(FragmentLibraryBinding.class);

        RealmResults<Book> books = BookModel.getAllBooks(realm);
        BookListAdapter bookListAdapter = new BookListAdapter(getActivity(), books, true);

        binding.listViewBooks.setEmptyView(binding.getRoot().findViewById(android.R.id.empty));
        binding.listViewBooks.setAdapter(bookListAdapter);
        binding.listViewBooks.setOnItemClickListener(this);

        return rootView;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        realm.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            Book book = (Book) parent.getItemAtPosition(position);
            mListener.onBookSelected(new BookViewModel(book), view.findViewById(R.id.imageViewBookCover));
        }
    }

    public interface OnLibraryFragmentListener {
        void onBookSelected(BookViewModel bookViewModel, View view);
    }

}
