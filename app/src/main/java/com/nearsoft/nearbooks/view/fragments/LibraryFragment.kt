package com.nearsoft.nearbooks.view.fragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.google.zxing.integration.android.IntentIntegrator
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.databinding.BookItemBinding
import com.nearsoft.nearbooks.databinding.FragmentLibraryBinding
import com.nearsoft.nearbooks.models.BookModel
import com.nearsoft.nearbooks.models.view.Book
import com.nearsoft.nearbooks.models.view.Borrow
import com.nearsoft.nearbooks.sync.SyncChangeHandler
import com.nearsoft.nearbooks.util.ErrorUtil
import com.nearsoft.nearbooks.util.SyncUtil
import com.nearsoft.nearbooks.util.ViewUtil
import com.nearsoft.nearbooks.view.activities.BaseActivity
import com.nearsoft.nearbooks.view.activities.BookDetailActivity
import com.nearsoft.nearbooks.view.activities.zxing.CaptureActivityAnyOrientation
import com.nearsoft.nearbooks.view.adapters.BookRecyclerViewCursorAdapter
import com.nearsoft.nearbooks.view.adapters.listeners.OnBookItemClickListener
import com.nearsoft.nearbooks.view.helpers.SpacingDecoration
import com.nearsoft.nearbooks.ws.responses.MessageResponse
import io.realm.Realm
import io.realm.RealmChangeListener
import retrofit2.Response
import rx.Subscriber

class LibraryFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, SyncChangeHandler.OnSyncChangeListener, BaseActivity.OnSearchListener, RealmChangeListener<Realm>, OnBookItemClickListener {

