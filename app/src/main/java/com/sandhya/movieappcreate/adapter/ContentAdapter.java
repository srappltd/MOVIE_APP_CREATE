package com.sandhya.movieappcreate.adapter;

import static androidx.core.content.FileProvider.getUriForFile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sandhya.movieappcreate.FullActivity;
import com.sandhya.movieappcreate.R;
import com.sandhya.movieappcreate.UpdateContentActivity;
import com.sandhya.movieappcreate.model.ContentModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import firebase.com.protolitewrapper.BuildConfig;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    Context context;
    ArrayList<ContentModel> models;

    DatabaseReference referenceDel;
    Uri thumbUri;

    public ContentAdapter(Context context, ArrayList<ContentModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ContentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContentAdapter.ViewHolder holder, int position) {
        ContentModel model = models.get(position);
        holder.txtTitle.setText(model.getTitle());
        holder.txtDate.setText(model.getDate());
        Glide.with(context).load(model.getThumb()).placeholder(R.drawable.ic_launcher_background).into(holder.imgThumb);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullActivity.class);
                intent.putExtra("title",model.getTitle());
                intent.putExtra("description",model.getDescription());
                intent.putExtra("video",model.getVideo());
                context.startActivity(intent);
            }
        });
        //update video title
//        holder.txtUpdate.setVisibility(View.VISIBLE);
        holder.txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateContentActivity.class);
                intent.putExtra("updateTitle",model.getTitle());
                intent.putExtra("updateDescription",model.getDescription());
                intent.putExtra("updateThumbnail",model.getThumb());
                intent.putExtra("key",model.getKey());
                context.startActivity(intent);
            }
        });
        //remove video
//        holder.imgClose.setVisibility(View.VISIBLE);
        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder a = new AlertDialog.Builder(context);
                a.setTitle("Delete Video");
                a.setMessage("delete video...");
                a.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        referenceDel = FirebaseDatabase.getInstance().getReference().child("Contents");
                        referenceDel.child(model.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Delete Video", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                a.setCancelable(false);
                a.show();
            }
        });

        // Load image with Picasso
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(model.getThumb());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.imgThumb);
            }
        });
        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/text/plain/video/*");
                shareIntent.putExtra(Intent.EXTRA_TEXT, model.getTitle());
                // Add custom link with app package name to share intent
                String packageName = context.getPackageName();
                String link = "\n\nhttps://movieappcreate.page.link/" + packageName;
                shareIntent.putExtra(Intent.EXTRA_TEXT, link);

                Uri videoUri = Uri.parse(model.getVideo());
                shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);

                // Get bitmap from ImageView
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.imgThumb.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                // Save bitmap to external storage
                String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Share image", null);
                Uri bitmapUri = Uri.parse(bitmapPath);

                // Create dynamic link
                DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://movieappcreate.page.link/home/"))
                        .setDomainUriPrefix("https://movieappcreate.page.link")
//                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                        .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
//                                .setTitle(model.getTitle())
//                                .setDescription(model.getDescription())
//                                .setImageUrl(bitmapUri)
//                                .build())
                        .buildDynamicLink();

                Uri dynamicLinkUri = dynamicLink.getUri();

                // Add link to share intent
                shareIntent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString());

                // Set WhatsApp package name
                shareIntent.setPackage("com.whatsapp");

                // Add image to share intent
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);

                context.startActivity(Intent.createChooser(shareIntent, "Share image"));
            }
        });

    }



    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb,imgClose,imgShare;
        TextView txtTitle,txtUpdate,txtDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtUpdate = itemView.findViewById(R.id.txtUpdate);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgClose = itemView.findViewById(R.id.imgClose);
            imgShare = itemView.findViewById(R.id.imgShare);
        }
    }
}
