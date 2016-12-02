package com.photointent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class TakeVideo extends Activity
{

    public static final int RECORD_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 4;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    private final int CAMERA_PIC_REQUEST = 1888, REQ_CODE_PICK_IMAGE = 1, VIDEO_REQUEST=022, PICK_VIDEO_REQUEST=032;

    public static String path = "";
    private final String capture_dir = Environment.getExternalStorageDirectory() + "/Retrofit/";
    Activity activity;
    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    Uri imageFileUri;


    public TakeVideo() {}

    public TakeVideo(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String image_from = b.getString("from");
            if (image_from.equalsIgnoreCase("camera"))
            {
                File file = new File(capture_dir);
                if (!file.exists())
                {
                    file.mkdirs();
                }
                path = capture_dir + System.currentTimeMillis() + ".mp4";
                imageFileUri = Uri.fromFile(new File(path));
                if (!marshMallowPermission.checkPermissionForCamera())
                {
                    marshMallowPermission.requestPermissionForCamera();
                }
                else
                {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    // create a file to save the video
                    imageFileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_VIDEO));

                    // set the image file name
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);

                    // set the video image quality to high
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                    // start the Video Capture Intent
                    startActivityForResult(intent, VIDEO_REQUEST);
                }
            }
            else if (image_from.equalsIgnoreCase("gallery"))
            {

                if (!marshMallowPermission.checkPermissionForExternalStorage())
                {
                    marshMallowPermission.requestPermissionForExternalStorage();
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    intent.setType("video/*");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, PICK_VIDEO_REQUEST);
                }
            }
        }

    }

    public static final int MEDIA_TYPE_VIDEO = 2;
    private static File getOutputMediaFile(int type)
    {
        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraVideo");

        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }


        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;

        if(type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");

        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (!marshMallowPermission.checkPermissionForExternalStorage())
                    {
                        marshMallowPermission.requestPermissionForExternalCameraStorage();
                    }
                    else
                    {

                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                        startActivityForResult(takeVideoIntent, VIDEO_REQUEST);
                    }
                }
                else
                {
                    onBackPressed();
                }
                return;
            }
            case CAMERA_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                    startActivityForResult(takeVideoIntent, VIDEO_REQUEST);
                }
                else
                {
                    onBackPressed();
                }
                return;
            }
            case EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    intent.setType("video/*");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, PICK_VIDEO_REQUEST);
                }
                else
                {
                    onBackPressed();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == PICK_VIDEO_REQUEST)
            {
                Uri selectedImage = data.getData();
                if (selectedImage != null)
                {
                    Cursor cursor = getContentResolver().query(selectedImage,
                            new String[]{MediaStore.Video.VideoColumns.DATA},
                            null,
                            null,
                            null);
                    cursor.moveToFirst();
                    path = cursor.getString(0);
//                    setImageCrop(path, 0);
                    cursor.close();
                    Intent intent = new Intent();
                    // path = "file://"+path;
                    intent.putExtra("filePath", path);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
            if (requestCode == VIDEO_REQUEST) {
                try {
//                    setImageCrop(path, 1);
                    Intent intent = new Intent();
                    // path = "file://"+path;
                    intent.putExtra("filePath", imageFileUri.getPath());
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (final Exception e) {
                    e.printStackTrace();
                    path = "";
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }

        } else {
            path = "";
            setResult(RESULT_CANCELED);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void setImageCrop(final String path, int i) {
        try {
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            final int sample = Math.min(options.outWidth / 500,
                    options.outHeight / 250);
            options.inSampleSize = sample;
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[16 * 1024];
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            int orientation = 0;
            try {
                final ExifInterface exif = new ExifInterface(path);
                final String exifOrientation = exif
                        .getAttribute(ExifInterface.TAG_ORIENTATION);
                orientation = Integer.valueOf(exifOrientation);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            final Matrix mat = new Matrix();
            if (orientation == 1) {
                mat.postRotate(0);
            } else if (orientation == 6) {
                mat.postRotate(90);
            } else if (orientation == 8) {
                mat.postRotate(270);
            } else if (orientation == 3) {
                mat.postRotate(180);
            }

            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                    mat, true);
            savebitmap(bm, path);
            bm.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    private void savebitmap(final Bitmap img, final String imagePath) {
        try {
            final File f = new File(imagePath);
            if (f.isFile()) {
                f.delete();
            }
            img.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(
                    new File(imagePath)));
            final File f1 = new File(imagePath);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    public class MarshMallowPermission {

        Activity activity;

        public MarshMallowPermission(Activity activity) {
            this.activity = activity;
        }

        public boolean checkPermissionForRecord() {
            int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }

        public boolean checkPermissionForExternalStorage() {
            int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }

        public boolean checkPermissionForCamera() {
            int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }

        public void requestPermissionForRecord() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(activity, "Microphone permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_REQUEST_CODE);
            }
        }

        public boolean requestPermissionForExternalStorage() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            }
            return true;
        }

        public boolean requestPermissionForExternalCameraStorage() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            }
            return true;
        }

        public boolean requestPermissionForCamera() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                Toast.makeText(activity, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
            return true;
        }
    }
}