package com.example.jithin.buyso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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

public class MyShop extends AppCompatActivity {

    private FloatingActionButton fab;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String usergmail;
    public ProgressDialog mProgressDialog;

    ListView listView;
    Item_key item;
    ItemAdapter itemAdapter;
    List<Item_key> allEntry = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_shop);

        showProgressDialog();

        if(mAuth.getCurrentUser() != null){
            usergmail = mAuth.getCurrentUser().getEmail();
            usergmail = usergmail.substring(0,usergmail.length()-10);
        }


        listView = (ListView) findViewById(R.id.list_view);
        itemAdapter = new ItemAdapter(this, R.layout.item, allEntry);
        listView.setAdapter(itemAdapter);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyShop.this, AddItem.class);
                startActivity(intent);
            }
        });


        DatabaseReference myRef_entry = database.getReference("Shop").child(usergmail).child("Item");
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
        Query myTopPostsQuery = myRef.child("Shop").child(usergmail).child("Item");
        myTopPostsQuery.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {

                    hideProgressDialog();

                    item = dataSnapshot.getValue(Item_key.class);
                    item.key = dataSnapshot.getKey();
                    allEntry.add(item);
                    itemAdapter.notifyDataSetChanged();

                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (Item_key pt : allEntry) {

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
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.profile:Intent intent = new Intent(this, Profile.class);
                            startActivity(intent);
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
