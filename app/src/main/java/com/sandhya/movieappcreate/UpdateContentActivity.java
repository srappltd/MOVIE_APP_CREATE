package com.sandhya.movieappcreate;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sandhya.movieappcreate.model.ContentModel;

import java.util.HashMap;

public class UpdateContentActivity extends AppCompatActivity {
    ImageView imgThumbUpdate;
    TextView txtGalleryOpen, tbTitle;
    EditText edtUpdateTitle, edtUpdateDes;
    Button btnUpdate;
    Uri thumbnailUri;

    DatabaseReference reference;
    StorageReference storageReference, imageRef;
    String key;

    ContentModel model;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_content);

        //method find
        initIdFind();
        initGallery();
        initUpdateData();

    }

    private void initIdFind() {
        imgThumbUpdate = findViewById(R.id.imgThumbUpdate);
        edtUpdateTitle = findViewById(R.id.edtUpdateTitle);
        edtUpdateDes = findViewById(R.id.edtUpdateDes);
        txtGalleryOpen = findViewById(R.id.txtGalleryOpen);
        btnUpdate = findViewById(R.id.btnUpdate);
        tbTitle = findViewById(R.id.tbTitle);
    }

    private void initUpdateData() {

        reference = FirebaseDatabase.getInstance().getReference().child("Contents");
        storageReference = FirebaseStorage.getInstance().getReference().child("Contents");
        //data passing contentAdapter to UpdateContentActivity
        Bundle bundle = getIntent().getExtras();
        String titleUpdate = bundle.getString("updateTitle");
        String desUpdate = bundle.getString("updateDescription");
        String thumbnailUpdate = bundle.getString("updateThumbnail");
        key = bundle.getString("key");

        //image and text fetch data
        Glide.with(this).load(thumbnailUpdate).placeholder(R.drawable.ic_launcher_background).into(imgThumbUpdate);
        edtUpdateTitle.setText(titleUpdate);
        edtUpdateDes.setText(desUpdate);
        tbTitle.setText(titleUpdate);

        //progressDialog show
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //update button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtUpdateTitle.getText().toString();
                String des = edtUpdateDes.getText().toString();
                if (thumbnailUri != null && !title.isEmpty() && !des.isEmpty()) {
                    progressDialog.show();
                    imageRef = storageReference.child(System.currentTimeMillis() + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(thumbnailUri)));
                    imageRef.putFile(thumbnailUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri thumb) {
//
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("thumb", thumb.toString());
                                    map.put("title", title);
                                    map.put("description", des);
                                    reference.child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                edtUpdateTitle.setText("");
                                                edtUpdateDes.setText("");
                                                startActivity(new Intent(UpdateContentActivity.this, VideoActivity.class));
                                                finish();
                                                Toast.makeText(UpdateContentActivity.this, "Successfully update Data", Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(UpdateContentActivity.this, "Something wrong, please try again...", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(UpdateContentActivity.this, "Please Insert data", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initGallery() {
        txtGalleryOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent();
                iGallery.setType("image/*");
                iGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(iGallery, 200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                thumbnailUri = data.getData();
                imgThumbUpdate.setImageURI(thumbnailUri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}