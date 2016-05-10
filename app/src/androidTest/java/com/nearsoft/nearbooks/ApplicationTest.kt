package com.nearsoft.nearbooks

import android.os.SystemClock
import android.test.ApplicationTestCase
import com.nearsoft.nearbooks.models.realm.Book
import com.nearsoft.nearbooks.models.realm.Borrow
import com.nearsoft.nearbooks.util.ErrorUtil
import com.nearsoft.nearbooks.ws.BookService
import com.nearsoft.nearbooks.ws.GoogleBooksService
import com.nearsoft.nearbooks.ws.bodies.RequestBody
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse
import com.nearsoft.nearbooks.ws.responses.MessageResponse
import com.nearsoft.nearbooks.ws.responses.googlebooks.GoogleBooksSearchResponse
import io.realm.Realm
import retrofit2.Response
import rx.observers.TestSubscriber
import java.io.IOException

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
class ApplicationTest : ApplicationTestCase<NearbooksApplication>(NearbooksApplication::class.java) {

    private var mBookService: BookService? = null
    private var mGoogleBooksService: GoogleBooksService? = null

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        application
        SystemClock.sleep(100)
        val nearbooksApplicationComponent = NearbooksApplication.applicationComponent
        mBookService = nearbooksApplicationComponent.provideBookService()
        mGoogleBooksService = nearbooksApplicationComponent.provideGoogleBooksService()
    }

    @Throws(IOException::class)
    fun testBookService() {
        val observable = mBookService!!.getAllBooksRx()

        val testSubscriber = TestSubscriber<List<Book>>()
        observable.subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val responses = testSubscriber.onNextEvents
        assertNotNull(responses)
        assertEquals(1, responses.size)

        val books = responses[0]
        assertFalse(books.isEmpty())
    }

    fun testBookModel() {
        val book = com.nearsoft.nearbooks.models.realm.Book()
        book.id = "123"
        book.author = "epool"
        book.isAvailable = true
        book.title = "Title"
        book.releaseYear = "2015"
        book.numberOfCopies = 1
        book.numberOfDaysAllowedForBorrowing = 7

        val realm = Realm.getDefaultInstance()

        realm.executeTransaction { realm.copyToRealmOrUpdate(book) }

        val book1 = realm.where(com.nearsoft.nearbooks.models.realm.Book::class.java)
                .equalTo(Book.ID, "123")
                .findFirst()

        assertNotNull(book1)
        assertEquals(book.id, book1.id)

        realm.executeTransaction { book1.deleteFromRealm() }

        realm.close()
    }

    @Throws(IOException::class)
    fun testBookAvailability() {
        val observable = mBookService!!.getBookAvailability("0321534468-0")

        val testSubscriber = TestSubscriber<Response<AvailabilityResponse>>()
        observable.subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val responses = testSubscriber.onNextEvents
        assertNotNull(responses)
        assertEquals(1, responses.size)

        val response = responses[0]
        assertNotNull(response)
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
    }

    @Throws(IOException::class)
    fun testRequestBookToBorrow() {
        val requestBody = RequestBody()
        requestBody.qrCode = "0321534468-0"
        requestBody.userEmail = "epool@nearsoft.com"
        val observable = mBookService!!.requestBookToBorrow(requestBody)

        val testSubscriber = TestSubscriber<Response<Borrow>>()
        observable.subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val responses = testSubscriber.onNextEvents
        assertNotNull(responses)
        assertEquals(1, responses.size)

        val response = responses[0]
        assertNotNull(response)

        if (response.isSuccessful) {
            val borrow = response.body()
            assertNotNull(borrow)
        } else {
            val errorMessage = ErrorUtil.parseError(response)
            assertNotNull(errorMessage)
        }
    }

    @Throws(IOException::class)
    fun testCheckInBook() {
        val requestBody = RequestBody()
        requestBody.qrCode = "0321534468-1"
        requestBody.userEmail = "epool@nearsoft.com"
        val observable = mBookService!!.checkInBook(requestBody)

        val testSubscriber = TestSubscriber<Response<MessageResponse>>()
        observable.subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val responses = testSubscriber.onNextEvents
        assertNotNull(responses)
        assertEquals(1, responses.size)

        val response = responses[0]
        assertNotNull(response)

        if (response.isSuccessful) {
            val messageResponse = response.body()
            assertNotNull(messageResponse)
        } else {
            val messageResponse = ErrorUtil.parseError(MessageResponse::class.java, response)
            assertNotNull(messageResponse)
        }
    }

    @Throws(IOException::class)
    fun testCheckOutBook() {
        val requestBody = RequestBody()
        requestBody.qrCode = "0321534468-1"
        requestBody.userEmail = "epool@nearsoft.com"
        val observable = mBookService!!.checkOutBook(requestBody)

        val testSubscriber = TestSubscriber<Response<MessageResponse>>()
        observable.subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val responses = testSubscriber.onNextEvents
        assertNotNull(responses)
        assertEquals(1, responses.size)

        val response = responses[0]
        assertNotNull(response)

        if (response.isSuccessful) {
            val messageResponse = response.body()
            assertNotNull(messageResponse)
        } else {
            val messageResponse = ErrorUtil.parseError(MessageResponse::class.java, response)
            assertNotNull(messageResponse)
        }
    }

    @Throws(IOException::class)
    fun testGoogleBooksFindByIsbn() {
        val observable = mGoogleBooksService!!.findBooksByIsbn("9780321534460")

        val testSubscriber = TestSubscriber<Response<GoogleBooksSearchResponse>>()
        observable.subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val responses = testSubscriber.onNextEvents
        assertNotNull(responses)
        assertEquals(1, responses.size)

        val response = responses[0]
        assertNotNull(response)
        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assertTrue(response.body().totalItems > 0)
    }

}