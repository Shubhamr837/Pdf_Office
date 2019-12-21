package com.shubhamr837.pdfoffice.adapters;

import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shubhamr837.pdfoffice.MainActivity;
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.activity.PdfReadActivity;

import java.io.File;
import java.util.Vector;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.MyViewHolder>  {
    private int FILE_NAME_LENGTH = 50;
    public static String pdf_intent;
    public static Vector<File> files;
    public String type ;

   public FilesListAdapter(String pdf_intent, String type){
       this.pdf_intent = pdf_intent;
       this.type=type;

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
        @Override
        public void onClick(View view) {
            Intent intent;
            String file_path =FilesListAdapter.files.get(getAdapterPosition()).getAbsolutePath();

            if(type.equals("pdf"))
            {intent= new Intent(view.getContext(),PdfReadActivity.class);
            intent.putExtra("file_path",file_path);
            intent.putExtra("intent",pdf_intent);
            view.getContext().startActivity(intent);}
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
       else if(file_name.endsWith(".doc")||file_name.endsWith(".DOC"))
            imageView.setImageResource(R.drawable.doc_icon);
       else if(file_name.endsWith(".txt"))
                imageView.setImageResource(R.drawable.txt_icon);

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}
