package com.nearsoft.nearbooks;

import android.os.SystemClock;
import android.test.ApplicationTestCase;

import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Borrow;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.ws.BookService;
import com.nearsoft.nearbooks.ws.bodies.RequestBody;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<NearbooksApplication> {

    private Retrofit retrofit;
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
        Response<List<Book>> booksResponse = mBookService.getAllBooks().execute();
        assertTrue(booksResponse.isSuccess());
        List<Book> books = booksResponse.body();
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

        Book book1 = SQLite.select().from(Book.class).querySingle();

        assertTrue(book.exists());
        assertEquals(book, book1);
    }

    public void testBookAvailability() throws IOException {
        Response<AvailabilityResponse> response = mBookService
                .getBookAvailability("0321534468-0")
                .execute();
        assertNotNull(response);
        if (response.isSuccess()) {
            AvailabilityResponse availabilityResponse = response.body();
            assertNotNull(availabilityResponse);
        } else {
            String errorMessage = ErrorUtil.parseError(response);
            assertNotNull(errorMessage);
        }
    }

    public void testRequestBookToBorrow() throws IOException {
        RequestBody requestBody = new RequestBody();
        requestBody.setQrCode("0321534468-0");
        requestBody.setUserEmail("epool@nearsoft.com");
        Response<Borrow> response = mBookService.requestBookToBorrow(requestBody).execute();
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
        Response<MessageResponse> response = mBookService.checkInBook(requestBody).execute();
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
        Response<MessageResponse> response = mBookService.checkOutBook(requestBody).execute();
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