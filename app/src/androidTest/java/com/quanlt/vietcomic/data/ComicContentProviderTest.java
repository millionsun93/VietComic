package com.quanlt.vietcomic.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ComicContentProviderTest extends ProviderTestCase2<ComicContentProvider> {
    private MockContentResolver mockContentResolver;

    public ComicContentProviderTest() {
        super(ComicContentProvider.class, ComicContract.CONTENT_AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
        mockContentResolver = getMockContentResolver();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
//        getMockContext().deleteDatabase(ComicDbHelper.DATABASE_NAME);

    }

    @Test
    public void testName() throws Exception {
        Uri uri = mockContentResolver.insert(ComicContract.ComicEntry.CONTENT_URI, TestUtilities.provideComicContentValue());
        assertNotNull(uri);
    }

    @Test
    public void testGetType() throws Exception {
        String type = mockContentResolver.getType(ComicContract.ComicEntry.CONTENT_URI);
        assertEquals("Error: the comics should return ComicEntry.CONTENT_DIR_TYPE",
                ComicContract.ComicEntry.CONTENT_DIR_TYPE, type);

        type = mockContentResolver.getType(ComicContract.ComicEntry.buildComicWithId(TestUtilities.COMIC_ID));
        assertEquals("Error: the comics should return ComicEntry.CONTENT_ITEM_TYPE",
                ComicContract.ComicEntry.CONTENT_ITEM_TYPE, type);

        type = mockContentResolver.getType(ComicContract.ComicEntry.buildComicWithAuthor(TestUtilities.AUTHOR));
        assertEquals("Error: the comics should return ComicEntry.CONTENT_DIR_TYPE",
                ComicContract.ComicEntry.CONTENT_DIR_TYPE, type);

        type = mockContentResolver.getType(ComicContract.ComicEntry.buildComicWithCategory(TestUtilities.CATEGORY));
        assertEquals("Error: the comics should return ComicEntry.CONTENT_DIR_TYPE",
                ComicContract.ComicEntry.CONTENT_DIR_TYPE, type);

        type = mockContentResolver.getType(ComicContract.CategoryEntry.CONTENT_URI);
        assertEquals("Error: the categories should return CategoryEntry.CONTENT_DIR_TYPE",
                ComicContract.CategoryEntry.CONTENT_DIR_TYPE, type);
    }

    @Test
    public void testInsertComic() throws Exception {
        ContentValues values = TestUtilities.provideComicContentValue();
        Uri returnUri = mockContentResolver.insert(ComicContract.ComicEntry.CONTENT_URI, values);
        assertTrue("Insert failed ", returnUri != null);
        assertEquals("Comic id is not matched ", TestUtilities.COMIC_ID, returnUri.getPathSegments().get(1));
        Cursor cursor = mockContentResolver.query(ComicContract.ComicEntry.CONTENT_URI,
                ComicContract.ComicEntry.getColumns(),
                null,
                null,
                null);
        assertTrue(cursor.moveToFirst());
        Comic comic = Comic.fromCursor(cursor);
        assertNotNull(comic);
        assertEquals(ComicContract.ComicEntry.getColumns().length, cursor.getColumnCount());
        assertEquals(cursor.getLong(cursor.getColumnIndex(ComicContract.ComicEntry.COLUMN_UPDATE_TIME)), TestUtilities.calendar.getTimeInMillis());
    }

    @Test
    public void testSearchByAuthor() throws Exception {
        insertComic();
        ContentValues values = TestUtilities.provideComicContentValue();
        Cursor cursor = mockContentResolver.query(ComicContract.ComicEntry.buildComicWithAuthor(TestUtilities.AUTHOR),
                ComicContract.ComicEntry.getColumns(),
                null,
                null,
                null);
        TestUtilities.validateCursor("Error when get comic with author", cursor, values);
    }

    @Test
    public void testSearchByCategory() throws Exception {
        insertComic();
        ContentValues values = TestUtilities.provideComicContentValue();
        Cursor cursor = mockContentResolver.query(ComicContract.ComicEntry.buildComicWithCategory(TestUtilities.CATEGORY),
                ComicContract.ComicEntry.getColumns(),
                null,
                null,
                null);
        assertNotNull(cursor.moveToFirst());
        assertEquals(1, cursor.getCount());
        TestUtilities.validateCursor("Error when get comic with author", cursor, values);
    }

    @Test
    public void testFavorite() throws Exception {
        insertComic();
        ContentValues values = TestUtilities.provideComicContentValue();
        ContentValues favoriteValue = new ContentValues();
        favoriteValue.put(ComicContract.COLUMN_COMIC_ID, values.getAsString(ComicContract.ComicEntry._ID));
        Uri returnUri = mContext.getContentResolver().insert(ComicContract.FavoriteEntry.CONTENT_URI, favoriteValue);
        assertNotNull(returnUri);
    }

    @Test
    public void testDeleteFavorite() throws Exception {
        ContentValues values = TestUtilities.provideComicContentValue();
        int deletedRow = mContext.getContentResolver().delete(ComicContract.FavoriteEntry.CONTENT_URI,
                ComicContract.COLUMN_COMIC_ID + "=" + values.getAsString(ComicContract.ComicEntry._ID),
                null);
        assertEquals(1, deletedRow);
    }

    private void insertComic() {
        ContentValues values = TestUtilities.provideComicContentValue();
        mockContentResolver.insert(ComicContract.ComicEntry.CONTENT_URI, values);
    }
}
