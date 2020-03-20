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
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.utils.CommonConstants;
import com.shubhamr837.pdfoffice.utils.FirebaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
    private DownloadTask downloadTask;
    private String to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdf_intent = getIntent().getExtras().getString("intent");
        if(pdf_intent!=null&&pdf_intent.startsWith("Convert"))
        {setContentView(R.layout.activity_pdf_read_and_convert);
            ((Button)findViewById(R.id.convert_button)).setOnClickListener(this);
        }
        else
            setContentView(R.layout.activity_pdf_read);
        to=getIntent().getExtras().getString("convert_to");
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
       if(pdf_intent!=null&&!pdf_intent.startsWith("Convert")) {
           
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
    private class DownloadTask extends AsyncTask<File,Integer, Intent>{
        public JSONObject jsonObject;
        private Context context;
        public CustomDialogFragment customDialogFragment;
        public String convert_to ;


        public DownloadTask(Context context,CustomDialogFragment customDialogFragment , String convert_to)
        {
         this.context=context;
         this.convert_to=convert_to;
         this.customDialogFragment=customDialogFragment;

        }


        @Override
        protected Intent doInBackground(File... files) {
            File pdf_file = files[0];
            Intent downloadActivityIntent;
            int bytesRead;
            URL url ;
            HttpURLConnection httpURLConnection = null ;

            ByteArrayOutputStream bos= new ByteArrayOutputStream();
            try {

                if(convert_to.equals("docx")) {
                    url = new URL(CommonConstants.PDF_DOCX_CONVERSION_URL);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                }
                else if(convert_to.equals("txt"))
                {
                    url = new URL(CommonConstants.TXT_CONVERSION_URL);
                    httpURLConnection = (HttpURLConnection)url.openConnection();

                }
                else if(convert_to.equals("img")){
                    url= new URL(CommonConstants.PDF_TO_IMG_CONVERSION_URL);
                }
                else {
                    return null;
                }
                if(httpURLConnection!=null) {
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-binary; utf-8");
                    httpURLConnection.setRequestProperty("auth-token", FirebaseUtils.getToken());
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

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            customDialogFragment.dismiss();
            downloadActivityIntent = new Intent(context, DownloadFileActivity.class);
            if(jsonObject!=null)
            try {
                downloadActivityIntent.putExtra("download_link",jsonObject.getString("download_link"));
                downloadActivityIntent.putExtra("file_name",pdf_file.getName());
                downloadActivityIntent.putExtra("type", convert_to);
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
                if(to.equals("txt"))
                {
                    downloadTask = new DownloadTask(this,customDialogFragment,to);
                }
                else if(to.equals("docx"))
                {
                    downloadTask = new DownloadTask(this,customDialogFragment,"docx");
                }
                else if(to.equals("img")){
                    downloadTask = new DownloadTask(this,customDialogFragment,to);
                }
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
