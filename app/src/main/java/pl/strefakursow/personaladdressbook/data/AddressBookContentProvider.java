package pl.strefakursow.personaladdressbook.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import pl.strefakursow.personaladdressbook.R;

public class AddressBookContentProvider extends ContentProvider {

    /* Egzemplarz klasy - umożliwia obiektowi ContentProvider uzyskanie dostępu do bazy danych */
    private AddressBookDatabaseHelper dbHelper;

    /* Pomocnik obiektu ContentProvider */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /* Stałe obiektu UriMatcher używane w celu określenia operacji do wykonania na bazie danych */
    private static final int ONE_CONTACT = 1; // Wykonanie operacji dla jednego kontaktu.
    private static final int CONTACTS = 2; // Wykonanie operacji dla całej tabeli kontaktów.

    /* Konfiguracja obiektu UriMatcher */
    static {
        /* Adres URI kontaktu o określonym identyfikatorze (#) */
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.Contact.TABLE_NAME + "/#", ONE_CONTACT);

        /* Adres URI dla całej tabeli kontaktów */
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.Contact.TABLE_NAME, CONTACTS);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        /* Przyjmuje wartość 1, jeżeli usuwanie przebiegło pomyślnie; w przeciwnym razie 0 */
        int numberOfRowsDeleted;

        /* Sprawdza adres URI */
        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:

                /* Odczytanie identyfikatora kontaktu, który ma zostać usunięty */
                String id = uri.getLastPathSegment();

                /* Skasowanie kontaktu */
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(DatabaseDescription.Contact.TABLE_NAME,
                        DatabaseDescription.Contact._ID + "=" + id, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        /* Jeżeli dokonano usunięcia, to powiadom obiekty nasłuchujące zmian w bazie danych */
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        /* Zwróć info o usunięciu */
        return numberOfRowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        /* Deklaracja obiektu URI */
        Uri newContactUri = null;

        /* Sprawdzenie, czy adres URI odwołuje się do tabeli "contacts" */
        switch (uriMatcher.match(uri)) {
            case CONTACTS:

                /* Wstawienie nowego kontaktu do tabeli */
                long rowId = dbHelper.getWritableDatabase().insert(DatabaseDescription.Contact.TABLE_NAME, null, values);

                /* Tworzenie adresu URI dla dodanego kontaktu */
                /* Jeżeli dodanie się powiodło... */
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.Contact.buildContactUri(rowId);

                    /* Powiadomienie obiektów nasłuchujących zmian w tabeli */
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                /* Jeżeli dodanie się nie powiodło... */
                else {
                    throw new SQLException(getContext().getString(R.string.insert_failed) + uri);
                }

                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        /* Zwrócenie adresu URI */
        return newContactUri;

    }

    @Override
    public boolean onCreate() {

        /* Utworzenie obiektu AddressBookDatabaseHelper */
        dbHelper = new AddressBookDatabaseHelper(getContext());

        /* Operacja utworzenia obiektu ContentProvider została zakończona sukcesem */
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        /* Obiekt SQLiteQueryBuilder służący do tworzenia zapytań SQL */
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseDescription.Contact.TABLE_NAME);

        /* Wybranie jednego lub wszystkich kontaktów z tabeli */
        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                queryBuilder.appendWhere(DatabaseDescription.Contact._ID + "=" + uri.getLastPathSegment());
                break;
            case CONTACTS:
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_query_uri) + uri);
        }

        /* Wykonanie zapytania SQL i inicjalizacja obiektu Cursor */
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);

        /* Konfiguracja obiektu Cursor */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        /* Zwrócenie obiektu Cursor */
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        /* Przyjmuje wartość 1, jeżeli aktualizacja przebiegła pomyślnie; w przeciwnym razie 0 */
        int numberOfRowsUpdated;

        /* Sprawdza adres URI */
        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:

                /* Odczytanie identyfikatora kontaktu, który ma zostać zaktualizowany */
                String id = uri.getLastPathSegment();

                /* Aktualizacja zawartości kontaktu */
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(DatabaseDescription.Contact.TABLE_NAME,
                        values, DatabaseDescription.Contact._ID + "=" + id, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_update_uri) + uri);
        }

        /* Jeżeli dokonano aktualizacji, to powiadom obiekty nasłuchujące zmian w bazie danych */
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        /* Zwróć info o aktualizacji */
        return numberOfRowsUpdated;
    }
}















