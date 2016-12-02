package com.photointent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    private String picpath = "";
    File file;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST = 1888;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        image=(ImageView)findViewById(R.id.image);
        findViewById(R.id.btv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForUpload();
            }
        });
    }
    public void showDialogForUpload() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ImageActivity.this);
        alertDialogBuilder.setMessage("Upload Picture");

        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ImageActivity.this, TakeImage.class);
                        intent.putExtra("from", "gallery");
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
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
                Intent intent = new Intent(ImageActivity.this, TakeImage.class);
                intent.putExtra("from", "camera");
                startActivityForResult(intent, CAMERA_REQUEST);
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
            if (requestCode == RESULT_LOAD_IMAGE || requestCode == CAMERA_REQUEST)
            {
                if (resultCode == RESULT_OK)
                {
                    picpath = data.getStringExtra("filePath");
                    if (picpath != null) {
                        file = new File(data.getStringExtra("filePath"));
                        if (file.exists())
                        {
                            image.setImageURI(Uri.fromFile(file));
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
