package com.nearsoft.nearbooks.view.helpers;

import android.support.v7.graphics.Palette;

/**
 * Wrapper to put the colors extracted from a image with the Palette api.
 * Created by epool on 1/13/16.
 */
public class ColorsWrapper {

    private final int statusBarColor;

    private final int backgroundColor;

    private final int titleTextColor;

    private final int bodyTextColor;

    public ColorsWrapper(int backgroundColor, Palette.Swatch swatch) {
        this.backgroundColor = backgroundColor;
        this.statusBarColor = swatch.getTitleTextColor();
        this.titleTextColor = swatch.getTitleTextColor();
        this.bodyTextColor = swatch.getBodyTextColor();
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public int getBodyTextColor() {
        return bodyTextColor;
    }
}
