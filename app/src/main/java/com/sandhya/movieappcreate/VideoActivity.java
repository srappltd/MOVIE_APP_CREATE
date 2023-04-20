package com.sandhya.movieappcreate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandhya.movieappcreate.adapter.ContentAdapter;
import com.sandhya.movieappcreate.model.ContentModel;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    RecyclerView videoRecycler;
    DatabaseReference reference;
    ArrayList<ContentModel> arrModels;
    ContentAdapter adapter;
    ContentModel contentModel;
    Toolbar videoToolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoToolbar = findViewById(R.id.videoToolbar);
        videoRecycler = findViewById(R.id.videoRecycler);
        videoRecycler.setLayoutManager(new GridLayoutManager(this,2));
        arrModels = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Contents");
        ValueEventListener valueEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    contentModel = dataSnapshot.getValue(ContentModel.class);
                    arrModels.add(contentModel);
                }
                adapter = new ContentAdapter(VideoActivity.this, arrModels);
                videoRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        videoToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.share:
                        //  Toast.makeText(VideoActivity.this, "share", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String shareBody = "Download SR Movie Player from the Google Play Store: https://play.google.com/store/apps/details?id="+getPackageName();
                        String shareSubject = "SR Movie Player";
                        intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        intent.putExtra(Intent.EXTRA_REFERRER_NAME,"Sandhya");
                        startActivity(Intent.createChooser(intent, "Share using"));
                        break;
                    case R.id.setting:
                        Toast.makeText(VideoActivity.this, "setting", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

    }

}