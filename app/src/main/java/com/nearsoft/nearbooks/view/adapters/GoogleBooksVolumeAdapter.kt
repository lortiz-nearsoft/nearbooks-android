package com.nearsoft.nearbooks.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nearsoft.nearbooks.databinding.ItemGoogleBooksVolumeBinding
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody
import java.util.*

/**
 * Google books volume adapter.
 * Created by epool on 2/2/16.
 */
class GoogleBooksVolumeAdapter(private val mOnGoogleBookItemClickListener: (ItemGoogleBooksVolumeBinding) -> Unit) : RecyclerView.Adapter<GoogleBooksVolumeAdapter.GoogleBooksVolumeViewHolder>() {

    private val mGoogleBookBodies = ArrayList<GoogleBookBody>()

    fun setGoogleBookBodies(googleBookBodies: List<GoogleBookBody>) {
        mGoogleBookBodies.clear()
        mGoogleBookBodies.addAll(googleBookBodies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoogleBooksVolumeViewHolder {
        val binding = ItemGoogleBooksVolumeBinding.inflate(LayoutInflater.from(parent.context))
        return GoogleBooksVolumeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoogleBooksVolumeViewHolder, position: Int) {
        holder.setupViewAtPosition(position)
    }

    override fun getItemCount(): Int {
        return mGoogleBookBodies.size
    }

    inner class GoogleBooksVolumeViewHolder(private val mBinding: ItemGoogleBooksVolumeBinding) : RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {

        init {
            mBinding.root.setOnClickListener(this)
        }

        fun setupViewAtPosition(position: Int) {
            val googleBookBody = mGoogleBookBodies[position]
            mBinding.book = googleBookBody
            mBinding.executePendingBindings()
        }

        override fun onClick(v: View) {
            mOnGoogleBookItemClickListener(mBinding)
        }

    }

}
