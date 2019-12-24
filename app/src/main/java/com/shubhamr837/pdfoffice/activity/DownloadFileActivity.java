package com.shubhamr837.pdfoffice.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shubhamr837.pdfoffice.R;

public class DownloadFileActivity extends AppCompatActivity {
String type;
public TextView file_name;
public Button download_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_file);
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
        file_name=(TextView)findViewById(R.id.fileName);
        download_button=(Button)findViewById(R.id.download_button);
        file_name.setText("feature under development");
        download_button.setText("Feature Under Development");
        download_button.setTextColor(getResources().getColor(R.color.white));

        switch (type)
        {
            case "pdf":

            case "docx":

            case "txt":

            case "img":
        }
    }

}
