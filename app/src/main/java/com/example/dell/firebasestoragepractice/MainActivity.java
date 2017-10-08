package com.example.dell.firebasestoragepractice;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private StorageReference photoRef;
    private ImageView lastImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastImage = (ImageView) findViewById(R.id.show_simple_image);
        storageReference = FirebaseStorage.getInstance().getReference();
        
        uploadImage();
    }

    private void uploadImage() {
    String[] projection = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE
    };

    final  Cursor cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,null,null,MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");

        if (cursor.moveToFirst()){
            String imageLocation= cursor.getString(1);

            File imageFile = new File(imageLocation);

            Uri imageUri= Uri.fromFile(imageFile);
            photoRef = storageReference.child("photos/last_image.jpg");

            photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  //  Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Checking",""+e.getMessage());

                }
            });

            if (imageFile.exists()){
                Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                lastImage.setImageBitmap(bm);
            }
        }



    }
}
