package com.nearsoft.nearbooks.view.fragments


import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.integration.android.IntentIntegrator
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.databinding.FragmentRegisterBookBinding
import com.nearsoft.nearbooks.databinding.ItemGoogleBooksVolumeBinding
import com.nearsoft.nearbooks.models.BookModel
import com.nearsoft.nearbooks.models.GoogleBooksModel
import com.nearsoft.nearbooks.util.ErrorUtil
import com.nearsoft.nearbooks.util.ViewUtil
import com.nearsoft.nearbooks.view.activities.zxing.CaptureActivityAnyOrientation
import com.nearsoft.nearbooks.view.adapters.GoogleBooksVolumeAdapter
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody
import com.nearsoft.nearbooks.ws.responses.MessageResponse
import rx.Subscriber

class RegisterBookFragment : BaseFragment(), GoogleBooksVolumeAdapter.OnGoogleBookItemClickListener {

    companion object {

        private val KEY_CODE = "KEY_CODE"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment RegisterBookFragment.
         */
        fun newInstance(): RegisterBookFragment {
            val fragment = RegisterBookFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mBinding: FragmentRegisterBookBinding
    private lateinit var mGoogleBooksVolumeAdapter: GoogleBooksVolumeAdapter
    private var mCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGoogleBooksVolumeAdapter = GoogleBooksVolumeAdapter(this)
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_CODE)) {
            mCode = savedInstanceState.getString(KEY_CODE)
            findBooksByIsbn()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_book, container, false)
        mBinding.fabScanQrCode.setOnClickListener { v -> startQRScanner() }

        mBinding.recyclerViewBooks.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        mBinding.recyclerViewBooks.layoutManager = layoutManager
        mBinding.recyclerViewBooks.adapter = mGoogleBooksVolumeAdapter

        return mBinding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_CODE, mCode)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null && scanResult.contents != null) {
            mCode = scanResult.contents
            findBooksByIsbn()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun findBooksByIsbn() {
        val progressDialog = ProgressDialog.show(context, getString(R.string.message_getting_book_info), null, true)
        subscribeToFragment(GoogleBooksModel.findGoogleBooksByIsbn(mCode!!).subscribe(object : Subscriber<List<GoogleBookBody>>() {
            override fun onCompleted() {
            }

            override fun onError(t: Throwable) {
                progressDialog.dismiss()
                ViewUtil.showSnackbarMessage(mBinding, ErrorUtil.getMessageFromThrowable(t, context)!!)
            }

            override fun onNext(googleBookBodies: List<GoogleBookBody>) {
                progressDialog.dismiss()
                mGoogleBooksVolumeAdapter.setGoogleBookBodies(googleBookBodies)
                showResults(!googleBookBodies.isEmpty())
            }
        }))
    }

    private fun showResults(showResults: Boolean) {
        if (showResults) {
            mBinding.recyclerViewBooks.visibility = View.VISIBLE
            mBinding.tvEmpty.visibility = View.GONE
        } else {
            mBinding.recyclerViewBooks.visibility = View.GONE
            mBinding.tvEmpty.visibility = View.VISIBLE
        }
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.captureActivity = CaptureActivityAnyOrientation::class.java
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setPrompt(getString(R.string.message_scan_book_isbn_bar_code))
        integrator.setCameraId(0)  // Use a specific camera of the device
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    override fun onGoogleBookItemClicked(binding: ItemGoogleBooksVolumeBinding) {
        val googleBookBody = binding.book
        AlertDialog.Builder(context).setTitle(R.string.question_register_new_book).setMessage(
                getString(R.string.message_book_resume, googleBookBody.title,
                        googleBookBody.authors, googleBookBody.publishedDate)).setPositiveButton(android.R.string.ok) { dialog, which ->
            subscribeToFragment(BookModel.registerNewBook(googleBookBody).doOnError { t ->
                ViewUtil.showSnackbarMessage(mBinding,
                        t.message!!)
            }.subscribe { response ->
                if (response.isSuccessful) {
                    ViewUtil.showToastMessage(context, R.string.message_done)
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
            })
        }.setNegativeButton(android.R.string.cancel, null).show()
    }

}
