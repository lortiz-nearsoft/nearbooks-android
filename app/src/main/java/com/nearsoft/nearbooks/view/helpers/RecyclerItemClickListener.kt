package com.nearsoft.nearbooks.view.helpers

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Recycler item click listener.
 * Created by epool on 11/20/15.
 */
class RecyclerItemClickListener(
        context: Context,
        private val mListener: RecyclerItemClickListener.OnItemClickListener?
) : RecyclerView.SimpleOnItemTouchListener() {

    private val mGestureDetector: GestureDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }
            })

    override fun onInterceptTouchEvent(view: RecyclerView?, e: MotionEvent?): Boolean {
        val childView = view!!.findChildViewUnder(e!!.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

}
