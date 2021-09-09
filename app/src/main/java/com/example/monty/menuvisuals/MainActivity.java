package com.example.monty.menuvisuals;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Toolbar toolbar;
    File file,f;
    Uri uri;
    Intent camIntent;
    final int REQUESTPERMISSIONCODE = 1;
    TessBaseAPI mTess;
    private String datapath = "";
    private String mImageFileLocation = "";
    String OCRresult = "";
    byte[] b = null;
    Cursor c = null;
    Semaphore semaphore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Visuals");
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView);
        datapath = getFilesDir()+ "/tesseract/";

        //make sure training data has been copied
        checkFile(new File(datapath + "tessdata/"));
        //initialize Tesseract API
        String lang = "eng";
        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);

        semaphore = new Semaphore(1);
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            RequestRunTimePermission();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void RequestRunTimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUESTPERMISSIONCODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_camera) {
            cameraOpen();
        }
        else if (item.getItemId() == R.id.btn_seedish){
            seeDish();
        }
        return true;
    }
    //See dish function
    private void seeDish() {
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        try {
            databaseHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            databaseHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        Toast.makeText(MainActivity.this, "Successfully Imported", Toast.LENGTH_SHORT).show();
        Cursor c = databaseHelper.takeString(OCRresult);
        //Cursor c = databaseHelper.takeString("lasagne");
        int index = OCRresult.length();
        if (c != null) {
            byte[] b = c.getBlob(c.getColumnIndex("Image"));
            String s = c.getString(c.getColumnIndex("Taste"));
            TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
            //OCRTextView.setText(databaseHelper.getTaste());
            OCRTextView.setText(s);
        }
        else {
            Toast.makeText(this, "OOPS!!! The item you scanned is not the one we know... Please help and add", Toast.LENGTH_LONG).show();
        }
    }

    private void cameraOpen() {
        camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(),
                "File" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camIntent.putExtra("return-data", true);
        mImageFileLocation = file.getAbsolutePath();
        Toast.makeText(this,uri.toString(),Toast.LENGTH_LONG).show();
        startActivityForResult(camIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            cropImage();
        } else if (resultCode == 2) {
            if (data != null) {
                uri = data.getData();
                cropImage();
            }
        } else {
            if (requestCode == 1) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = bundle.getParcelable("data");
                    imageView.setImageBitmap(bitmap);
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/Shopic Snaps");

                    if(!myDir.exists())
                        myDir.mkdirs();

                    String fname = "Image_.jpg";
                    File file = new File (myDir, fname);

                    //if (file.exists ())
                    //  file.delete ();

                    try
                    {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    OCRresult ="";
                    mImageFileLocation = file.getAbsolutePath();
                    Bitmap originalPhoto = BitmapFactory.decodeFile(mImageFileLocation);
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                    mTess.setImage(originalPhoto);
                    OCRresult = mTess.getUTF8Text();
                    TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
                    OCRTextView.setText(OCRresult);
                }
            }
        }
    }

    private void cropImage() {
        try {
            Intent cropIntrent = new Intent("com.android.camera.action.CROP",null)
                    .setDataAndType(uri,"image/*")
                    .putExtra("crop","true")
                    .putExtra("scale",true)
                    .putExtra("return-data",true)
                    .putExtra("scaleUpIfNeeded",true)
                    .putExtra(MediaStore.EXTRA_OUTPUT,uri)
                    .putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(cropIntrent , 1);
        }
        catch (ActivityNotFoundException ex){
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUESTPERMISSIONCODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}