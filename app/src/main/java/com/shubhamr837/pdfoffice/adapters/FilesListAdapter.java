package com.shubhamr837.pdfoffice.adapters;

import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.MainActivity;
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.activity.DownloadFileActivity;
import com.shubhamr837.pdfoffice.activity.PdfReadActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static com.shubhamr837.pdfoffice.utils.Utils.isNetworkConnected;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.MyViewHolder>  {
    private int FILE_NAME_LENGTH = 50;
    public static String pdf_intent;
    public static Vector<File> files;
    public String type ;
    private static CustomDialogFragment customDialogFragment;
    private static FragmentManager fragmentManager;

   public FilesListAdapter(String pdf_intent, String type,FragmentManager fragmentManager){
       this.pdf_intent = pdf_intent;
       this.type=type;
       this.fragmentManager=fragmentManager;
   }






    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public View view;
        public String type;
        public MyViewHolder(View v,String type) {
            super(v);
            v.setOnClickListener(this);
            view = v;
            this.type=type;
        }
        public class DownloadTask extends AsyncTask<File,Integer, Intent> {
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
                context.startActivity(downloadActivityIntent);
                customDialogFragment.dismiss();

            }
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            String file_path =FilesListAdapter.files.get(getAdapterPosition()).getAbsolutePath();

            if(type.equals("pdf"))
            {intent= new Intent(view.getContext(),PdfReadActivity.class);
            intent.putExtra("file_path",file_path);
            intent.putExtra("intent",pdf_intent);
            view.getContext().startActivity(intent);}
            else if (type.equals("docx")){
                if(isNetworkConnected(view.getContext()))
                {
                    customDialogFragment = new CustomDialogFragment("Converting File","Please wait...",true);
                    customDialogFragment.setCancelable(false);
                    customDialogFragment.show(fragmentManager,"Convert File Fragment");
                    DownloadTask downloadTask = new DownloadTask(view.getContext(),customDialogFragment);
                    downloadTask.execute(new File(file_path));

                }
                else
                    Toast.makeText(view.getContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
            }

        }


    @NonNull
    @Override
    public FilesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_files_list_element, parent, false);

        MyViewHolder vh = new MyViewHolder(v,type);
        return vh;
    }



    @Override
    public void onBindViewHolder(@NonNull FilesListAdapter.MyViewHolder holder, int position) {
        TextView textView = (TextView) holder.view.findViewById(R.id.file_name);
        String file_name = files.get(position).getName();
        String type = holder.type;
        if (file_name.length() > FILE_NAME_LENGTH)
        {
            textView.setText(file_name.substring(0, FILE_NAME_LENGTH) + "...");
             }
        else
        {textView.setText(file_name);}
        ImageView imageView = (ImageView) holder.view.findViewById(R.id.file_icon);
       if(file_name.endsWith(".pdf"))
                imageView.setImageResource(R.drawable.pdf_icon);
       else if(file_name.endsWith(".docx")||file_name.endsWith(".DOCX"))
            imageView.setImageResource(R.drawable.doc_icon);
       else if(file_name.endsWith(".txt"))
                imageView.setImageResource(R.drawable.txt_icon);

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}
