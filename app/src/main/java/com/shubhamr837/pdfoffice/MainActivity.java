package com.shubhamr837.pdfoffice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.activity.ImageSelectionActivity;
import com.shubhamr837.pdfoffice.activity.FilesSelection;
import com.shubhamr837.pdfoffice.adapters.FilesListAdapter;
import com.shubhamr837.pdfoffice.adapters.GridAdapter;
import com.shubhamr837.pdfoffice.activity.EmailPasswordActivity;
import com.shubhamr837.pdfoffice.utils.ScanFiles;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Vector;


public class MainActivity extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemClickListener
{
    private FirebaseUser user ;
    private static final int AUTHENTICATION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE_FILES=1;
    public static final String pdf_files_list = "PDF_FILES_LIST";
    public static final String txt_files_list = "TXT_FILES_LIST";
    public static final String doc_files_list = "DOC_FILES_LIST";
    public static MainActivity instance;

    public static Vector<File> pdf_files = new Vector<>() ;
    public static Vector<File> doc_files = new Vector<>() ;
    public static Vector<File> txt_files = new Vector<>() ;;
    public CustomDialogFragment customDialogFragment;
    public ScanFiles scanFiles = new ScanFiles(this);
    public Thread scan_files = new Thread(scanFiles);



    public Integer[] mThumbIds = {
            R.drawable.pdf_to_word,
            R.drawable.pdf_to_text,
            R.drawable.pdf_to_image,R.drawable.image_to_pdf

    };
    public Integer[] mStrings = {
            R.string.pdf_to_word,
            R.string.pdf_to_text,
            R.string.pdf_to_image,R.string.image_to_pdf

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance =  this;
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red));
        }
        findViewById(R.id.pdf_reader).setOnClickListener(this);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        getFiles();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NETWORK_STATE)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_WIFI_STATE)) {
                scan_files.start();

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_PERMISSIONS_REQUEST_READ_WRITE_FILES);

            }
        }
        else{
            scan_files.start();
        }
        gridview.setAdapter(new GridAdapter(this,mThumbIds,mStrings));
        gridview.setOnItemClickListener(this);


    }

    public void authenticate(){
        Intent authentication_intent = new Intent(this,EmailPasswordActivity.class);
        startActivityForResult(authentication_intent,AUTHENTICATION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode){
            case AUTHENTICATION_REQUEST_CODE:
                if(resultCode==RESULT_OK) {
                    Toast.makeText(getApplicationContext(),"Sucessfully Signed in",Toast.LENGTH_SHORT).show();
                    if(scan_files.isAlive()){
                        customDialogFragment = new CustomDialogFragment("Please Wait","Scanning files ...",false);
                        customDialogFragment.setCancelable(false);
                        customDialogFragment.show(getSupportFragmentManager(),"scan_files");
                        while(scan_files.isAlive());
                        customDialogFragment.dismiss();

                    }
                }
                else if(resultCode==RESULT_CANCELED){
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Authentication failure",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
    @Override
    public void onClick(View v){
        int id=v.getId();
        if(id==R.id.pdf_reader)
         {
          Intent pdf_files_intent = new Intent(this, FilesSelection.class);
          pdf_files_intent.putExtra("type","pdf");
          pdf_files_intent.putExtra("tittle","Open a file");
          pdf_files_intent.putExtra("intent","read");
          FilesListAdapter.files=pdf_files;
          startActivity(pdf_files_intent);
         }
    }
    @Override
    public void onItemClick(AdapterView<?> a, View v, int position,long id)
    {
        Intent intent;

        switch (position){
            case 0:
                  intent = new Intent(this, FilesSelection.class);
                  intent.putExtra("type","pdf");
                  intent.putExtra("to","docx");
                  intent.putExtra("tittle","select a file");
                  intent.putExtra("intent","Convert_to_docx");
                  FilesListAdapter.files=pdf_files;
                  startActivity(intent);
                  break;


            case 1:intent = new Intent(this, FilesSelection.class);
                intent.putExtra("type","pdf");
                intent.putExtra("to","txt");
                intent.putExtra("tittle","select a file");
                intent.putExtra("intent","Convert_to_txt");
                FilesListAdapter.files=pdf_files;
                startActivity(intent);
                break;

            case 2:intent = new Intent(this, FilesSelection.class);
                intent.putExtra("type","pdf");
                intent.putExtra("to","img");
                intent.putExtra("tittle","select a file");
                intent.putExtra("intent","Convert_to_img");
                FilesListAdapter.files=pdf_files;
                startActivity(intent);
                break;

            case 3:
                intent= new Intent(this,ImageSelectionActivity.class);
                startActivity(intent);
                break;

        }

    }
@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_WRITE_FILES: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && !scan_files.isAlive()) {
                    scan_files.start();
                } else {
                  finish();
                }
                return;
            }

        }
    }

    public void getFiles(){
        Type type;
        String json;
        Gson gson = new Gson();
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        if(appSharedPrefs.contains(pdf_files_list)&&appSharedPrefs.contains(doc_files_list)&&appSharedPrefs.contains(txt_files_list)) {
            json = appSharedPrefs.getString(pdf_files_list, "");
            type = new TypeToken<Vector<File>>() {
            }.getType();
            pdf_files = gson.fromJson(json, type);
            json = appSharedPrefs.getString(txt_files_list, "");
            txt_files = gson.fromJson(json, type);
            json = appSharedPrefs.getString(doc_files_list, "");
            doc_files = gson.fromJson(json, type);
        }
    }
    public static MainActivity getInstance(){
        return instance;
    }

}

