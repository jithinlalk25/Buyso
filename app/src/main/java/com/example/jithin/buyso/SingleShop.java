package com.example.jithin.buyso;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SingleShop extends AppCompatActivity {

    private FloatingActionButton fab;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public ProgressDialog mProgressDialog;

    ListView listView;
    Item_key_gmail item;
    ItemAdapter_gmail itemAdapter;
    List<Item_key_gmail> allEntry = new ArrayList<>();

    String gmail,name,number,district,loc_name,loc_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_shop);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        showProgressDialog();


        gmail = getIntent().getStringExtra("gmail");
        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        district = getIntent().getStringExtra("district");
        loc_name = getIntent().getStringExtra("loc_name");
        loc_id = getIntent().getStringExtra("loc_id");

        getSupportActionBar().setTitle(name);


        listView = (ListView) findViewById(R.id.list_view);
        itemAdapter = new ItemAdapter_gmail(this, R.layout.item, allEntry);
        listView.setAdapter(itemAdapter);


        DatabaseReference myRef_entry = database.getReference("Shop").child(gmail).child("Item");
        myRef_entry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


        DatabaseReference myRef = database.getReference();
        Query myTopPostsQuery = myRef.child("Shop").child(gmail).child("Item");
        myTopPostsQuery.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {

                    hideProgressDialog();

                    item = dataSnapshot.getValue(Item_key_gmail.class);
                    item.key = dataSnapshot.getKey();
                    item.gmail = gmail;
                    allEntry.add(item);
                    itemAdapter.notifyDataSetChanged();

                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (Item_key_gmail pt : allEntry) {

                    if (key.equals(pt.key)) {
                        allEntry.remove(pt);
                        itemAdapter.notifyDataSetChanged();

                        break;
                    }
                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.shop:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(name);
                adb.setMessage("Contact : "+number+"\nLocation : "+loc_name);
                adb.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String uri = "tel:" + number;
                        Intent callIntent = new Intent(Intent.ACTION_VIEW);
                        callIntent.setData(Uri.parse(uri));
                        startActivity(callIntent);

                        return;
                    }
                });
                adb.setNegativeButton("Locate", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=Google&query_place_id="+loc_id));
                        startActivity(browserIntent);

                        return;
                    }
                });
                adb.show();
                break;
        }

        return true;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");//getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
