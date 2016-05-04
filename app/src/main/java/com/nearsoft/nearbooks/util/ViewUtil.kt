package com.nearsoft.nearbooks.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.databinding.ViewDataBinding
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.ActionMenuView
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.*

/**
 * Utilities related with views.
 * Created by epool on 1/8/16.
 */
object ViewUtil {

    fun loadBitmapFromUrlImage(context: Context,
                               url: String): Observable<Bitmap> {
        return Observable.create { subscriber ->
            try {
                val bitmap = Picasso.with(context).load(url).get()
                subscriber.onNext(bitmap)
                subscriber.onCompleted()
            } catch (e: IOException) {
                e.printStackTrace()
                subscriber.onError(e)
            }
        }
    }

    fun getPaletteFromUrlImage(context: Context, url: String): Observable<Palette> {
        return loadBitmapFromUrlImage(context, url).map { Palette.from(it).generate() }
    }

    fun getColorsWrapperFromUrlImage(context: Context, url: String): Observable<ColorsWrapper> {
        return getPaletteFromUrlImage(context, url).map {
            val defaultColor = ContextCompat.getColor(context, R.color.colorPrimary)
            ViewUtil.getVibrantPriorityColorSwatchPair(it, defaultColor)
        }
    }

    fun loadImageFromUrl(imageView: ImageView,
                         url: String): Observable<ColorsWrapper> {
        val context = imageView.context
        return Observable.create { subscriber ->
            val requestCreator = Picasso.with(context).load(url)
            requestCreator.placeholder(R.drawable.ic_launcher)
            requestCreator.error(R.drawable.ic_launcher)
            requestCreator.into(imageView, object : Callback {
                override fun onSuccess() {
                    if (subscriber.isUnsubscribed) return

                    ViewUtil.getColorsWrapperFromUrlImage(context, url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { colorsWrapper ->
                        subscriber.onNext(colorsWrapper)
                        subscriber.onCompleted()
                    }
                }

                override fun onError() {
                    subscriber.onCompleted()
                }
            })
        }
    }

    fun getVibrantPriorityColorSwatchPair(
            palette: Palette?, defaultColor: Int): ColorsWrapper? {
        if (palette == null) return null

        if (palette.vibrantSwatch != null) {
            return ColorsWrapper(palette.getVibrantColor(defaultColor),
                    palette.vibrantSwatch!!)
        } else if (palette.lightVibrantSwatch != null) {
            return ColorsWrapper(palette.getLightVibrantColor(defaultColor),
                    palette.lightVibrantSwatch!!)
        } else if (palette.darkVibrantSwatch != null) {
            return ColorsWrapper(palette.getDarkVibrantColor(defaultColor),
                    palette.darkVibrantSwatch!!)
        } else if (palette.mutedSwatch != null) {
            return ColorsWrapper(palette.getMutedColor(defaultColor),
                    palette.mutedSwatch!!)
        } else if (palette.lightMutedSwatch != null) {
            return ColorsWrapper(palette.getLightMutedColor(defaultColor),
                    palette.lightMutedSwatch!!)
        } else if (palette.darkMutedSwatch != null) {
            return ColorsWrapper(palette.getDarkMutedColor(defaultColor),
                    palette.darkMutedSwatch!!)
        }
        return null
    }

    fun showSnackbarMessage(viewDataBinding: ViewDataBinding, message: String): Snackbar {
        val snackbar = Snackbar.make(viewDataBinding.root, message, Snackbar.LENGTH_LONG)
        snackbar.show()
        return snackbar
    }

    fun showToastMessage(context: Context, message: String): Toast {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.show()
        return toast
    }

    fun showToastMessage(context: Context, messageRes: Int, vararg formatArgs: Any): Toast {
        val toast = Toast.makeText(context, context.getString(messageRes, *formatArgs), Toast.LENGTH_LONG)
        toast.show()
        return toast
    }

    object Toolbar {
        /**
         * Use this method to colorize toolbar icons to the desired target color

         * @param toolbarView       toolbar view being colored
         * *
         * @param toolbarIconsColor the target color of toolbar icons
         * *
         * @param activity          reference to activity needed to register observers
         */
        fun colorizeToolbar(toolbarView: android.support.v7.widget.Toolbar,
                            toolbarIconsColor: Int, activity: Activity) {
            val colorFilter = PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.MULTIPLY)

            for (i in 0..toolbarView.childCount - 1) {
                val v = toolbarView.getChildAt(i)

                //Step 1 : Changing the color of back button (or open drawer button).
                if (v is ImageButton) {
                    //Action Bar back button
                    v.drawable.colorFilter = colorFilter
                }

                if (v is ActionMenuView) {
                    for (j in 0..v.childCount - 1) {

                        //Step 2: Changing the color of any ActionMenuViews - icons that
                        //are not back button, nor text, nor overflow menu icon.
                        val innerView = v.getChildAt(j)

                        if (innerView is ActionMenuItemView) {
                            val drawablesCount = innerView.compoundDrawables.size
                            for (k in 0..drawablesCount - 1) {
                                if (innerView.compoundDrawables[k] != null) {
                                    val finalK = k

                                    //Important to set the color filter in seperate thread,
                                    //by adding it to the message queue
                                    //Won't work otherwise.
                                    innerView.post { innerView.compoundDrawables[finalK].colorFilter = colorFilter }
                                }
                            }
                        }
                    }
                }

                //Step 3: Changing the color of title and subtitle.
                toolbarView.setTitleTextColor(toolbarIconsColor)
                toolbarView.setSubtitleTextColor(toolbarIconsColor)

                //Step 4: Changing the color of the Overflow Menu icon.
                setOverflowButtonColor(activity, colorFilter)
            }
        }

        /**
         * It's important to set overflowDescription attribute in styles, so we can grab the
         * reference to the overflow icon. Check: res/values/styles.xml

         * @param activity    Activity where is called.
         * *
         * @param colorFilter Color to be set.
         */
        private fun setOverflowButtonColor(activity: Activity,
                                           colorFilter: PorterDuffColorFilter) {
            @SuppressLint("PrivateResource")
            val overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description)
            val decorView = activity.window.decorView as ViewGroup
            val viewTreeObserver = decorView.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            val outViews = ArrayList<View>()
                            decorView.findViewsWithText(outViews, overflowDescription,
                                    View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
                            if (outViews.isEmpty()) return
                            val overflow = outViews[0] as ImageView
                            overflow.colorFilter = colorFilter
                            removeOnGlobalLayoutListener(decorView, this)
                        }
                    })
        }

        @SuppressWarnings("deprecation")
        private fun removeOnGlobalLayoutListener(v: View,
                                                 listener: ViewTreeObserver.OnGlobalLayoutListener) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                v.viewTreeObserver.removeGlobalOnLayoutListener(listener)
            } else {
                v.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }
    }

}
