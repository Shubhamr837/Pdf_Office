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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.activity.DownloadFileActivity;
import com.shubhamr837.pdfoffice.activity.PdfReadActivity;
import com.shubhamr837.pdfoffice.utils.CommonConstants;
import com.shubhamr837.pdfoffice.utils.FileAction;
import com.shubhamr837.pdfoffice.utils.FileType;

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

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.MyViewHolder> {
    private int FILE_NAME_LENGTH = 50;
    public static FileAction action;
    public static Vector<File> files;
    public FileType type;
    public FileType to;
    private static CustomDialogFragment customDialogFragment;
    private static FragmentManager fragmentManager;

    public FilesListAdapter(FileAction action, FileType type, FileType to, FragmentManager fragmentManager) {
        this.action = action;
        this.type = type;
        this.to = to;
        this.fragmentManager = fragmentManager;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;
        public FileType type;
        public FileType to;
        ;

        public MyViewHolder(View v, FileType type, FileType to) {
            super(v);
            v.setOnClickListener(this);
            view = v;
            this.to = to;
            this.type = type;
        }

        public class ConvertTask extends AsyncTask<File, Integer, Intent> {
            public JSONObject jsonObject;
            private Context context;
            public CustomDialogFragment customDialogFragment;
            private URL url;


            public ConvertTask(Context context, CustomDialogFragment customDialogFragment) {
                this.context = context;
                this.customDialogFragment = customDialogFragment;
            }


            @Override
            protected Intent doInBackground(File... files) {
                File pdf_file = files[0];
                Intent downloadActivityIntent;
                int bytesRead;

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    if (to == FileType.DOCX && type == FileType.PDF) {
                        url = new URL(CommonConstants.PDF_DOCX_CONVERSION_URL);
                    } else if (to == FileType.PDF && type == FileType.DOCX) {
                        url = new URL(CommonConstants.DOCX_TO_PDF_CONVERSION_URL);
                    } else if (to == FileType.IMAGE && type == FileType.PDF) {
                        url = new URL(CommonConstants.PDF_TO_IMG_CONVERSION_URL);
                    }
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-binary; utf-8");
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
                downloadActivityIntent.putExtra("type", type);
                if (jsonObject != null)
                    try {
                        downloadActivityIntent.putExtra("download_link", jsonObject.getString("download-link"));
                        downloadActivityIntent.putExtra("file_name", jsonObject.getString("file_name"));
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
            String file_path = FilesListAdapter.files.get(getAdapterPosition()).getAbsolutePath();

            if (type == FileType.PDF && action == FileAction.READ) {
                intent = new Intent(view.getContext(), PdfReadActivity.class);
                intent.putExtra("file_path", file_path);
                intent.putExtra("action", action);
                view.getContext().startActivity(intent);
            } else if (type == FileType.DOCX) {
                if (isNetworkConnected(view.getContext())) {
                    if(CommonConstants.SERVER_URL.equals(""))
                    {
                        Toast.makeText(view.getContext(), "Server is not configured", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    customDialogFragment = new CustomDialogFragment("Converting File", "Please wait...", true);
                    customDialogFragment.setCancelable(false);
                    customDialogFragment.show(fragmentManager, "Convert File Fragment");
                    ConvertTask convertTask = new ConvertTask(view.getContext(), customDialogFragment);
                    convertTask.execute(new File(file_path));

                } else {
                    Toast.makeText(view.getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            } else if (type == FileType.PDF && action == FileAction.CONVERT) {
                intent = new Intent(view.getContext(), PdfReadActivity.class);
                intent.putExtra("file_path", file_path);
                intent.putExtra("action", action);
                intent.putExtra("to", to);
                view.getContext().startActivity(intent);
            }
        }

    }


    @NonNull
    @Override
    public FilesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_files_list_element, parent, false);

        MyViewHolder vh = new MyViewHolder(v, type, to);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull FilesListAdapter.MyViewHolder holder, int position) {
        TextView textView = (TextView) holder.view.findViewById(R.id.file_name);
        String file_name = files.get(position).getName();
        if (file_name.length() > FILE_NAME_LENGTH) {
            textView.setText(String.format("%s...", file_name.substring(0, FILE_NAME_LENGTH)));
        } else {
            textView.setText(file_name);
        }
        ImageView imageView = (ImageView) holder.view.findViewById(R.id.file_icon);
        if (file_name.endsWith(".pdf"))
            imageView.setImageResource(R.drawable.pdf_icon);
        else if (file_name.endsWith(".docx") || file_name.endsWith(".DOCX"))
            imageView.setImageResource(R.drawable.doc_icon);
        else if (file_name.endsWith(".txt"))
            imageView.setImageResource(R.drawable.txt_icon);

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}
