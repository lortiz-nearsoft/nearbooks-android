package com.nearsoft.nearbooks.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.databinding.ItemGoogleBooksVolumeBinding;
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Google books volume adapter.
 * Created by epool on 2/2/16.
 */
public class GoogleBooksVolumeAdapter
        extends RecyclerView.Adapter<GoogleBooksVolumeAdapter.GoogleBooksVolumeViewHolder> {

    private final List<GoogleBookBody> mGoogleBookBodies = new ArrayList<>();
    private final OnGoogleBookItemClickListener mOnGoogleBookItemClickListener;

    public GoogleBooksVolumeAdapter(OnGoogleBookItemClickListener onGoogleBookItemClickListener) {
        mOnGoogleBookItemClickListener = onGoogleBookItemClickListener;
    }

    public void setGoogleBookBodies(List<GoogleBookBody> googleBookBodies) {
        mGoogleBookBodies.clear();
        mGoogleBookBodies.addAll(googleBookBodies);
        notifyDataSetChanged();
    }

    @Override
    public GoogleBooksVolumeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemGoogleBooksVolumeBinding binding = ItemGoogleBooksVolumeBinding
                .inflate(LayoutInflater.from(parent.getContext()));
        return new GoogleBooksVolumeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(GoogleBooksVolumeViewHolder holder, int position) {
        holder.setupViewAtPosition(position);
    }

    @Override
    public int getItemCount() {
        return mGoogleBookBodies.size();
    }

    public interface OnGoogleBookItemClickListener {

        void onGoogleBookItemClicked(ItemGoogleBooksVolumeBinding binding);

    }

    public class GoogleBooksVolumeViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ItemGoogleBooksVolumeBinding mBinding;

        public GoogleBooksVolumeViewHolder(ItemGoogleBooksVolumeBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnClickListener(this);
        }

        public void setupViewAtPosition(int position) {
            GoogleBookBody googleBookBody = mGoogleBookBodies.get(position);
            mBinding.setBook(googleBookBody);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            if (mOnGoogleBookItemClickListener != null) {
                mOnGoogleBookItemClickListener.onGoogleBookItemClicked(mBinding);
            }
        }

    }

}
