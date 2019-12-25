package com.shubhamr837.pdfoffice.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.adapters.GridAdapter;
import com.shubhamr837.pdfoffice.utils.Packager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ImageSelectionActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener{
    int PICK_IMAGE=1;
    public Integer[] mThumbIds = {
            R.drawable.camera_icon,R.drawable.gallery_icon
    };
    public Integer[] mStrings = {
            R.string.select_from_camera,R.string.select_from_gallery
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Image");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        GridView gridview = (GridView) findViewById(R.id.gridview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red));
        }

        gridview.setAdapter(new GridAdapter(this,mThumbIds,mStrings));
        gridview.setOnItemClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent;
        switch (position){
            case 0:

            case 1:
                intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);


        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File zip_file = new File(getObbDir()+"/temp_zip.zip");
        Uri imagePath;
        CustomDialogFragment customDialogFragment;
        if(requestCode == PICK_IMAGE) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getClipData() != null) {
                    customDialogFragment = new CustomDialogFragment("Converting Files","Please wait...",true);
                    customDialogFragment.setCancelable(false);
                    customDialogFragment.show(getSupportFragmentManager(),"Convert File Fragment");
                    SendImages sendImages = new SendImages(data,customDialogFragment,this);
                    sendImages.execute(zip_file);
                }
            } else if(data.getData() != null) {
                imagePath = data.getData();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }
    }
    private class SendImages extends AsyncTask<File , Integer , Intent>{

        Uri imagePath;
        ArrayList<File> files= new ArrayList<>();
        File image_file;
        CustomDialogFragment customDialogFragment;
        Intent data;
        JSONObject jsonObject;
        Context context;

        public SendImages(Intent data, CustomDialogFragment customDialogFragment, Context context){
            this.data = data;
            this.customDialogFragment = customDialogFragment;
        }

        @Override
        protected Intent doInBackground(File... f) {

            File zip_file = f[0];
            Intent downloadActivityIntent;
            int bytesRead;

            ByteArrayOutputStream bos= new ByteArrayOutputStream();

            int count = data.getClipData().getItemCount();
            System.out.println("data is "+ count);//evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
            for(int i = 0; i < count; i++)
            { imagePath = data.getClipData().getItemAt(i).getUri();
                final InputStream imageStream;
                try {
                    image_file = new File(getObbDir(),"image_"+i+".jpg");
                    FileOutputStream fileOutputStream = new FileOutputStream(image_file);
                    imageStream = getContentResolver().openInputStream(imagePath);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream);
                    fileOutputStream.close();
                    files.add(image_file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Packager.packZip(zip_file,files);
            } catch (IOException e) {
                e.printStackTrace();
            }




            System.out.println("Temp Zip File created at "+zip_file.getAbsolutePath());



            try {
                URL url = new URL("https://www.google.com/");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type","application/x-binary; utf-8");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();
                OutputStream out = httpURLConnection.getOutputStream();
                FileInputStream in = new FileInputStream(zip_file);
                byte[] buffer = new byte[1024];
                while (true) {
                    bytesRead = in.read(buffer);
                    if (bytesRead == -1)
                        break;
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                in.close();
                InputStream inputStream;
                Thread.sleep(2000);
                if (httpURLConnection.getResponseCode() < 400) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                buffer = new byte[1024];
                while (-1 != (bytesRead = inputStream.read(buffer))) {
                    bos.write(buffer, 0, bytesRead);
                }
                jsonObject = new JSONObject(new String(bos.toByteArray()));


            } catch (Exception e) {
                e.printStackTrace();
            }

            downloadActivityIntent = new Intent(context, DownloadFileActivity.class);
            downloadActivityIntent.putExtra("type","pdf");
            if(jsonObject!=null)
                try {
                    downloadActivityIntent.putExtra("download_link",jsonObject.getString("download-link"));
                    downloadActivityIntent.putExtra("file_name",jsonObject.getString("file_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return downloadActivityIntent;
        }

        protected void onPostExecute(Intent downloadActivityIntent) {
            if(downloadActivityIntent!=null){
                context.startActivity(downloadActivityIntent);
                customDialogFragment.dismiss();
            }
        }
    }
}

