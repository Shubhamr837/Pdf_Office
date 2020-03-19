package com.shubhamr837.pdfoffice.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.adapters.GridAdapter;
import com.shubhamr837.pdfoffice.utils.CommonConstants;
import com.shubhamr837.pdfoffice.utils.Packager;
import com.shubhamr837.pdfoffice.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ImageSelectionActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener{
    private static final int PICK_IMAGE=1;
    private static final int DOWNLOAD_ACTIVITY_REQUEST_CODE=2;
    private static Intent downloadActivityIntent ;
    private Intent Cameraintent;
    public Integer[] mThumbIds = {
           R.drawable.image_icon
    };
    public Integer[] mStrings = {
            R.string.select_from_gallery
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
        downloadActivityIntent = new Intent(this, DownloadFileActivity.class);

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
        else if (requestCode == DOWNLOAD_ACTIVITY_REQUEST_CODE )
        {
            finish();
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
            this.context = context;
        }

        @Override
        protected Intent doInBackground(File... f) {

            File zip_file = f[0];

            int bytesRead;

            ByteArrayOutputStream bos= new ByteArrayOutputStream();

            int count = data.getClipData().getItemCount();
            System.out.println("data is "+ count);//evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
            for(int i = 0; i < count; i++)
            { imagePath = data.getClipData().getItemAt(i).getUri();
                final InputStream in;
                try {
                    image_file = new File(getObbDir(),"image_"+i+".png");
                    FileOutputStream out = new FileOutputStream(image_file);
                    in = getContentResolver().openInputStream(imagePath);
                    byte[] buffer = new byte[1024];
                    int lengthRead;
                    while ((lengthRead = in.read(buffer)) > 0) {
                        out.write(buffer, 0, lengthRead);
                        out.flush();
                    }
                    out.close();
                    in.close();
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

            Log.i("Temp Zip File created",zip_file.getAbsolutePath());

            try {
                URL url = new URL(CommonConstants.IMG_TO_PDF_CONVERSION_URL);
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(url.toString());

                InputStreamEntity reqEntity = new InputStreamEntity(
                        new FileInputStream(zip_file), -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
                //Do something with response..
                zip_file.delete();


            } catch (Exception e) {
                e.printStackTrace();
            }



            if(jsonObject!=null)
                try {
                    downloadActivityIntent.putExtra(CommonConstants.DOWNLOAD_LINK_KEY,jsonObject.getString("download_link"));
                    downloadActivityIntent.putExtra("file_name","Images");
                    downloadActivityIntent.putExtra("type","pdf");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return downloadActivityIntent;
        }

        protected void onPostExecute(Intent downloadActivityIntent) {
            if(downloadActivityIntent!=null){
                ((ImageSelectionActivity)context).startActivityForResult(downloadActivityIntent,DOWNLOAD_ACTIVITY_REQUEST_CODE);
                customDialogFragment.dismiss();
            }
        }
    }
}

