package com.nearsoft.nearbooks.util;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ViewBookManagerPasswordBinding;
import com.nearsoft.nearbooks.models.SharedPreferenceModel;
import com.nearsoft.nearbooks.view.helpers.SimpleTextWatcher;

/**
 * Repeat click action.
 * Created by epool on 2/3/16.
 */
public class TapsEasterEggHandler {

    private final Activity mActivity;
    private final View mView;
    private final String mPreferenceKey;
    private final int mEnableMessageRes;
    private final int mCounterMessageRes;
    private final int mTapsToStartShowingCountToastInfo;
    private final int mTapsToHandleAction;
    private final int mDelayMillisToResetTapsCounter;
    private final String mPassword;
    private final Runnable mActionToHandle;
    private final ViewBookManagerPasswordBinding mBinding;
    private final AlertDialog mAlertDialog;

    private int mTapsCounter;
    private Handler mHandler = new Handler();
    private Runnable mResetTapsCounterRunnable = () -> mTapsCounter = 0;
    private Toast mEasterEggToast;

    private TapsEasterEggHandler(@NonNull Builder builder) {
        mActivity = builder.mActivity;
        mView = builder.mView;
        mPreferenceKey = builder.mPreferenceKey;
        mEnableMessageRes = builder.mEnableMessageRes;
        mCounterMessageRes = builder.mCounterMessageRes;
        mTapsToStartShowingCountToastInfo = builder.mTapsToStartShowingCountToastInfo;
        mTapsToHandleAction = builder.mTapsToHandleAction;
        mDelayMillisToResetTapsCounter = builder.mDelayMillisToResetTapsCounter;
        mPassword = builder.mPassword;
        mActionToHandle = builder.mActionToHandle;

        mBinding = ViewBookManagerPasswordBinding.inflate(mActivity.getLayoutInflater());

        mAlertDialog = new AlertDialog.Builder(mActivity)
                .setTitle(R.string.title_activation_password)
                .setView(mBinding.getRoot())
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        setupAlertDialog();

        setupEditText();
    }

    public static Builder with(@NonNull Activity activity, @NonNull View viewForInteraction,
                               @NonNull String preferenceKey) {
        return new Builder(activity, viewForInteraction, preferenceKey);
    }

    private boolean isActionHandled() {
        return SharedPreferenceModel.INSTANCE.getBoolean(mPreferenceKey);
    }

    private void markActionAsHandled() {
        SharedPreferenceModel.INSTANCE.putBoolean(mPreferenceKey, true);
    }

    private void setupAlertDialog() {
        mAlertDialog.setOnShowListener(dialog -> {
            Button button = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> tryPassword());
        });
    }

    private void setupEditText() {
        mBinding.etBookManagerPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tryPassword();
                return true;
            }
            return false;
        });
        mBinding.etBookManagerPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.tilBookManagerPassword.getError() != null) {
                    mBinding.tilBookManagerPassword.setError(null);
                }
            }
        });
    }

    private void tryPassword() {
        mBinding.tilBookManagerPassword.setError(null);

        Editable editablePassword = mBinding.etBookManagerPassword.getText();

        if (TextUtils.isEmpty(editablePassword)) return;

        if (editablePassword.toString().equals(mPassword)) {
            handleActionWithMessage();
            mAlertDialog.dismiss();
        } else {
            mBinding.tilBookManagerPassword
                    .setError(mActivity.getString(R.string.message_wrong_password));
        }
    }

    public void setupHandler() {
        if (isActionHandled()) {
            handleAction();
            return;
        }

        mView.setOnClickListener(v -> {
            if (mEasterEggToast != null) mEasterEggToast.cancel();
            if (++mTapsCounter == mTapsToHandleAction) {
                if (mPassword == null) {
                    handleActionWithMessage();
                } else {
                    mAlertDialog.show();
                }
            } else if (mTapsToStartShowingCountToastInfo < mTapsCounter &&
                    mTapsCounter < mTapsToHandleAction &&
                    mCounterMessageRes != 0) {
                mEasterEggToast = ViewUtil.showToastMessage(
                        mActivity,
                        mCounterMessageRes,
                        mTapsToHandleAction - mTapsCounter
                );
            }
            mHandler.removeCallbacks(mResetTapsCounterRunnable);
            mHandler.postDelayed(mResetTapsCounterRunnable, mDelayMillisToResetTapsCounter);
        });
    }

    private void handleActionWithMessage() {
        handleAction();
        if (mEnableMessageRes != 0) {
            ViewUtil.showToastMessage(mActivity, mEnableMessageRes);
        }
    }

    private void handleAction() {
        if (mActionToHandle != null) mActionToHandle.run();
        markActionAsHandled();
        mView.setOnClickListener(null);
    }

    public static class Builder {

        private static final int TAPS_TO_START_SHOWING_COUNT_TOAST_INFO = 3;
        private static final int TAPS_TO_HANDLE_ACTION = 7;
        private static final int DELAY_MILLIS_TO_RESET_TAPS_COUNTER = 1000;

        private final Activity mActivity;
        private final View mView;
        private final String mPreferenceKey;
        private int mEnableMessageRes;
        private int mCounterMessageRes;
        private int mTapsToStartShowingCountToastInfo = TAPS_TO_START_SHOWING_COUNT_TOAST_INFO;
        private int mTapsToHandleAction = TAPS_TO_HANDLE_ACTION;
        private int mDelayMillisToResetTapsCounter = DELAY_MILLIS_TO_RESET_TAPS_COUNTER;
        private String mPassword;
        private Runnable mActionToHandle;

        private Builder(@NonNull Activity activity, @NonNull View viewForInteraction,
                        @NonNull String preferenceKey) {
            mActivity = activity;
            mView = viewForInteraction;
            mPreferenceKey = preferenceKey;
        }

        public Builder enableMessageRes(int enableMessageRes) {
            mEnableMessageRes = enableMessageRes;
            return this;
        }

        public Builder counterMessageRes(int counterMessageRes) {
            mCounterMessageRes = counterMessageRes;
            return this;
        }

        public Builder tapsToStartShowingCountToastInfo(int tapsToStartShowingCountToastInfo) {
            mTapsToStartShowingCountToastInfo = tapsToStartShowingCountToastInfo;
            return this;
        }

        public Builder tapsToHandleAction(int tapsToHandleAction) {
            mTapsToHandleAction = tapsToHandleAction;
            return this;
        }

        public Builder delayMillisToResetTapsCounter(int delayMillisToResetTapsCounter) {
            mDelayMillisToResetTapsCounter = delayMillisToResetTapsCounter;
            return this;
        }

        public Builder password(String password) {
            mPassword = password;
            return this;
        }

        public Builder actionToHandle(Runnable actionToHandle) {
            mActionToHandle = actionToHandle;
            return this;
        }

        public void build() {
            new TapsEasterEggHandler(this).setupHandler();
        }

    }

}
