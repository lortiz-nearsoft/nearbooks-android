package com.nearsoft.nearbooks;

import android.os.SystemClock;
import android.test.ApplicationTestCase;

import com.nearsoft.nearbooks.models.sqlite.Book;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<NearbooksApplication> {
    public ApplicationTest() {
        super(NearbooksApplication.class);
    }

    public void testBookModel() {
        FlowManager.init(getApplication());
        SystemClock.sleep(100);
        Book book = new Book();
        book.setId("123");
        book.setAuthor("epool");
        book.setIsAvailable(true);
        book.setIsbn("123412341234");
        book.setTitle("Title");
        book.setYear("2015");
        if (!book.exists()) {
            book.save();
        }

        Book book1 = SQLite.select().from(Book.class).querySingle();

        assertTrue(book.exists());
        assertEquals(book, book1);
    }
}