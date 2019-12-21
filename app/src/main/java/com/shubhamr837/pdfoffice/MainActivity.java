package com.shubhamr837.pdfoffice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.activity.ImageSelectionActivity;
import com.shubhamr837.pdfoffice.activity.FilesSelection;
import com.shubhamr837.pdfoffice.adapters.FilesListAdapter;
import com.shubhamr837.pdfoffice.adapters.GridAdapter;
import com.shubhamr837.pdfoffice.activity.EmailPasswordActivity;
import com.shubhamr837.pdfoffice.utils.Utils;

import java.io.File;
import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

import static com.shubhamr837.pdfoffice.utils.Utils.bubbleSort;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemClickListener
{
    FirebaseUser user ;
    private static final int AUTHENTICATION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE_FILES=1;
    public Vector<File> pdf_files = new Vector<>() ;
    public Vector<File> doc_files = new Vector<>();
    public Vector<File> txt_files = new Vector<>();
    public static Thread scan_files;
    CustomDialogFragment customDialogFragment;


    public Integer[] mThumbIds = {
            R.drawable.pdf_to_word, R.drawable.word_to_pdf,
            R.drawable.pdf_to_text,R.drawable.text_to_pdf,
            R.drawable.pdf_to_image,R.drawable.image_to_pdf
    };
    public Integer[] mStrings = {
            R.string.pdf_to_word,R.string.word_to_pdf,
            R.string.pdf_to_text,R.string.text_to_pdf,
            R.string.pdf_to_image,R.string.image_to_pdf
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red));
        }
        findViewById(R.id.pdf_reader).setOnClickListener(this);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        scan_files = new Thread(){
            public void run(){
                File f = Environment.getExternalStorageDirectory();
                Stack<File> stack = new Stack<File>();
                stack.push(f);
                while(!stack.isEmpty()) {
                    f = stack.pop();
                    File[] file = f.listFiles();
                    for (File ff : file) {
                        if (ff.isDirectory()) stack.push(ff);
                        else if (ff.isFile() && ff.getPath().endsWith(".pdf")) {
                            pdf_files.add(ff);

                        }
                        else if (ff.isFile() &&( ff.getPath().endsWith(".doc")||ff.getPath().endsWith(".DOC"))){
                            doc_files.add(ff);
                        }
                        else if (ff.isFile() && ff.getPath().endsWith(".txt")){
                            txt_files.add(ff);
                        }
                    }
                }
                bubbleSort(pdf_files);bubbleSort(doc_files);bubbleSort(txt_files);
                customDialogFragment.dismiss();


            }
        };

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NETWORK_STATE)&&ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_WIFI_STATE)) {

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


        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
            authenticate();
        else
        {customDialogFragment = new CustomDialogFragment("Please wait","Searching all Pdf files...",false);
         customDialogFragment.setCancelable(false);
         customDialogFragment.show(getSupportFragmentManager(),"Sacnning Files ...");
        }
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
                    customDialogFragment = new CustomDialogFragment("Please wait","Searching all Pdf files...",false);
                    customDialogFragment.setCancelable(false);
                    customDialogFragment.show(getSupportFragmentManager(),"Sacnning Files ...");

                }
                else if(resultCode==RESULT_CANCELED){
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Authentication failure",Toast.LENGTH_SHORT).show();
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
                  intent.putExtra("to","doc");
                  intent.putExtra("tittle","select a file");
                  intent.putExtra("intent","Convert to Doc");
                  FilesListAdapter.files=pdf_files;
                  startActivity(intent);
                  break;

            case 1:intent = new Intent(this, FilesSelection.class);
                   intent.putExtra("type","doc");
                   intent.putExtra("to","pdf");
                   intent.putExtra("tittle","select a file");
                   FilesListAdapter.files=doc_files;
                   startActivity(intent);
                   break;

            case 2:intent = new Intent(this, FilesSelection.class);
                intent.putExtra("type","pdf");
                intent.putExtra("to","txt");
                intent.putExtra("tittle","select a file");
                FilesListAdapter.files=pdf_files;
                startActivity(intent);
                break;

            case 3:intent = new Intent(this, FilesSelection.class);
                intent.putExtra("type","txt");
                intent.putExtra("to","pdf");
                intent.putExtra("tittle","select a file");
                FilesListAdapter.files=txt_files;
                startActivity(intent);
                break;
            case 4:intent = new Intent(this, FilesSelection.class);
                intent.putExtra("type","pdf");
                intent.putExtra("to","img");
                intent.putExtra("tittle","select a file");
                FilesListAdapter.files=pdf_files;
                startActivity(intent);
                break;

            case 5:
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
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scan_files.start();
                } else {
                  finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }




}

