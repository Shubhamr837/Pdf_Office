package com.shubhamr837.pdfoffice;

import android.content.Context;
import android.content.Intent;

import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.activity.DownloadFileActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils implements Runnable {
    private File pdf_file;
    private CustomDialogFragment customDialogFragment;
    private Context context;
    public Intent downloadActivityIntent;
    public NetworkUtils (File pdf_file, CustomDialogFragment customDialogFragment, Context context){
        this.pdf_file=pdf_file;
        this.customDialogFragment=customDialogFragment;
        this.context=context;
    }
    @Override
    public void run() {
        int bytesRead;
        JSONObject jsonObject;
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
            String text = new String(bos.toByteArray());
            System.out.println(text);

        } catch (Exception e) {
            e.printStackTrace();
        }
        customDialogFragment.dismiss();
        downloadActivityIntent = new Intent(context, DownloadFileActivity.class);
        downloadActivityIntent.putExtra("type","pdf");
        context.startActivity(downloadActivityIntent);
    }
}
