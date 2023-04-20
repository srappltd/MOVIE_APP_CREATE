package com.sandhya.movieappcreate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sandhya.movieappcreate.model.ContentModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    StorageReference storage;
    StorageReference storageVideo,storageThumb;
    DatabaseReference reference;

    FirebaseFirestore firestore;



    VideoView videoView;
    ImageView imgThumbnail;
    EditText edtTitle,edtDescription;
    Button btnVideoThumbnail,btnSubmit;
    Uri thumbnailUri,videoUri;

    ProgressBar progressBar;
    ContentModel model;

    static final int REQUEST_CODE_GALLERY = 100;
    static final int REQUEST_CODE_VIDEO = 101;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // firebase and storage init
        storage = FirebaseStorage.getInstance().getReference().child("Contents");
        reference = FirebaseDatabase.getInstance().getReference().child("Contents");
        firestore = FirebaseFirestore.getInstance();

        initIdFind();
        //video upload method
        initSubmit();
        //video and image show method
        initVideoThumb();
    }

    public void initIdFind(){
        videoView = findViewById(R.id.videoView);
        imgThumbnail = findViewById(R.id.imgThumbnail);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
    }
    private void initSubmit() {
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        btnSubmit = findViewById(R.id.btnSubmit);

        //progressDialog show
        ProgressDialog progressDialog = new ProgressDialog(UploadActivity.this);
        progressDialog.setTitle("Video Upload");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        btnSubmit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(UploadActivity.this,VideoActivity.class));
                return false;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtTitle.getText().toString();
                String description = edtDescription.getText().toString();
                if (videoUri!=null&&thumbnailUri!=null&&!title.isEmpty()&&!description.isEmpty()){
                    progressDialog.show();

                    //video upload
                    storageVideo = storage.child(System.currentTimeMillis()+"."+ MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(videoUri)));

                    //storage
                    storageVideo.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageThumb =storage.child(System.currentTimeMillis()+"."+ MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(thumbnailUri)));

                            //thumbnail upload
                            storageThumb.putFile(thumbnailUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    storageThumb.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri thumb) {

                                            storageVideo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri video) {
                                                    String key = reference.push().getKey();
                                                    String date = DateFormat.getDateInstance().format(new Date());
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("thumb",thumb.toString());
                                                    map.put("video",video.toString());
                                                    map.put("title",title);
                                                    map.put("description",description);
                                                    map.put("date",date);
                                                    map.put("key",key);
                                                    ContentModel model = new ContentModel(null,title,description,video.toString(),thumb.toString(),key,date);
                                                    reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
//                                                                progressDialog.dismiss();
//                                                                Toast.makeText(UploadActivity.this, "send", Toast.LENGTH_SHORT).show();
                                                            }else {
//                                                                progressDialog.dismiss();
//                                                                Toast.makeText(UploadActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    firestore.collection("Videos").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()){
                                                                progressDialog.dismiss();
                                                                startActivity(new Intent(UploadActivity.this,VideoActivity.class));
                                                                Toast.makeText(UploadActivity.this, "send", Toast.LENGTH_SHORT).show();
                                                            }else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(UploadActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }
                    });

                }else {
                    Toast.makeText(UploadActivity.this, "Please Insert data", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //bottom dialog gallery and video id find
    private void initVideoThumb(){
        btnVideoThumbnail = findViewById(R.id.btnVideoThumbnail);
        btnVideoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomDialog = new BottomSheetDialog(UploadActivity.this);
                bottomDialog.setContentView(R.layout.bottom_dialog);
                bottomDialog.setCancelable(true);
                bottomDialog.setCanceledOnTouchOutside(false);
                bottomDialog.show();

                ImageView imgClose;
                LinearLayout llGallery,llVideo;
                imgClose = bottomDialog.findViewById(R.id.imgClose);
                llGallery = bottomDialog.findViewById(R.id.llGallery);
                llVideo = bottomDialog.findViewById(R.id.llVideo);
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomDialog.dismiss();
                    }
                });

                //open gallery file
                llGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent iGallery = new Intent();
                        iGallery.setType("image/*");
                        iGallery.setAction(Intent.ACTION_GET_CONTENT);
                        imgThumbnail.setVisibility(View.VISIBLE);
                        startActivityForResult(Intent.createChooser(iGallery, "Select image"), REQUEST_CODE_GALLERY);
                        bottomDialog.dismiss();
                    }
                });
                //open video file
                llVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent iVideo = new Intent();
                        iVideo.setType("video/*");
                        iVideo.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(iVideo, "Select Video"), REQUEST_CODE_VIDEO);
                        bottomDialog.dismiss();
                    }
                });

            }
        });
    }

    //video and image uri find
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            //thumbnail uri
            if (requestCode == REQUEST_CODE_GALLERY) {
                thumbnailUri = data.getData();
                imgThumbnail.setImageURI(thumbnailUri);

                //video uri
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                videoUri = data.getData();
                videoView.setVideoURI(videoUri);
                videoView.start();
                MediaController controller = new MediaController(UploadActivity.this);
                controller.setMediaPlayer(videoView);
                videoView.setMediaController(controller);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}