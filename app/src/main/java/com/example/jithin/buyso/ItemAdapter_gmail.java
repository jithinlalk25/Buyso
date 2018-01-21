package com.example.jithin.buyso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.List;

public class ItemAdapter_gmail extends ArrayAdapter<Item_key_gmail> {

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String usergmail;

    public ItemAdapter_gmail(Context context, int resource, List<Item_key_gmail> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item, parent, false);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();

        final ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView price = (TextView) convertView.findViewById(R.id.price);

        final Item_key_gmail entry = getItem(position);

        usergmail = entry.gmail+"@gmail.com";
try {

    StorageReference riversRef = mStorageRef.child(usergmail+"/"+entry.getImageId()+"/1");

    final File localFile = File.createTempFile("image", "jpg");
    riversRef.getFile(localFile)
            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Successfully downloaded data to local file


                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(Uri.fromFile(localFile).toString(), image, new ImageLoadingListener() {
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
}catch (Exception e){}

        title.setText(entry.getTitle());
        price.setText(getContext().getResources().getString(R.string.Rs) + entry.getPrice());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item_key_gmail entryClick = getItem(position);
                Intent intent = new Intent(getContext(), ItemDetail_shop.class);
                intent.putExtra("gmail",usergmail);
                intent.putExtra("imageId",entryClick.getImageId());
                intent.putExtra("title",entryClick.getTitle());
                intent.putExtra("price",entryClick.getPrice());
                intent.putExtra("description",entryClick.getDescription());
                intent.putExtra("key",entryClick.key);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
