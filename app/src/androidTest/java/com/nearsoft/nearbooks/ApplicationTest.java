package com.nearsoft.nearbooks;

import android.os.SystemClock;
import android.test.ApplicationTestCase;

import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Borrow;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.ws.BookService;
import com.nearsoft.nearbooks.ws.bodies.RequestBody;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<NearbooksApplication> {

    private BookService mBookService;

    public ApplicationTest() {
        super(NearbooksApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getApplication();
        SystemClock.sleep(100);
        NearbooksApplicationComponent nearbooksApplicationComponent = NearbooksApplication
                .getNearbooksApplicationComponent();
        mBookService = nearbooksApplicationComponent.providesBookService();
    }

    public void testBookService() throws IOException {
        Observable<List<Book>> observable = mBookService.getAllBooks();

        TestSubscriber<List<Book>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<List<Book>> responses = testSubscriber.getOnNextEvents();
        assertNotNull(responses);
        assertEquals(1, responses.size());

        List<Book> books = responses.get(0);
        assertFalse(books.isEmpty());
    }

    public void testBookModel() {
        Book book = new Book();
        book.setId("123");
        book.setAuthor("epool");
        book.setAvailable(true);
        book.setTitle("Title");
        book.setReleaseYear(2015);
        book.setNumberOfCopies(1);
        book.setNumberOfDaysAllowedForBorrowing(7);
        if (!book.exists()) {
            book.save();
        }

        Book book1 = BookModel.findByBookId("123");

        assertTrue(book.exists());
        assertEquals(book, book1);
    }

    public void testBookAvailability() throws IOException {
        Observable<Response<AvailabilityResponse>> observable = mBookService
                .getBookAvailability("0321534468-0");

        TestSubscriber<Response<AvailabilityResponse>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<Response<AvailabilityResponse>> responses = testSubscriber.getOnNextEvents();
        assertNotNull(responses);
        assertEquals(1, responses.size());

        Response<AvailabilityResponse> response = responses.get(0);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.body());
    }

    public void testRequestBookToBorrow() throws IOException {
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode("0321534468-0");
        requestBody.setUserEmail("epool@nearsoft.com");
        Observable<Response<Borrow>> observable = mBookService.requestBookToBorrow(requestBody);

        TestSubscriber<Response<Borrow>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<Response<Borrow>> responses = testSubscriber.getOnNextEvents();
        assertNotNull(responses);
        assertEquals(1, responses.size());

        Response<Borrow> response = responses.get(0);
        assertNotNull(response);

        if (response.isSuccess()) {
            Borrow borrow = response.body();
            assertNotNull(borrow);
        } else {
            String errorMessage = ErrorUtil.parseError(response);
            assertNotNull(errorMessage);
        }
    }

    public void testCheckInBook() throws IOException {
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode("0321534468-1");
        requestBody.setUserEmail("epool@nearsoft.com");
        Observable<Response<MessageResponse>> observable = mBookService.checkInBook(requestBody);

        TestSubscriber<Response<MessageResponse>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<Response<MessageResponse>> responses = testSubscriber.getOnNextEvents();
        assertNotNull(responses);
        assertEquals(1, responses.size());

        Response<MessageResponse> response = responses.get(0);
        assertNotNull(response);

        if (response.isSuccess()) {
            MessageResponse messageResponse = response.body();
            assertNotNull(messageResponse);
        } else {
            MessageResponse messageResponse = ErrorUtil.parseError(MessageResponse.class, response);
            assertNotNull(messageResponse);
        }
    }

    public void testCheckOutBook() throws IOException {
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode("0321534468-1");
        requestBody.setUserEmail("epool@nearsoft.com");
        Observable<Response<MessageResponse>> observable = mBookService.checkOutBook(requestBody);

        TestSubscriber<Response<MessageResponse>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<Response<MessageResponse>> responses = testSubscriber.getOnNextEvents();
        assertNotNull(responses);
        assertEquals(1, responses.size());

        Response<MessageResponse> response = responses.get(0);
        assertNotNull(response);

        if (response.isSuccess()) {
            MessageResponse messageResponse = response.body();
            assertNotNull(messageResponse);
        } else {
            MessageResponse messageResponse = ErrorUtil.parseError(MessageResponse.class, response);
            assertNotNull(messageResponse);
        }
    }

}