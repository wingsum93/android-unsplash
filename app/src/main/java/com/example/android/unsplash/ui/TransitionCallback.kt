package com.example.android.unsplash.ui

import android.transition.Transition

/**
 * Dummy implementations of TransitionListener methods.
 */
abstract class TransitionCallback : Transition.TransitionListener {

    override fun onTransitionStart(transition: Transition) {
        // no-op
    }

    override fun onTransitionEnd(transition: Transition) {
        // no-op
    }

    override fun onTransitionCancel(transition: Transition) {
        // no-op
    }

    override fun onTransitionPause(transition: Transition) {
        // no-op
    }

    override fun onTransitionResume(transition: Transition) {
        // no-op
    }
}