    companion object {

        private val ACTION_REQUEST = 0
        private val ACTION_CHECK_IN = 1
        private val ACTION_CHECK_OUT = 2

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment LibraryFragment.
         */
        fun newInstance(): LibraryFragment {
            val fragment = LibraryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mBinding: FragmentLibraryBinding
    private lateinit var mSearchView: SearchView
    private lateinit var mRealm: Realm
    private val mBookRecyclerViewCursorAdapter: BookRecyclerViewCursorAdapter by lazy {
        BookRecyclerViewCursorAdapter(context, mRealm, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false)

        mBinding.fab.setOnClickListener { view -> startQRScanner() }

        mBinding.recyclerViewBooks.setHasFixedSize(true)
        val isLandscape = resources.getBoolean(R.bool.isLandscape)
        val isTablet = resources.getBoolean(R.bool.isTable)
        val layoutManager = GridLayoutManager(context,
                if (isTablet) if (isLandscape) 6 else 4 else if (isLandscape) 4 else 2)
        mBinding.recyclerViewBooks.layoutManager = layoutManager
        val margin = resources.getDimensionPixelSize(R.dimen.books_margin)
        mBinding.recyclerViewBooks.addItemDecoration(SpacingDecoration(margin, margin, true))
        mBinding.recyclerViewBooks.adapter = mBookRecyclerViewCursorAdapter

        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mBinding.textViewEmpty.setOnClickListener { v -> onRefresh() }

        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null && scanResult.contents != null) {
            val qrCode = scanResult.contents
            showBookActions(qrCode)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        baseActivity.syncChangeHandler.addOnSyncChangeListener(this)
    }

    override fun onDetach() {
        super.onDetach()

        baseActivity.syncChangeHandler.removeOnSyncChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        mRealm.removeChangeListener(this)
        mRealm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        // Inflate the options menu from XML
        inflater!!.inflate(R.menu.menu_library, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu!!.findItem(R.id.menu_search)
        mSearchView = searchMenuItem.actionView as SearchView
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(baseActivity.componentName))
        mSearchView.setIconifiedByDefault(true)
        mSearchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                mSearchView.isIconified = true
                mSearchView.setQuery("", false)
            }
        }
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mBookRecyclerViewCursorAdapter.filterByQuery(query)
                updateUI()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mBookRecyclerViewCursorAdapter.filterByQuery(newText)
                updateUI()
                return false
            }
        })
    }

    override fun onRefresh() {
        val user = mLazyUser.get()
        if (!SyncUtil.isSyncing(user)) {
            SyncUtil.triggerRefresh(user)
        }
    }

    override fun onSyncChange(isSyncing: Boolean) {
        mBinding.swipeRefreshLayout.post {
            if (!mBinding.swipeRefreshLayout.isRefreshing && isSyncing) {
                mBinding.swipeRefreshLayout.isRefreshing = true
            } else if (mBinding.swipeRefreshLayout.isRefreshing && !isSyncing) {
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
            updateUI()
        }
    }

    private fun updateUI() {
        if (mBookRecyclerViewCursorAdapter.itemCount == 0) {
            mBinding.recyclerViewBooks.visibility = View.GONE
            mBinding.textViewEmpty.visibility = View.VISIBLE
        } else {
            mBinding.recyclerViewBooks.visibility = View.VISIBLE
            mBinding.textViewEmpty.visibility = View.GONE
        }
    }

    override fun onSearchRequest(query: String) {
        mSearchView.setQuery(query, false)
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.captureActivity = CaptureActivityAnyOrientation::class.java
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setPrompt(getString(R.string.message_scan_book_qr_code))
        integrator.setCameraId(0)  // Use a specific camera of the device
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    private fun showBookActions(qrCode: String) {
        val qrCodeParts = qrCode.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (qrCodeParts.size == 2) {
            val realmBooks = BookModel.findByBookId(qrCodeParts[0])
            if (realmBooks != null) {
                val book = Book(realmBooks)
                AlertDialog.Builder(context).setTitle(book.title).setItems(R.array.actions_book) { dialog, which ->
                    when (which) {
                        ACTION_REQUEST -> subscribeToFragment(BookModel.requestBookToBorrow(
                                mLazyUser.get(), qrCode).subscribe(object : Subscriber<Response<com.nearsoft.nearbooks.models.realm.Borrow>>() {
                            override fun onCompleted() {
                            }

                            override fun onError(t: Throwable) {
                                ViewUtil.showSnackbarMessage(mBinding,
                                        t.message!!)
                            }

                            override fun onNext(response: Response<com.nearsoft.nearbooks.models.realm.Borrow>) {
                                if (response.isSuccessful) {
                                    val borrow = Borrow(response.body())
                                    val status = borrow.status
                                    if (status == Borrow.STATUS_REQUESTED) {
                                        ViewUtil.showSnackbarMessage(
                                                mBinding,
                                                getString(R.string.message_book_requested))
                                    } else if (status == Borrow.STATUS_ACTIVE) {
                                        ViewUtil.showSnackbarMessage(
                                                mBinding,
                                                getString(R.string.message_book_active))
                                    }
                                } else {
                                    val messageResponse = ErrorUtil.parseError(MessageResponse::class.java, response)
                                    if (messageResponse != null) {
                                        ViewUtil.showSnackbarMessage(
                                                mBinding,
                                                messageResponse.message!!)
                                    } else {
                                        ViewUtil.showSnackbarMessage(
                                                mBinding,
                                                ErrorUtil.getGeneralExceptionMessage(context,
                                                        response.code())!!)
                                    }
                                }
                            }
                        }))
                        ACTION_CHECK_IN -> subscribeToFragment(BookModel.doBookCheckIn(mBinding,
                                mLazyUser.get(), qrCode))
                        ACTION_CHECK_OUT -> subscribeToFragment(BookModel.doBookCheckOut(mBinding,
                                mLazyUser.get(), qrCode))
                    }
                }.show()
            } else {
                Snackbar.make(
                        mBinding.root,
                        resources.getQuantityString(R.plurals.message_books_not_found, 1),
                        Snackbar.LENGTH_LONG).show()
            }
        } else {
            Snackbar.make(
                    mBinding.root,
                    getString(R.string.error_invalid_qr_code),
                    Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onChange(realm: Realm) {
        mBookRecyclerViewCursorAdapter.notifyDataSetChanged()
        updateUI()
    }

    override fun onBookItemClicked(binding: BookItemBinding) {
        BookDetailActivity.openWith(baseActivity, binding);
    }

}
