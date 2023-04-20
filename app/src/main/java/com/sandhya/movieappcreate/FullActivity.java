package com.sandhya.movieappcreate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FullActivity extends AppCompatActivity {

    PlayerView playerView;
    ProgressBar progressBar;
    ImageView btFullScreen;
    SimpleExoPlayer simpleExoPlayer;
    boolean flag = false;
    ImageView btQuality;

    Uri videoUri;

    // Reference for Firebase.
    DatabaseReference databaseReference;
    TextView txtTitle, txtDescription, txtTitleControl;
    CardView cardView;
    SeekBar volume_seek_ba;
    Spinner spinner;
    DefaultTrackSelector trackSelectors;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);
        playerView = findViewById(R.id.player_view);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Contents");

        //notification bar hide
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //method find
        initIdFind();
//        initExoplayerView();
        initBundlePassing();
        initFullScreen();
        initVolume();
//        initQuality();


        ///exoplayer find
        LoadControl loadControl = new DefaultLoadControl();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(FullActivity.this, trackSelector, loadControl);
        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory("exoplayer_video");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri, factory, extractorsFactory, null, null);
        Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
                null, Format.NO_VALUE, Format.NO_VALUE, "en", null, Format.OFFSET_SAMPLE_RELATIVE);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            }
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }
            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

    }

    public void initIdFind(){
        txtTitleControl = playerView.findViewById(R.id.txtTitleControl);
        progressBar = findViewById(R.id.progress_bar);
        txtDescription = findViewById(R.id.txtDes);
        txtTitle = findViewById(R.id.txtTitle);
        btFullScreen = playerView.findViewById(R.id.btFullscreen);
        volume_seek_ba = playerView.findViewById(R.id.volume_seek_ba);
        cardView = findViewById(R.id.cardView);
        btQuality = playerView.findViewById(R.id.bt_quality);

    }
    public void initBundlePassing(){
        Bundle bundle = getIntent().getExtras();
        String video = bundle.getString("video");
        String title = bundle.getString("title");
        String description = bundle.getString("description");
        txtTitle.setText(title);
        txtDescription.setText(description);
        txtTitleControl.setText(title);
        videoUri = Uri.parse(video);

    }

    public void initFullScreen(){
        btFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    flag = false;
                } else {
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    flag = true;

                }

            }
        });
    }

    public void initVolume(){
        volume_seek_ba.setMax(50);
        volume_seek_ba.setProgress(50);
        volume_seek_ba.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = (float) progress / 100;
                simpleExoPlayer.setVolume(volume);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public void initQuality() {
        spinner = playerView.findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, new String[]{"Low", "Medium", "High"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DefaultTrackSelector.ParametersBuilder parametersBuilder = new DefaultTrackSelector.ParametersBuilder();
                switch (position) {
                    case 0: // Low quality
                        parametersBuilder.setMaxVideoBitrate(500000); // 500 Kbps
                        break;
                    case 1: // Medium quality
                        parametersBuilder.setMaxVideoBitrate(1000000); // 1 Mbps
                        break;
                    case 2: // High quality
                        parametersBuilder.setMaxVideoBitrate(2000000); // 2 Mbps
                        break;
                    case 3: // Full HD quality
                        parametersBuilder.setMaxVideoBitrate(8000000); // 8 Mbps
                        break;
                }
                DefaultTrackSelector.Parameters parameters = parametersBuilder.build();
                trackSelectors.setParameters(parameters);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void initVideoView(){
        //videoView.setVideoURI(videoUri);
//        videoView.start();
//        MediaController controller = new MediaController(this);
//        controller.setMediaPlayer(videoView);
//        videoView.setMediaController(controller);
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.getPlaybackState();

    }

}