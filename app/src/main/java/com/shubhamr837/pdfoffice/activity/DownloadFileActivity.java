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
import android.widget.ImageView;
import android.widget.TextView;

import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.utils.HttpUtils;

import java.net.URL;

public class DownloadFileActivity extends AppCompatActivity implements View.OnClickListener {
public String type;
private TextView file_name;
private Button download_button;
private ImageView file_icon;
private URL url;
private HttpUtils httpUtils = new HttpUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_file);
        file_name=(TextView)findViewById(R.id.fileName);
        download_button=(Button)findViewById(R.id.download_button);
        file_icon = (ImageView)findViewById(R.id.fileIcon);


        file_name.setText("feature under development");
        download_button.setText("Feature Under Development");
        download_button.setTextColor(getResources().getColor(R.color.white));

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

    switch (view.getId()){

        case R.id.download_button :
            httpUtils.downloadFile(url,type);
    }
    }
}
