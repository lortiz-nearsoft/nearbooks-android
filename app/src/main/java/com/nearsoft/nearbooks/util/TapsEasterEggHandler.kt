package com.nearsoft.nearbooks.util

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.databinding.ViewBookManagerPasswordBinding
import com.nearsoft.nearbooks.models.SharedPreferenceModel
import com.nearsoft.nearbooks.view.helpers.SimpleTextWatcher

/**
 * Repeat click action.
 * Created by epool on 2/3/16.
 */
class TapsEasterEggHandler private constructor(builder: TapsEasterEggHandler.Builder) {

    private val mActivity: Activity
    private val mView: View
    private val mPreferenceKey: String
    private val mEnableMessageRes: Int
    private val mCounterMessageRes: Int
    private val mTapsToStartShowingCountToastInfo: Int
    private val mTapsToHandleAction: Int
    private val mDelayMillisToResetTapsCounter: Int
    private val mPassword: String?
    private val mActionToHandle: Runnable?
    private val mBinding: ViewBookManagerPasswordBinding
    private val mAlertDialog: AlertDialog

    private var mTapsCounter: Int = 0
    private val mHandler = Handler()
    private val mResetTapsCounterRunnable = Runnable { mTapsCounter = 0 }
    private var mEasterEggToast: Toast? = null

    init {
        mActivity = builder.mActivity
        mView = builder.mView
        mPreferenceKey = builder.mPreferenceKey
        mEnableMessageRes = builder.mEnableMessageRes
        mCounterMessageRes = builder.mCounterMessageRes
        mTapsToStartShowingCountToastInfo = builder.mTapsToStartShowingCountToastInfo
        mTapsToHandleAction = builder.mTapsToHandleAction
        mDelayMillisToResetTapsCounter = builder.mDelayMillisToResetTapsCounter
        mPassword = builder.mPassword
        mActionToHandle = builder.mActionToHandle

        mBinding = DataBindingUtil.inflate(mActivity.layoutInflater, R.layout.view_book_manager_password, null, false)

        mAlertDialog = AlertDialog
                .Builder(mActivity)
                .setTitle(R.string.title_activation_password)
                .setView(mBinding.root)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()

        setupAlertDialog()

        setupEditText()
    }

    private val isActionHandled: Boolean
        get() = SharedPreferenceModel.getBoolean(mPreferenceKey)

    private fun markActionAsHandled() {
        SharedPreferenceModel.putBoolean(mPreferenceKey, true)
    }

    private fun setupAlertDialog() {
        mAlertDialog.setOnShowListener {
            val button = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener { tryPassword() }
        }
    }

    private fun setupEditText() {
        mBinding.etBookManagerPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tryPassword()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        mBinding.etBookManagerPassword.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (mBinding.tilBookManagerPassword.error != null) {
                    mBinding.tilBookManagerPassword.error = null
                }
            }
        })
    }

    private fun tryPassword() {
        mBinding.tilBookManagerPassword.error = null

        val editablePassword = mBinding.etBookManagerPassword.text

        if (TextUtils.isEmpty(editablePassword)) return

        if (editablePassword.toString() == mPassword) {
            handleActionWithMessage()
            mAlertDialog.dismiss()
        } else {
            mBinding.tilBookManagerPassword.error = mActivity.getString(R.string.message_wrong_password)
        }
    }

    fun setupHandler() {
        if (isActionHandled) {
            handleAction()
            return
        }

        mView.setOnClickListener { v ->
            if (mEasterEggToast != null) mEasterEggToast!!.cancel()
            if (++mTapsCounter == mTapsToHandleAction) {
                if (mPassword == null) {
                    handleActionWithMessage()
                } else {
                    mAlertDialog.show()
                }
            } else if (mTapsToStartShowingCountToastInfo < mTapsCounter &&
                    mTapsCounter < mTapsToHandleAction &&
                    mCounterMessageRes != 0) {
                mEasterEggToast = ViewUtil.showToastMessage(
                        mActivity,
                        mCounterMessageRes,
                        mTapsToHandleAction - mTapsCounter
                )
            }
            mHandler.removeCallbacks(mResetTapsCounterRunnable)
            mHandler.postDelayed(mResetTapsCounterRunnable, mDelayMillisToResetTapsCounter.toLong())
        }
    }

    private fun handleActionWithMessage() {
        handleAction()
        if (mEnableMessageRes != 0) {
            ViewUtil.showToastMessage(mActivity, mEnableMessageRes)
        }
    }

    private fun handleAction() {
        mActionToHandle?.run()
        markActionAsHandled()
        mView.setOnClickListener(null)
    }

    companion object {

        fun with(activity: Activity, viewForInteraction: View,
                 preferenceKey: String): Builder {
            return Builder(activity, viewForInteraction, preferenceKey)
        }
    }

    class Builder constructor(val mActivity: Activity, val mView: View, val mPreferenceKey: String) {

        companion object {
            private const val TAPS_TO_START_SHOWING_COUNT_TOAST_INFO = 3
            private const val TAPS_TO_HANDLE_ACTION = 7
            private const val DELAY_MILLIS_TO_RESET_TAPS_COUNTER = 1000
        }

        var mEnableMessageRes: Int = 0
        var mCounterMessageRes: Int = 0
        var mTapsToStartShowingCountToastInfo = TAPS_TO_START_SHOWING_COUNT_TOAST_INFO
        var mTapsToHandleAction = TAPS_TO_HANDLE_ACTION
        var mDelayMillisToResetTapsCounter = DELAY_MILLIS_TO_RESET_TAPS_COUNTER
        var mPassword: String? = null
        var mActionToHandle: Runnable? = null

        fun enableMessageRes(enableMessageRes: Int): Builder {
            mEnableMessageRes = enableMessageRes
            return this
        }

        fun counterMessageRes(counterMessageRes: Int): Builder {
            mCounterMessageRes = counterMessageRes
            return this
        }

        fun tapsToStartShowingCountToastInfo(tapsToStartShowingCountToastInfo: Int): Builder {
            mTapsToStartShowingCountToastInfo = tapsToStartShowingCountToastInfo
            return this
        }

        fun tapsToHandleAction(tapsToHandleAction: Int): Builder {
            mTapsToHandleAction = tapsToHandleAction
            return this
        }

        fun delayMillisToResetTapsCounter(delayMillisToResetTapsCounter: Int): Builder {
            mDelayMillisToResetTapsCounter = delayMillisToResetTapsCounter
            return this
        }

        fun password(password: String): Builder {
            mPassword = password
            return this
        }

        fun actionToHandle(actionToHandle: Runnable): Builder {
            mActionToHandle = actionToHandle
            return this
        }

        fun build() {
            TapsEasterEggHandler(this).setupHandler()
        }

    }

}
