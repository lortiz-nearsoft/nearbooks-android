package com.nearsoft.nearbooks.exceptions

import android.support.annotation.StringRes

/**
 * Exception for sign in errors.
 * Created by epool on 1/4/16.
 */
class SignInException(
        detailMessage: String,
        @StringRes resourceId: Int,
        vararg formatArgs: Any
) : NearbooksException(detailMessage, resourceId, formatArgs)
