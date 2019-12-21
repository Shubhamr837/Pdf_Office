package com.shubhamr837.pdfoffice.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.R;

import java.io.File;
import java.net.HttpURLConnection;

import static com.shubhamr837.pdfoffice.utils.Utils.isNetworkConnected;

public class PdfReadActivity extends AppCompatActivity implements View.OnClickListener, OnPageScrollListener, OnLoadCompleteListener {

    private PDFView pdfView;
    private CustomDialogFragment customDialogFragment;
    public SeekBar seekBar;
    public TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pdf_intent = getIntent().getExtras().getString("intent");
        if(pdf_intent.startsWith("Convert"))
        {setContentView(R.layout.activity_pdf_read_and_convert);
            ((Button)findViewById(R.id.convert_button)).setOnClickListener(this);
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
        pdfView = (PDFView) findViewById(R.id.pdfView);
        File pdf_file = new File(getIntent().getExtras().getString("file_path"));
        pdfView.fromFile(pdf_file).onLoad(this).enableSwipe(true)
                .enableDoubletap(true)
                .onPageScroll(this)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .password(null).
                scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(2)
                .swipeHorizontal(true).load();
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        textView = (TextView)findViewById(R.id.textView);
        seekBar.setProgress(pdfView.getCurrentPage());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
                pdfView.jumpTo(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Page "+pval + "/" + seekBar.getMax());
            }
        });
        seekBar.setEnabled(true);





    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.convert_button :
                if(isNetworkConnected(this))
                {customDialogFragment = new CustomDialogFragment("Converting File","Please wait...",true);
                customDialogFragment.setCancelable(false);
                customDialogFragment.show(getSupportFragmentManager(),"Convert File Fragment");

                }
                else
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageScrolled(int page, float positionOffset) {
        seekBar.setProgress(page);
        textView.setText("Page "+page + "/" + seekBar.getMax());
    }

    @Override
    public void loadComplete(int nbPages) {
        seekBar.setMax(nbPages-1);
    }
}
