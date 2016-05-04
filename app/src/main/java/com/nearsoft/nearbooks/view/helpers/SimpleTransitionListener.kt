package com.nearsoft.nearbooks.view.helpers

import android.annotation.TargetApi
import android.os.Build
import android.transition.Transition

/**
 * Simple implementation of [Transition.TransitionListener] to implement just the necessary methods.
 * Created by epool on 1/5/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
abstract class SimpleTransitionListener : Transition.TransitionListener {

    override fun onTransitionStart(transition: Transition) {
    }

    override fun onTransitionEnd(transition: Transition) {
    }

    override fun onTransitionCancel(transition: Transition) {
    }

    override fun onTransitionPause(transition: Transition) {
    }

    override fun onTransitionResume(transition: Transition) {
    }

}
