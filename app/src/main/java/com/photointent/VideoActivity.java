package com.photointent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class VideoActivity extends AppCompatActivity {

    String picpath="";
    File file;
    private static int RESULT_LOAD_VIDEO= 1;
    private static final int VIDEO_REQUEST = 1888;
    VideoView video;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        video=(VideoView)findViewById(R.id.video);
        findViewById(R.id.btv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
showDialogForUpload();
            }
        });
        MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(video);
        video.setMediaController(mediaController);

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                video.start();
                Log.i("VideoActivity", "Duration = " + video.getDuration());
            }
        });

    }

    public void showDialogForUpload() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VideoActivity.this);
        alertDialogBuilder.setMessage("Upload Video");

        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(VideoActivity.this, TakeVideo.class);
                        intent.putExtra("from", "gallery");
                        startActivityForResult(intent, RESULT_LOAD_VIDEO);
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(VideoActivity.this, TakeVideo.class);
                intent.putExtra("from", "camera");
                startActivityForResult(intent, VIDEO_REQUEST);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if (requestCode == RESULT_LOAD_VIDEO || requestCode == VIDEO_REQUEST)
            {
                if (resultCode == RESULT_OK)
                {
                    picpath = data.getStringExtra("filePath");
                    if (picpath != null) {
                        file = new File(data.getStringExtra("filePath"));
                        if (file.exists())
                        {
                            video.setVideoURI(Uri.fromFile(file));
                        }
                    } else {
                        picpath = "";
                    }
                    Log.i("File Path", "" + picpath);

                }
            }
        }

    }

}
