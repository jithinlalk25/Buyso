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

public class ShopAdapter extends ArrayAdapter<Pro_key> {

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String usergmail;
    String[] dist = {"Alappuzha","Ernakulam","Idukki","Kollam","Kannur","Kasaragod","Kottayam","Kozhikode"
            ,"Malappuram","Palakkad","Pathanamthitta","Thiruvananthapuram","Thrissur","Wayanad"};

    public ShopAdapter(Context context, int resource, List<Pro_key> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.shop, parent, false);
        }

        if(mAuth.getCurrentUser() != null){
            usergmail = mAuth.getCurrentUser().getEmail();
            //usergmail = usergmail.substring(0,usergmail.length()-10);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView district = (TextView) convertView.findViewById(R.id.district);

        final Pro_key entry = getItem(position);

        name.setText(entry.getName());
        district.setText(dist[Integer.parseInt(entry.getDistrict())]);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pro_key entryClick = getItem(position);
                Intent intent = new Intent(getContext(), SingleShop.class);
                intent.putExtra("gmail",entryClick.gmail);
                intent.putExtra("name",entryClick.getName());
                intent.putExtra("number",entryClick.getNumber());
                intent.putExtra("district",entryClick.getDistrict());
                intent.putExtra("loc_name",entryClick.getLoc_name());
                intent.putExtra("loc_id",entryClick.getLoc_id());
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
