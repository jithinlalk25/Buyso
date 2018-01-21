package com.example.jithin.buyso;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddItem extends AppCompatActivity {

    ArrayList<String> imgURLs = new ArrayList<>();
    private String mAppend = "file:/";
    EditText title,price,description;
    private FirebaseMethods mFirebaseMethods;

    private FirebaseAuth mAuth;
    private String usergmail;

    boolean pro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            usergmail = mAuth.getCurrentUser().getEmail();
            usergmail = usergmail.substring(0,usergmail.length()-10);
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Shop").child(usergmail);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("Profile")) {
                    pro = true;
                    // run some code
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mFirebaseMethods = new FirebaseMethods(AddItem.this);

        title = (EditText) findViewById(R.id.title);
        price = (EditText) findViewById(R.id.price);
        description = (EditText) findViewById(R.id.description);

    }

    public void addimage(View view){
        Intent intent = new Intent(AddItem.this, AlbumSelectActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 4);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            StringBuffer stringBuffer = new StringBuffer();
            imgURLs.clear();
            for (int i = 0, l = images.size(); i < l; i++) {
                stringBuffer.append(images.get(i).path + "\n");
                imgURLs.add(images.get(i).path);
            }
            setupGridView();
            //textView.setText(stringBuffer.toString());
        }
    }

    private void setupGridView(){

        GridView gridView = (GridView) findViewById(R.id.gridView);
        CardView cardView = (CardView) findViewById(R.id.card_view);
        cardView.setVisibility(View.VISIBLE);
        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels - convertDpToPx(26);

        int imageWidth = gridWidth/2;
        gridView.setColumnWidth(imageWidth);

        //Toast.makeText(this,""+imgURLs.size(),Toast.LENGTH_SHORT).show();

        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        if(imgURLs.size()>2) {
            layoutParams.height = gridWidth; //this is in pixels
            gridView.setLayoutParams(layoutParams);
        }else {
            layoutParams.height = imageWidth; //this is in pixels
            gridView.setLayoutParams(layoutParams);
        }

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

    }

    public int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));

    }

    public void upload(View view){

       hideSoftKeyboard();

        if(!pro)
            Toast.makeText(this,"Set your profile",Toast.LENGTH_SHORT).show();
        else if(imgURLs.size()<1)
            Toast.makeText(this,"No image is added",Toast.LENGTH_SHORT).show();
        else if(title.getText().toString().trim().equals(""))
            Toast.makeText(this,"Invalid title",Toast.LENGTH_SHORT).show();
        else if(price.getText().toString().trim().equals(""))
            Toast.makeText(this,"Invalid price",Toast.LENGTH_SHORT).show();
        else if(description.getText().toString().trim().equals(""))
            Toast.makeText(this,"Invalid description",Toast.LENGTH_SHORT).show();
        else{

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Confirmation");
            adb.setMessage("Are you sure to upload item?");
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mFirebaseMethods.uploadNewPhoto(imgURLs,title.getText().toString().trim(),price.getText().toString().trim(),description.getText().toString().trim());

                    return;
                }
            });
            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    return;
                }
            });
            adb.show();
        }

    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
