package com.nearsoft.nearbooks;

import android.os.SystemClock;
import android.test.ApplicationTestCase;

import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.ws.BookService;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import retrofit.Response;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<NearbooksApplication> {

    public ApplicationTest() {
        super(NearbooksApplication.class);
    }

    public void testBookService() throws IOException {
        getApplication();
        SystemClock.sleep(100);
        NearbooksApplicationComponent nearbooksApplicationComponent = NearbooksApplication
                .getNearbooksApplicationComponent();
        BookService bookService = nearbooksApplicationComponent.providesBookService();
        Response<List<Book>> booksResponse = bookService.getAllBooks().execute();
        assertTrue(booksResponse.isSuccess());
        List<Book> books = booksResponse.body();
        assertFalse(books.isEmpty());
    }

    public void testBookModel() {
        FlowManager.init(getApplication());
        SystemClock.sleep(100);
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
}