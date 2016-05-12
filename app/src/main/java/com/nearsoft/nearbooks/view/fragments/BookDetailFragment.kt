package com.nearsoft.nearbooks.view.fragments

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.databinding.FragmentBookDetailBinding
import com.nearsoft.nearbooks.models.BookModel
import com.nearsoft.nearbooks.models.view.Book
import com.nearsoft.nearbooks.models.view.Borrow
import com.nearsoft.nearbooks.util.ErrorUtil
import com.nearsoft.nearbooks.util.ViewUtil
import com.nearsoft.nearbooks.view.activities.BaseActivity
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper
import com.nearsoft.nearbooks.view.helpers.SimpleTransitionListener
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse
import com.nearsoft.nearbooks.ws.responses.MessageResponse
import com.squareup.picasso.Picasso
import retrofit2.Response
import rx.Subscriber

class BookDetailFragment : BaseFragment() {

    companion object {

        // View name of the header image. Used for activity scene transitions
        const val VIEW_NAME_BOOK_COVER = "detail:book_cover:image"
        const val VIEW_NAME_BOOK_TOOLBAR = "detail:book_toolbar:toolbar"

        const val ARG_BOOK = "ARG_BOOK"

        const val ARG_COLORS_WRAPPER = "ARG_COLORS_WRAPPER"

        fun newInstance(book: Book, colorsWrapper: ColorsWrapper): BookDetailFragment {

            val args = Bundle()
            args.putParcelable(ARG_BOOK, book)
            args.putParcelable(ARG_COLORS_WRAPPER, colorsWrapper)

            val fragment = BookDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mBinding: FragmentBookDetailBinding

    private lateinit var mBook: Book
    private lateinit var mColorsWrapper: ColorsWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(ARG_BOOK) && arguments.containsKey(ARG_COLORS_WRAPPER)) {
            // Load the dummy title specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load title from a title provider.
            mBook = arguments.getParcelable<Book>(ARG_BOOK)
            mColorsWrapper = arguments.getParcelable<ColorsWrapper>(ARG_COLORS_WRAPPER)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentBookDetailBinding.inflate(inflater, container, false)
        mBinding.setBook(mBook)
        mBinding.setColors(mColorsWrapper)

        setupActionBar(mBinding.toolbar)

        mBinding.toolbar.post {
            val isLandscape = resources.getBoolean(R.bool.isLandscape)
            if (isLandscape && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                baseActivity.window?.statusBarColor = mColorsWrapper.backgroundColor
            }
            ViewUtil.Toolbar.colorizeToolbar(mBinding.toolbar,
                    mColorsWrapper.titleTextColor, baseActivity)
        }

        ViewCompat.setTransitionName(mBinding.imageViewBookCover, VIEW_NAME_BOOK_COVER)
        ViewCompat.setTransitionName(mBinding.toolbar, VIEW_NAME_BOOK_TOOLBAR)

        mBinding.fabRequestBook.setOnClickListener { view ->
            subscribeToFragment(BookModel.requestBookToBorrow(mLazyUser.get(),
                    mBook.id!! + "-0").subscribe(object : Subscriber<Response<com.nearsoft.nearbooks.models.realm.Borrow>>() {
                override fun onCompleted() {
                }

                override fun onError(t: Throwable) {
                    ViewUtil.showSnackbarMessage(mBinding, t.message!!)
                }

                override fun onNext(response: Response<com.nearsoft.nearbooks.models.realm.Borrow>) {
                    if (response.isSuccessful) {
                        val borrow = Borrow(response.body())
                        mBinding.setBorrow(borrow)
                        mBinding.executePendingBindings()
                        val status = borrow.status
                        if (status == Borrow.STATUS_REQUESTED) {
                            ViewUtil.showSnackbarMessage(mBinding,
                                    getString(R.string.message_book_requested))
                        } else if (status == Borrow.STATUS_ACTIVE) {
                            ViewUtil.showSnackbarMessage(mBinding,
                                    getString(R.string.message_book_active))
                        }
                        mBinding.fabRequestBook.hide()
                    } else {
                        val messageResponse = ErrorUtil.parseError(MessageResponse::class.java, response)
                        if (messageResponse != null) {
                            ViewUtil.showSnackbarMessage(mBinding,
                                    messageResponse.message!!)
                        } else {
                            ViewUtil.showSnackbarMessage(mBinding,
                                    ErrorUtil.getGeneralExceptionMessage(context,
                                            response.code())!!)
                        }
                    }
                }
            }))
        }

        Picasso.with(context)
                .load(getString(R.string.url_book_cover_thumbnail, mBook.id))
                .noPlaceholder().noFade().error(R.drawable.ic_launcher)
                .into(mBinding.imageViewBookCover)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || !addTransitionListener(baseActivity)) {
            loadFullResolution()
            checkBookAvailability()
        }

        return mBinding.root
    }

    private fun setupActionBar(toolbar: Toolbar) {
        // Show the Up button in the action bar.
        baseActivity.setSupportActionBar(toolbar)
        baseActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Try and add a [Transition.TransitionListener] to the entering shared element
     * [Transition]. We do this so that we can load the full-size image after the transition
     * has completed.

     * @return true if we were successful in adding a listener to the enter transition
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionListener(baseActivity: BaseActivity): Boolean {
        val transition = baseActivity.window.sharedElementEnterTransition

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(object : SimpleTransitionListener() {
                override fun onTransitionEnd(transition: Transition) {
                    // As the transition has ended, we can now load the full-size image
                    loadFullResolution()
                    checkBookAvailability()

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this)
                }

                override fun onTransitionCancel(transition: Transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this)
                }

            })
            return true
        }

        // If we reach here then we have not added a listener
        return false
    }

    private fun loadFullResolution() {
        Picasso.with(mBinding.imageViewBookCover.context)
                .load(getString(R.string.url_book_cover_full, mBook.id))
                .noFade()
                .noPlaceholder()
                .error(R.drawable.ic_launcher)
                .into(mBinding.imageViewBookCover)
    }

    private fun checkBookAvailability() {
        subscribeToFragment(BookModel.checkBookAvailability(mBook.id!!).subscribe(object : Subscriber<Response<AvailabilityResponse>>() {
            override fun onCompleted() {
            }

            override fun onError(t: Throwable) {
                ViewUtil.showSnackbarMessage(mBinding, t.message!!)
            }

            override fun onNext(response: Response<AvailabilityResponse>) {
                if (response.isSuccessful) {
                    val availabilityResponse = response.body()
                    val borrow = if (availabilityResponse.activeBorrow != null)
                        Borrow(availabilityResponse.activeBorrow)
                    else
                        null
                    mBinding.setBorrow(borrow)
                    mBinding.executePendingBindings()
                    if (availabilityResponse.isAvailable) {
                        mBinding.fabRequestBook.show()
                    } else {
                        mBinding.fabRequestBook.hide()
                    }
                }
            }
        }))
    }

}
