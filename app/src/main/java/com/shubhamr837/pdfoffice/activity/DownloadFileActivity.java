package com.shubhamr837.pdfoffice.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.utils.CommonConstants;
import com.shubhamr837.pdfoffice.utils.HttpUtils;
import com.shubhamr837.pdfoffice.utils.Packager;
import com.shubhamr837.pdfoffice.utils.Utils;


import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;

import static com.shubhamr837.pdfoffice.utils.Packager.unzip;

public class DownloadFileActivity extends AppCompatActivity implements View.OnClickListener {
public String type;
private TextView file_name;
private String download_link;
private Button download_button;
private ImageView file_icon;
private URL url;
private Intent calling_intent;
private HttpUtils httpUtils = new HttpUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calling_intent = getIntent();
        setContentView(R.layout.activity_download_file);
        file_name=(TextView)findViewById(R.id.fileName);
        download_button=(Button)findViewById(R.id.download_button);
        file_icon = (ImageView)findViewById(R.id.fileIcon);
        download_link = calling_intent.getExtras().getString(CommonConstants.DOWNLOAD_LINK_KEY);


        file_name.setText("");
        download_button.setText("Download");
        download_button.setTextColor(getResources().getColor(R.color.white));
        download_button.setOnClickListener(this::onClick);

        ActionBar actionBar = getSupportActionBar();
        type = getIntent().getExtras().getString("type");
        actionBar.setTitle("Download "+ type);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red));
        }


        switch (type)
        {
            case "pdf": file_icon.setImageResource(R.drawable.pdf_icon);

            break;

            case "docx": file_icon.setImageResource(R.drawable.doc_icon);

            break;
            case "txt": file_icon.setImageResource(R.drawable.txt_icon);

            break;
            case "img": file_icon.setImageResource(R.drawable.image_icon);

            break;
        }
    }

    @Override
    public void onClick(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    switch (view.getId()){

        case R.id.download_button :
            DownloadTask downloadTask = new DownloadTask(this,new CustomDialogFragment("Downloading file","please wait",false));
            try {
                downloadTask.execute(new URL(download_link));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            break;
    }
    }
    private class DownloadTask extends AsyncTask<URL,Integer, File> {
        private Context context;
        public CustomDialogFragment customDialogFragment;
        private File downloaded_file;
        private File download_folder;


        public DownloadTask(Context context,CustomDialogFragment customDialogFragment )
        {
            this.context=context;
            this.customDialogFragment=customDialogFragment;
            download_folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfReader");
            download_folder.mkdirs();

        }


        @Override
        protected File doInBackground(URL... urls) {
            URL url = urls[0] ;
            HttpURLConnection httpURLConnection ;
            System.out.println("Download link"+url.toString());
            customDialogFragment.setCancelable(false);
            customDialogFragment.show(((DownloadFileActivity)context).getSupportFragmentManager(),"downloading_file");
            String file_name = java.util.UUID.randomUUID().toString();
            if(file_name.length()>30){
                file_name = file_name.substring(0 , 30);
            }

            if(type.equals("img")){
                try {
                    downloaded_file = File.createTempFile("temp","file.zip");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(type.equals("pdf")) {
                try{
                    downloaded_file = new File(download_folder,file_name + ".pdf");
                    try{downloaded_file.createNewFile();}catch (Exception e){e.printStackTrace();}
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(type.equals("docx"))
            {
                try{
                    downloaded_file = new File(download_folder,file_name+".docx");
                    downloaded_file.createNewFile();
                }
                catch (Exception e){
                    e.printStackTrace();
                } }
            else if(type.equals("txt"))
            {
                downloaded_file=new File(download_folder,file_name+".txt");
                try{downloaded_file.createNewFile();}catch (Exception e){e.printStackTrace();}
                }


            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(new URL(download_link).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(downloaded_file);
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                in.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                // handle exception
            }
            System.out.println(type);
            System.out.println("File saved to " + downloaded_file.getAbsolutePath());
            return downloaded_file;

        }
        protected void onPostExecute(File file) {
            if(type.equals("img"))
            {
                ArrayList<File> fileArrayList = Packager.unzip(downloaded_file.getAbsolutePath(),download_folder.getAbsolutePath(),"jpg",context);
                for(File mfile: fileArrayList){
                    Utils.addImageToGallery(mfile.getAbsolutePath(),context);
                }
                downloaded_file.delete();
                Toast.makeText(context,"File saved to "+download_folder.getAbsolutePath() +" directory",Toast.LENGTH_SHORT).show();
                return;


            }
            customDialogFragment.dismiss();
            Toast.makeText(context,"File saved to "+downloaded_file.getAbsolutePath() +" directory",Toast.LENGTH_SHORT).show();

            ((DownloadFileActivity)context).finish();

        }
    }
}
