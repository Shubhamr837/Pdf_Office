package com.shubhamr837.pdfoffice.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.shubhamr837.pdfoffice.R;

import java.io.File;

public class PdfReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pdf_intent = getIntent().getExtras().getString("intent");
        if(pdf_intent.startsWith("Convert"))
        {setContentView(R.layout.activity_pdf_read_and_convert);
         findViewById(R.id.convert_button);
        }
        else
            setContentView(R.layout.activity_pdf_read);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(pdf_intent);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red));
        }
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        File pdf_file = new File(getIntent().getExtras().getString("file_path"));
        pdfView.fromFile(pdf_file).load();

    }
}
