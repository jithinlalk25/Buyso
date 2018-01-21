package com.example.jithin.buyso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jithin on 17/1/18.
 */

public class Profile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText name,number;
    TextView loc_name;
    String loc_id = "",district = "0";
    int PLACE_PICKER_REQUEST = 1;
    Spinner spin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef,myRef1;
    private String usergmail;
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        showProgressDialog();

        spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        String[] categories = {"Alappuzha","Ernakulam","Idukki","Kollam","Kannur","Kasaragod","Kottayam","Kozhikode"
        ,"Malappuram","Palakkad","Pathanamthitta","Thiruvananthapuram","Thrissur","Wayanad"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_1, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(dataAdapter);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        if(mAuth.getCurrentUser() != null){
            usergmail = mAuth.getCurrentUser().getEmail();
            usergmail = usergmail.substring(0,usergmail.length()-10);
        }

        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);
        loc_name = (TextView) findViewById(R.id.location);

        myRef1 = mFirebaseDatabase.getReference("Shop").child(usergmail).child("Profile");
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                hideProgressDialog();
                if(snapshot.exists()) {
                    Pro p = snapshot.getValue(Pro.class);
                    name.setText(p.getName());
                    number.setText(p.getNumber());
                    loc_name.setText(p.getLoc_name());
                    spin.setSelection(Integer.parseInt(p.getDistrict()));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home: finish();
                break;
            case R.id.save:

                if(name.getText().toString().trim().equals(""))
                    Toast.makeText(getApplicationContext(),"Invalid Name", Toast.LENGTH_SHORT).show();
                else if(number.getText().length() < 10 )
                    Toast.makeText(getApplicationContext(),"Invalid Number", Toast.LENGTH_SHORT).show();
                else
                    {
                        Pro pro = new Pro();
                        pro.setName(name.getText().toString().trim());
                        pro.setNumber(number.getText().toString());
                        pro.setDistrict(district);
                        pro.setLoc_name(loc_name.getText().toString().trim());
                        pro.setLoc_id(loc_id);

                        myRef.child("Shop").child(usergmail).child("Profile").setValue(pro);

                        Toast.makeText(getApplicationContext(),"Saved", Toast.LENGTH_SHORT).show();
                    }
                break;
        }

        return true;
    }

    public void location(View view){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try{
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch (Exception e){}

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                //String toastMsg = String.format("Place: %s", place.getName());
                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                loc_name.setText(place.getName().toString());
                loc_id = place.getId();
                //name.setText(loc_id);
            }
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");//getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        district = ""+pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
