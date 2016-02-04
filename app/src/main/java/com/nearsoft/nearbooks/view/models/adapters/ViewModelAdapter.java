package com.nearsoft.nearbooks.view.models.adapters;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * View model adapter.
 * Created by epool on 11/19/15.
 */
public class ViewModelAdapter {

    @BindingAdapter({"bind:imageUrl", "bind:error"})
    public static void loadImage(ImageView imageView, String url, Drawable error) {
        Picasso
                .with(imageView.getContext())
                .load(url)
                .placeholder(error)
                .error(error)
                .into(imageView);
    }

    @BindingAdapter({"bind:colors"})
    public static void setColorSchemeColors(SwipeRefreshLayout swipeRefreshLayout,
                                            int colorsResource) {
        swipeRefreshLayout
                .setColorSchemeColors(
                        swipeRefreshLayout
                                .getContext()
                                .getResources()
                                .getIntArray(colorsResource)
                );
    }

    @BindingAdapter({"bind:html"})
    public static void setTextFromHtml(TextView textView, String htmlString) {
        textView.setText(Html.fromHtml(htmlString));
    }

}
