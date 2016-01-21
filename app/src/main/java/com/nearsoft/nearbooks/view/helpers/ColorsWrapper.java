package com.nearsoft.nearbooks.view.helpers;

import android.support.v7.graphics.Palette;

/**
 * Wrapper to put the colors extracted from a image with the Palette api.
 * Created by epool on 1/13/16.
 */
public class ColorsWrapper {

    private final int mStatusBarColor;

    private final int mBackgroundColor;

    private final int mTitleTextColor;

    private final int mBodyTextColor;

    public ColorsWrapper(int backgroundColor, Palette.Swatch swatch) {
        mBackgroundColor = backgroundColor;
        mStatusBarColor = swatch.getTitleTextColor();
        mTitleTextColor = swatch.getTitleTextColor();
        mBodyTextColor = swatch.getBodyTextColor();
    }

    public ColorsWrapper(int statusBarColor, int backgroundColor, int titleTextColor,
                         int bodyTextColor) {
        mStatusBarColor = statusBarColor;
        mBackgroundColor = backgroundColor;
        mTitleTextColor = titleTextColor;
        mBodyTextColor = bodyTextColor;
    }

    public int getStatusBarColor() {
        return mStatusBarColor;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    public int getBodyTextColor() {
        return mBodyTextColor;
    }
}
