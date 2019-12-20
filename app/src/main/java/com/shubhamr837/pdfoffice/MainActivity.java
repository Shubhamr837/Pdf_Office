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
import com.shubhamr837.pdfoffice.activity.ImageSelectionActivity;
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
    public Vector<File> pdf_files = new Vector<>() ;
    public Vector<File> doc_files = new Vector<>();
    public Vector<File> txt_files = new Vector<>();
    boolean sorted=false;
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        findViewById(R.id.pdf_reader).setOnClickListener(this);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new GridAdapter(this,mThumbIds,mStrings));
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
                        else if (ff.isFile() && ff.getPath().endsWith(".doc")){
                            doc_files.add(ff);
                        }
                        else if (ff.isFile() && ff.getPath().endsWith(".txt")){
                            txt_files.add(ff);
                        }
                    }

                }
                //bubble sort files by date
                bubbleSort(pdf_files);
                bubbleSort(doc_files);
                bubbleSort(txt_files);
            }
        };
        scan_files.start();

    }
    public void bubbleSort(Vector<File> files){
        while(!sorted)
        {   sorted=true;
            int j=0;
            while(j<files.size()-1){
                if(files.get(j).lastModified()<files.get(j+1).lastModified())
                {   sorted=false;
                    Collections.swap(files,j,j+1);
                }
                j++;
            }
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
          Intent pdf_files_intent = new Intent(this,PdfFiles.class);
          pdf_files_intent.putExtra("files",pdf_files);
          pdf_files_intent.putExtra("type","pdf");
          startActivity(pdf_files_intent);
         }
    }
    @Override
    public void onItemClick(AdapterView<?> a, View v, int position,long id)
    {
        Intent intent;

        switch (position){
            case 0:

            case 1:

            case 2:

            case 3:

            case 4:

            case 5:
                intent= new Intent(this,ImageSelectionActivity.class);
                startActivity(intent);

        }
    }


}

