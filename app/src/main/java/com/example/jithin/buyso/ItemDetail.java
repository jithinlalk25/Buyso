package com.example.jithin.buyso;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

public class ItemDetail extends AppCompatActivity {

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String usergmail;
    String imageId,title,price,description,key;
    ImageView image1,image2,image3,image4;
    TextView title1,price1,description1;
    int i = 0,j = 0,k = 0;
    boolean b = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

        if(mAuth.getCurrentUser() != null){
            usergmail = mAuth.getCurrentUser().getEmail();
            //usergmail = usergmail.substring(0,usergmail.length()-10);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();

        imageId = getIntent().getStringExtra("imageId");
        title = getIntent().getStringExtra("title");
        price = getIntent().getStringExtra("price");
        description = getIntent().getStringExtra("description");
        key = getIntent().getStringExtra("key");

        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);
        image4 = (ImageView)findViewById(R.id.image4);

        final ImageView[] images = {image1,image2,image3,image4};

        images[1].setVisibility(View.GONE);
        images[2].setVisibility(View.GONE);
        images[3].setVisibility(View.GONE);

        getSupportActionBar().setTitle(title);

        title1 = (TextView)findViewById(R.id.title);
        price1 = (TextView)findViewById(R.id.price);
        description1 = (TextView)findViewById(R.id.description);

        title1.setText(title);
        price1.setText(getResources().getString(R.string.Rs) + price);
        description1.setText(description);

        try {
            i=0;
            for(final ImageView im: images){

            StorageReference riversRef = mStorageRef.child(usergmail+"/"+imageId+"/"+(++i));

            final File localFile = File.createTempFile("image"+i, "jpg");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file


                            im.setVisibility(View.VISIBLE);

                            ImageLoader imageLoader = ImageLoader.getInstance();

                            imageLoader.displayImage(Uri.fromFile(localFile).toString(), im, new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {}
                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {}
                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {}
                            });



                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        }}catch (Exception e){}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete:

                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Confirmation");
                adb.setMessage("Are you sure to delete item?");
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        b = true;
                        k=0;

                        for(j=0;j<4;j++) {

                            StorageReference pRef = mStorageRef.child(usergmail + "/" + imageId + "/"+(j+1));

                            pRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    if(b){
                                        b=false;
                                    Toast.makeText(ItemDetail.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    DatabaseReference myRef = database.getReference("Shop").child(usergmail.substring(0, usergmail.length() - 10)).child("Item");
                                    myRef.child(key).removeValue();
                                    finish();
                                }}
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    if(++k == 4){
                                    Toast.makeText(ItemDetail.this, "Failed", Toast.LENGTH_SHORT).show();
                                }}
                            });

                        }

                        return;
                    }
                });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                });
                adb.show();
                break;
        }

        return true;
    }


}
