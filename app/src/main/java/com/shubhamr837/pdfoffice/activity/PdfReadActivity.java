package com.shubhamr837.pdfoffice.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.NetworkUtils;
import com.shubhamr837.pdfoffice.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.shubhamr837.pdfoffice.utils.Utils.isNetworkConnected;

public class PdfReadActivity extends AppCompatActivity implements View.OnClickListener, OnPageScrollListener, OnLoadCompleteListener {

    private PDFView pdfView;
    private CustomDialogFragment customDialogFragment;
    public SeekBar seekBar;
    public TextView textView;
    public File pdf_file;
    public String pdf_intent;
    private PDFView.Configurator configurator;
    private boolean nightModeState=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdf_intent = getIntent().getExtras().getString("intent");
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
        pdf_file = new File(getIntent().getExtras().getString("file_path"));
        configurator=pdfView.fromFile(pdf_file);
        configurator.onLoad(this)
                .onPageScroll(this)
                .enableSwipe(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(2).nightMode(nightModeState)
                .swipeHorizontal(true).load();
       if(!pdf_intent.startsWith("Convert")) {
           
            seekBar = (SeekBar) findViewById(R.id.seekBar);
            textView = (TextView) findViewById(R.id.textView);
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
                    textView.setText("Page " + pval + "/" + seekBar.getMax());
                }
            });
            seekBar.setEnabled(true);

        }



    }
    public class DownloadTask extends AsyncTask<File,Integer, Intent>{
        public JSONObject jsonObject;
        private Context context;
        public CustomDialogFragment customDialogFragment;


        public DownloadTask(Context context,CustomDialogFragment customDialogFragment)
        {
         this.context=context;
         this.customDialogFragment=customDialogFragment;
        }


        @Override
        protected Intent doInBackground(File... files) {
            File pdf_file = files[0];
            Intent downloadActivityIntent;
            int bytesRead;

            ByteArrayOutputStream bos= new ByteArrayOutputStream();
            try {
                URL url = new URL("https://www.google.com/");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type","application/x-binary; utf-8");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();
                OutputStream out = httpURLConnection.getOutputStream();
                FileInputStream in = new FileInputStream(pdf_file);
                byte[] buffer = new byte[1024];
                while (true) {
                    bytesRead = in.read(buffer);
                    if (bytesRead == -1)
                        break;
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                in.close();
                InputStream inputStream;
                Thread.sleep(2000);
                if (httpURLConnection.getResponseCode() < 400) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                buffer = new byte[1024];
                while (-1 != (bytesRead = inputStream.read(buffer))) {
                    bos.write(buffer, 0, bytesRead);
                }
                jsonObject = new JSONObject(new String(bos.toByteArray()));


            } catch (Exception e) {
                e.printStackTrace();
            }
            customDialogFragment.dismiss();
            downloadActivityIntent = new Intent(context, DownloadFileActivity.class);
            downloadActivityIntent.putExtra("type","pdf");
            if(jsonObject!=null)
            try {
                downloadActivityIntent.putExtra("download_link",jsonObject.getString("download-link"));
                downloadActivityIntent.putExtra("file_name",jsonObject.getString("file_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return downloadActivityIntent;
        }
        protected void onPostExecute(Intent downloadActivityIntent) {
            startActivity(downloadActivityIntent);
            customDialogFragment.dismiss();

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.convert_button :
                if(isNetworkConnected(this))
                {
                customDialogFragment = new CustomDialogFragment("Converting File","Please wait...",true);
                customDialogFragment.setCancelable(false);
                customDialogFragment.show(getSupportFragmentManager(),"Convert File Fragment");
                DownloadTask downloadTask = new DownloadTask(this,customDialogFragment);
                downloadTask.execute(pdf_file);

                }
                else
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pdf_reader_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.night_mode_toggle:
                nightModeState=(!nightModeState);
                configurator.nightMode(nightModeState).defaultPage(pdfView.getCurrentPage()).load();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPageScrolled(int page, float positionOffset) {
       if(seekBar!=null) { seekBar.setProgress(page);
        textView.setText("Page "+page + "/" + seekBar.getMax());
    }
    }

    @Override
    public void loadComplete(int nbPages) {
        if(seekBar!=null)
        seekBar.setMax(nbPages-1);
    }


}
