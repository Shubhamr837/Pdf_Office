package com.shubhamr837.pdfoffice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.shubhamr837.pdfoffice.activity.PdfFiles;
import com.shubhamr837.pdfoffice.adapters.GridAdapter;
import com.shubhamr837.pdfoffice.activity.EmailPasswordActivity;

import java.io.File;
import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemClickListener
{
    FirebaseUser user ;
    private static final int AUTHENTICATION_REQUEST_CODE = 1;
    public Vector<File> pdf_files =new Vector<>() ;
    boolean sorted=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        findViewById(R.id.pdf_reader).setOnClickListener(this);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new GridAdapter(this));
        gridview.setOnItemClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
            authenticate();
        Thread scan_files = new Thread(){
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
                    }
                }
                //bubble sort files by date
                while(!sorted)
                {   sorted=true;
                    int j=0;
                    while(j<pdf_files.size()-1){
                        if(pdf_files.get(j).lastModified()<pdf_files.get(j+1).lastModified())
                        {   sorted=false;
                            Collections.swap(pdf_files,j,j+1);
                        }
                        j++;
                    }
                }


            }
        };
        scan_files.start();

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
          Intent pdf_files = new Intent(this,PdfFiles.class);
          startActivity(pdf_files);
         }
    }
    @Override
    public void onItemClick(AdapterView<?> a, View v, int position,long id)
    {

        switch (position){
            case 0:
        }
    }


}

