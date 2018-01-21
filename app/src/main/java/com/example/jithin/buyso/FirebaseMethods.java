package com.example.jithin.buyso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.UUID.randomUUID;

/**
 * Created by User on 6/26/2017.
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    public ProgressDialog mProgressDialog;
    int i,length;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String usergmail;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            usergmail = mAuth.getCurrentUser().getEmail();
            usergmail = usergmail.substring(0,usergmail.length()-10);
        }
    }

    public void uploadNewPhoto(ArrayList<String> imgURLs, final String title, final String price, final String description){

        mPhotoUploadProgress = 0;

        showProgressDialog();

        //Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        //FilePaths filePaths = new FilePaths();
        //case1) new photo
            //Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

        final String r = "" + randomUUID();

            length = imgURLs.size();
            i = -1;

        //Toast.makeText(mContext, ""+length, Toast.LENGTH_SHORT).show();

            for(String url: imgURLs){

                i++;


            String user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            StorageReference storageReference = mStorageReference
                    .child(user_email + "/" + r +"/" + (i+1));

            Bitmap bm = ImageManager.getBitmap(url);

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    //Toast.makeText(mContext, ""+i, Toast.LENGTH_SHORT).show();

                    if(i == length - 1){
                      //  hideProgressDialog();
                      //  Toast.makeText(mContext, "Item uploaded successfully", Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photos' node and 'user_photos' node
                    //addPhotoToDatabase(caption, firebaseUrl.toString());

                    //navigate to the main feed so the user can see their photo
                    //Intent intent = new Intent(mContext, HomeActivity.class);
                    //mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                    i=length;
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress == 100) {
                        mPhotoUploadProgress += progress;
                    }
                    if(mPhotoUploadProgress == (100*length)){//- 15 > mPhotoUploadProgress){
                        //Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        //mPhotoUploadProgress = progress;

                        hideProgressDialog();
                        Toast.makeText(mContext, "Item uploaded successfully", Toast.LENGTH_SHORT).show();

                        Item item = new Item();
                        item.setImageId(r);
                        item.setDescription(description);
                        item.setPrice(price);
                        item.setTitle(title);

                        myRef.child("Shop").child(usergmail).child("Item").push().setValue(item);
                        myRef.child("AllItem").push().setValue(item);

                        ((Activity)mContext).finish();

                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

    }}

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("Uploading...");//getString(R.string.loading));
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