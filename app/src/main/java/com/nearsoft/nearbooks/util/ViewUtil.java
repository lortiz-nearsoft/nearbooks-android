package com.nearsoft.nearbooks.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Utilities related with views.
 * Created by epool on 1/8/16.
 */
public class ViewUtil {

    public static Observable<Bitmap> loadBitmapFromUrlImage(final Context context,
                                                            final String url) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = Picasso.with(context).load(url).get();
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Palette> getPaletteFromUrlImage(Context context, String url) {
        return loadBitmapFromUrlImage(context, url)
                .map(bitmap -> Palette.from(bitmap).generate());
    }

    public static Observable<ColorsWrapper> getColorsWrapperFromUrlImage(final Context context,
                                                                         String url) {
        return getPaletteFromUrlImage(context, url)
                .map(palette -> {
                    int defaultColor = ContextCompat
                            .getColor(context, R.color
                                    .colorPrimary);
                    return ViewUtil
                            .getVibrantPriorityColorSwatchPair(palette,
                                    defaultColor);
                });
    }

    public static Observable<ColorsWrapper> loadImageFromUrl(final ImageView imageView,
                                                             final String url) {
        final Context context = imageView.getContext();
        return Observable.create(new Observable.OnSubscribe<ColorsWrapper>() {
            @Override
            public void call(final Subscriber<? super ColorsWrapper> subscriber) {
                RequestCreator requestCreator = Picasso.with(context).load(url);
                requestCreator.placeholder(R.drawable.ic_launcher);
                requestCreator.error(R.drawable.ic_launcher);
                requestCreator.into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (subscriber.isUnsubscribed()) return;

                        ViewUtil.getColorsWrapperFromUrlImage(context, url)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(colorsWrapper -> {
                                    subscriber.onNext(colorsWrapper);
                                    subscriber.onCompleted();
                                });
                    }

                    @Override
                    public void onError() {
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    public static ColorsWrapper getVibrantPriorityColorSwatchPair(
            Palette palette, int defaultColor) {
        if (palette == null) return null;

        if (palette.getVibrantSwatch() != null) {
            return new ColorsWrapper(palette.getVibrantColor(defaultColor),
                    palette.getVibrantSwatch());
        } else if (palette.getLightVibrantSwatch() != null) {
            return new ColorsWrapper(palette.getLightVibrantColor(defaultColor),
                    palette.getLightVibrantSwatch());
        } else if (palette.getDarkVibrantSwatch() != null) {
            return new ColorsWrapper(palette.getDarkVibrantColor(defaultColor),
                    palette.getDarkVibrantSwatch());
        } else if (palette.getMutedSwatch() != null) {
            return new ColorsWrapper(palette.getMutedColor(defaultColor),
                    palette.getMutedSwatch());
        } else if (palette.getLightMutedSwatch() != null) {
            return new ColorsWrapper(palette.getLightMutedColor(defaultColor),
                    palette.getLightMutedSwatch());
        } else if (palette.getDarkMutedSwatch() != null) {
            return new ColorsWrapper(palette.getDarkMutedColor(defaultColor),
                    palette.getDarkMutedSwatch());
        }
        return null;
    }

    public static void showSnackbarMessage(ViewDataBinding viewDataBinding, String message) {
        Snackbar.make(viewDataBinding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    public static class Toolbar {
        /**
         * Use this method to colorize toolbar icons to the desired target color
         *
         * @param toolbarView       toolbar view being colored
         * @param toolbarIconsColor the target color of toolbar icons
         * @param activity          reference to activity needed to register observers
         */
        public static void colorizeToolbar(android.support.v7.widget.Toolbar toolbarView,
                                           int toolbarIconsColor, Activity activity) {
            final PorterDuffColorFilter colorFilter =
                    new PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.MULTIPLY);

            for (int i = 0; i < toolbarView.getChildCount(); i++) {
                final View v = toolbarView.getChildAt(i);

                //Step 1 : Changing the color of back button (or open drawer button).
                if (v instanceof ImageButton) {
                    //Action Bar back button
                    ((ImageButton) v).getDrawable().setColorFilter(colorFilter);
                }

                if (v instanceof ActionMenuView) {
                    for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {

                        //Step 2: Changing the color of any ActionMenuViews - icons that
                        //are not back button, nor text, nor overflow menu icon.
                        final View innerView = ((ActionMenuView) v).getChildAt(j);

                        if (innerView instanceof ActionMenuItemView) {
                            int drawablesCount = ((ActionMenuItemView) innerView)
                                    .getCompoundDrawables().length;
                            for (int k = 0; k < drawablesCount; k++) {
                                if (((ActionMenuItemView) innerView)
                                        .getCompoundDrawables()[k] != null) {
                                    final int finalK = k;

                                    //Important to set the color filter in seperate thread,
                                    //by adding it to the message queue
                                    //Won't work otherwise.
                                    innerView.post(() -> ((ActionMenuItemView) innerView)
                                            .getCompoundDrawables()[finalK]
                                            .setColorFilter(colorFilter));
                                }
                            }
                        }
                    }
                }

                //Step 3: Changing the color of title and subtitle.
                toolbarView.setTitleTextColor(toolbarIconsColor);
                toolbarView.setSubtitleTextColor(toolbarIconsColor);

                //Step 4: Changing the color of the Overflow Menu icon.
                setOverflowButtonColor(activity, colorFilter);
            }
        }

        /**
         * It's important to set overflowDescription attribute in styles, so we can grab the
         * reference to the overflow icon. Check: res/values/styles.xml
         *
         * @param activity    Activity where is called.
         * @param colorFilter Color to be set.
         */
        private static void setOverflowButtonColor(final Activity activity,
                                                   final PorterDuffColorFilter colorFilter) {
            @SuppressLint("PrivateResource")
            final String overflowDescription = activity
                    .getString(R.string.abc_action_menu_overflow_description);
            final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            final ArrayList<View> outViews = new ArrayList<>();
                            decorView.findViewsWithText(outViews, overflowDescription,
                                    View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                            if (outViews.isEmpty()) {
                                return;
                            }
                            ImageView overflow = (ImageView) outViews.get(0);
                            overflow.setColorFilter(colorFilter);
                            removeOnGlobalLayoutListener(decorView, this);
                        }
                    });
        }

        @SuppressWarnings("deprecation")
        private static void removeOnGlobalLayoutListener(View v,
                                                         ViewTreeObserver
                                                                 .OnGlobalLayoutListener listener) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
            } else {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            }
        }
    }

}
