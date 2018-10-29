package com.example.wolf.ha1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    static final int REQUEST_DETAILS = 2;
    ListView listView;
    long current_Id;
    String current_search="";
    String mailSentTo="";
    TextView current_selected;
    View current_view;
    int current_position;
    SearchView searchView;
    int clickedContactPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        String[] requestedPermissions = new String[]{
                Manifest.permission.READ_CONTACTS
        };

        ActivityCompat.requestPermissions(this, requestedPermissions, 1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedContactPosition = position;
                Log.i("Contacts","_ID: "+ id);
                current_view = view;
                current_selected = (TextView) view.findViewById(R.id.btnContact);
                mailSentTo = current_selected.getText().toString();
                current_position = position;

                setContactsData(id);
            }
        });
    }


    @Override
    protected void onPause() {
        Log.e("Contacts","onPause: ");
        getState();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e("Contacts","onDestroy: ");
        saveState();
        super.onDestroy();
    }

    public void saveState() {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.MY_PREFERENCE_FILE),
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        if (current_Id != 0){
            editor.putLong("_ID", current_Id);
            editor.putLong("current_position", current_position);
        }

        if (!current_search.isEmpty() || !current_search.equals(""))
            editor.putString("search", current_search);

        editor.commit();
    }

    public void getState() {
        String TAG ="getState";
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.MY_PREFERENCE_FILE),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (prefs.contains("current_search")){
            current_position= prefs.getInt("current_position",0);
        }

        if (prefs.contains("search")){
            String search = prefs.getString("search","");

            Log.e(TAG,"search + " + search);
            Log.e("TAG","search + " + prefs.contains("search"));
            current_search = search;
        }

        editor.clear();
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.bar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener(){
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        /*Log.i("Contacts","onQueryTextSubmit: "+query);
                        getContactByName(query);
                        return true;*/
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.i("Contacts","onQueryTextChange: "+newText);
                        current_search = newText;
                        getContactByName(newText);
                        return true;
                    }
                }
        );
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT);

        getContacts();
    }

    private void setContactsData(long id){
        current_Id = id;
        List<String> details = getContactBy_id(id);

        Intent intent = new Intent(this, ContactDetails.class);
        intent.putExtra("txtName", details.get(0));
        intent.putExtra("txtEmail", details.get(1));
        intent.putExtra("txtPhone", details.get(2));
        intent.putExtra("_id", id);
        //startActivity(intent);
        if(details.get(0) != null && details.get(1) != null && details.get(2) != null){
            startActivityForResult(intent, REQUEST_DETAILS);
        }else {
            Toast.makeText(this, "This contact has missing information!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DETAILS) {
            String state = data.getStringExtra("state");

            if(!state.equals("back")){
                TextView tt = listView.getChildAt(current_position).findViewById(R.id.btnContact);
                tt.setText(tt.getText().toString()+" [mail sent]");
            }
        }
    }

    private void getContacts(){
        //GET CONTACTS
        String TAG = "getContacts";
        ContentResolver contentResolver = getContentResolver();
        String[] fromColums ={
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        /*Cursor cursors = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                fromColums, null, null, null
        );*/
        Cursor cursors = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,
                fromColums, null, null, null
        );
        Log.i(TAG,"COUNT: "+ cursors.getCount());

        //Adapter
        String[] fromColumNameNr ={
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone._ID
        };

        int[] toViews = {R.id.btnContact};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                this, // context
                R.layout.list_contact, // layout that defines the views for this list item.
                cursors, // The database cursor
                fromColumNameNr, // "from" - column names of the data to bind to the UI
                toViews, // TextViews that should display column in the "from" param
                0 // Flags used to determine the behavior of the adapter
        );
        listView.setAdapter(cursorAdapter);

        //from memory
        if (!current_search.isEmpty() || !current_search.equals("")){
            searchView.setQuery(current_search, true);
            getContactByName(current_search);
            searchView.setIconified(false);
            searchView.clearFocus();
            Log.e(TAG,"current_search: "+current_search);
        }
    }

    private void getContactByName(String name){
        ContentResolver contentResolver = getContentResolver();

        String[] fromColumNameNr ={
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone._ID
        };
        String[] fromColums ={
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursors = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                fromColums, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+ " LIKE ?", new String[]{"%"+name+"%"}, null
        );

        int[] toViews = {R.id.btnContact};

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                this, // context
                R.layout.list_contact, // layout that defines the views for this list item.
                cursors, // The database cursor
                fromColumNameNr, // "from" - column names of the data to bind to the UI
                toViews, // TextViews that should display column in the "from" param
                0 // Flags used to determine the behavior of the adapter
        );
        listView.setAdapter(cursorAdapter);
    }

    private List<String> getContactBy_id(long _id){
        List<String> val = new ArrayList<String>();

        //GET CONTACTS
        String TAG = "getContactBy_id";
        ContentResolver contentResolver = getContentResolver();
        String[] fromColums ={
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursors = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                fromColums, ContactsContract.CommonDataKinds.Phone._ID+ " = ?", new String[]{""+_id}, null
        );

        Log.i(TAG,"COUNT: "+ cursors.getCount());

        int index_Id = cursors.getColumnIndex(fromColums[0]);
        int indexId = cursors.getColumnIndex(fromColums[1]);
        int indexName = cursors.getColumnIndex(fromColums[2]);
        int indexNumber = cursors.getColumnIndex(fromColums[3]);

        while (cursors.moveToNext()){
            String _ID = cursors.getString(index_Id);
            String id = cursors.getString(indexId);
            String name = cursors.getString(indexName);
            String nr = cursors.getString(indexNumber);

            // get the user's email address
            String email = null;
            Cursor ce = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
            if (ce != null && ce.moveToFirst()) {
                email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                ce.close();
            }

            val.add(name);
            val.add(email);
            val.add(nr);

            Log.i(TAG,"_ID: "+_ID+" ID: "+ id + " Name: "+ name +" Nr: " + nr + " Email: " + email);
        }

        return val;
    }
}
