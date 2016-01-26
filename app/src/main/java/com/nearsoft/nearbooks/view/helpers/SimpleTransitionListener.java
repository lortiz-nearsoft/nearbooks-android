package com.nearsoft.nearbooks.view.helpers;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.Transition;

/**
 * Simple implementation of {@link Transition.TransitionListener} to implement just the necessary methods.
 * Created by epool on 1/5/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public abstract class SimpleTransitionListener implements Transition.TransitionListener {

    @Override
    public void onTransitionStart(Transition transition) {
    }

    @Override
    public void onTransitionEnd(Transition transition) {
    }

    @Override
    public void onTransitionCancel(Transition transition) {
    }

    @Override
    public void onTransitionPause(Transition transition) {
    }

    @Override
    public void onTransitionResume(Transition transition) {
    }

}
