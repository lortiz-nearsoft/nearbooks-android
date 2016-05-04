package com.nearsoft.nearbooks.view.models.adapters

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.support.v4.widget.SwipeRefreshLayout
import android.text.Html
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso

/**
 * View model adapter.
 * Created by epool on 11/19/15.
 */
class ViewModelAdapter {

    @BindingAdapter("bind:imageUrl", "bind:error")
    fun loadImage(imageView: ImageView, url: String, error: Drawable) {
        Picasso
                .with(imageView.context)
                .load(url)
                .placeholder(error)
                .error(error)
                .into(imageView)
    }

    @BindingAdapter("bind:colors")
    fun setColorSchemeColors(swipeRefreshLayout: SwipeRefreshLayout,
                             colorsResource: Int) {
        swipeRefreshLayout.setColorSchemeColors(
                *swipeRefreshLayout.context.resources.getIntArray(colorsResource)
        )
    }

    @BindingAdapter("bind:html")
    fun setTextFromHtml(textView: TextView, htmlString: String) {
        textView.text = Html.fromHtml(htmlString)
    }

}
