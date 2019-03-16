package pl.strefakursow.personaladdressbook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.strefakursow.personaladdressbook.data.DatabaseDescription;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /* Metody wywołania zwrotnego implementowane przez klasę MainActivity */
    public interface ContactsFragmentListener {

        /* Wywołanie w wyniku wybrania kontaktu */
        void onContactSelected(Uri contactUri);

        /* Wywołanie w wyniku dotknięcia przycisku (+) */
        void onAddContact();
    }

    /* Identyfikator obiektu Loader */
    private static final int CONTACTS_LOADER = 0;

    /* Obiekt informujący aktywność MainActivity o wybraniu kontaktu */
    private ContactsFragmentListener listener;

    /* Adapter obiektu RecyclerView */
    private ContactsAdapter contactsAdapter;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        /* Przygotowanie do wyświetlenia graficznego interfejsu użytkownika */
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        /* Uzyskanie odwołania do widoku RecyclerView */
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        /* Konfiguracja widoku RecyclerView - widok powinien wyświetlać elementy w formie pionowej listy */
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        contactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactClickListener() {
            @Override
            public void onClick(Uri contactUri) {
                listener.onContactSelected(contactUri);
            }
        });

        /* Ustawienie adaptera widoku RecyclerView */
        recyclerView.setAdapter(contactsAdapter);

        /* Dołączenie spersonalizowanego obiektu ItemDivider */
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        /* Rozmiar widoku RecyclerView nie ulega zmianie */
        recyclerView.setHasFixedSize(true);

        /* Inicjalizacja i konfiguracja przycisku dodawania kontaktu (+) */
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddContact();
            }
        });

        /* Zwrócenie widoku graficznego interfejsu użytkownika */
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ContactsFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }

    public void updateContactList() {
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /* Utworzenie obiektu CursorLoader */
        switch (id) {
            case CONTACTS_LOADER:
                return new CursorLoader(getActivity(),
                        DatabaseDescription.Contact.CONTENT_URI, // Adres URI tabeli kontaktów.
                        null, // Wartość null zwraca wszystkie kolumny.
                        null, // Wartość null zwraca wszystkie wiersze.
                        null, // Brak argumentów selekcji.
                        DatabaseDescription.Contact.COLUMN_NAME + " COLLATE NOCASE ASC"); // Kolejność sortowania.
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        contactsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactsAdapter.swapCursor(null);
    }
}
