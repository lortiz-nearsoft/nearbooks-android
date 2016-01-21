package com.nearsoft.nearbooks.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.widget.ImageView;

import com.nearsoft.nearbooks.view.helpers.SimpleTransitionListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

/**
 * Image loader.
 * Created by epool on 1/8/16.
 */
public class ImageLoader {

    private final Activity activity;
    private final ImageView imageView;
    private final String thumbnailImageUrl;
    private final String fullResolutionImageUrl;
    private final int placeholderResourceId;
    private final int errorResourceId;
    private final Palette.PaletteAsyncListener paletteAsyncListener;
    private final boolean transitionListenerAdded;
    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(bitmap);

            if (paletteAsyncListener != null) {
                Palette.from(bitmap).generate(paletteAsyncListener);
            }

            if (!transitionListenerAdded && fullResolutionImageUrl != null) {
                loadFullResolutionImage();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            imageView.setImageDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            imageView.setImageDrawable(placeHolderDrawable);
        }
    };

    private ImageLoader(Builder builder) {
        this.activity = builder.activity;
        this.imageView = builder.imageView;
        // http://goo.gl/BWZW22
        this.imageView.setTag(target);
        this.thumbnailImageUrl = builder.thumbnailImageUrl;
        this.fullResolutionImageUrl = builder.fullResolutionImageUrl;
        this.placeholderResourceId = builder.placeholderResourceId;
        this.errorResourceId = builder.errorResourceId;
        this.paletteAsyncListener = builder.paletteAsyncListener;
        this.transitionListenerAdded = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                addTransitionListener();
    }

    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        if (activity == null) {
            return false;
        }
        final Transition transition = activity.getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    loadFullResolutionImage();

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }

    private void loadFullResolutionImage() {
        if (fullResolutionImageUrl != null) {
            Picasso.with(imageView.getContext())
                    .load(fullResolutionImageUrl)
                    .noFade()
                    .noPlaceholder()
                    .error(errorResourceId)
                    .into(imageView);
        }
    }

    private void load() {
        RequestCreator requestCreator = Picasso.with(imageView.getContext()).load(thumbnailImageUrl);
        if (placeholderResourceId == 0) {
            requestCreator.noPlaceholder();
        } else {
            requestCreator.placeholder(placeholderResourceId);
        }
        if (errorResourceId != 0) {
            requestCreator.error(errorResourceId);
        }
        requestCreator.into(target);
    }

    public interface OnImageLoadListener {

        void onError();

    }

    public static class Builder {

        private final Activity activity;
        private final ImageView imageView;
        private final String thumbnailImageUrl;
        private String fullResolutionImageUrl;
        private int placeholderResourceId;
        private int errorResourceId;
        private Palette.PaletteAsyncListener paletteAsyncListener;

        public Builder(Activity activity, ImageView imageView, String thumbnailImageUrl) {
            this.activity = activity;
            this.imageView = imageView;
            this.thumbnailImageUrl = thumbnailImageUrl;
        }

        public Builder fullResolutionImageUrl(String fullResolutionImageUrl) {
            this.fullResolutionImageUrl = fullResolutionImageUrl;
            return this;
        }

        public Builder placeholderResourceId(int thumbnailResourceId) {
            this.placeholderResourceId = thumbnailResourceId;
            return this;
        }

        public Builder errorResourceId(int errorResourceId) {
            this.errorResourceId = errorResourceId;
            return this;
        }

        public Builder paletteAsyncListener(Palette.PaletteAsyncListener paletteAsyncListener) {
            this.paletteAsyncListener = paletteAsyncListener;
            return this;
        }

        public void load() {
            new ImageLoader(this).load();
        }

    }

}
