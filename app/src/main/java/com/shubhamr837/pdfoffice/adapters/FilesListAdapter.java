package com.shubhamr837.pdfoffice.adapters;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shubhamr837.pdfoffice.R;

import java.io.File;
import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.MyViewHolder>{
   private int FILE_NAME_LENGTH = 50;
    public Vector<File> files;
   public String type ;
    public Vector<File> pdf_files = new Vector<>() ;
    public Vector<File> doc_files = new Vector<>();
    public Vector<File> txt_files = new Vector<>();
    boolean sorted=false;
   public FilesListAdapter(Vector<File> files,String type){
       System.out.println("inside the constructor");
       File f = Environment.getExternalStorageDirectory();
       Stack<File> stack = new Stack<File>();
       stack.push(f);
       while(!stack.isEmpty()) {
           f = stack.pop();
           File[] file = f.listFiles();
           for (File ff : file) {
               if (ff.isDirectory()) stack.push(ff);
               else if (ff.isFile() && ff.getPath().endsWith(".pdf")) {
                   pdf_files.add(ff);

               }
               else if (ff.isFile() && ff.getPath().endsWith(".doc")){
                   doc_files.add(ff);
               }
               else if (ff.isFile() && ff.getPath().endsWith(".txt")){
                   txt_files.add(ff);
               }
           }
       }
       //bubble sort files by date
       switch (type)
       {
           case "pdf":   bubbleSort(pdf_files);
               this.files=pdf_files;
                         break;
           case "doc":   bubbleSort(doc_files);
               this.files=doc_files;
                         break;
           case "txt":   bubbleSort(txt_files);
                         this.files=txt_files;
                         break;
       }


   }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public MyViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    @NonNull
    @Override
    public FilesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_files_list_element, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }



    @Override
    public void onBindViewHolder(@NonNull FilesListAdapter.MyViewHolder holder, int position) {
        TextView textView = (TextView) holder.view.findViewById(R.id.file_name);
        String file_name = files.get(position).getName();
        if (file_name.length() > FILE_NAME_LENGTH)
        {
            textView.setText(file_name.substring(0, FILE_NAME_LENGTH) + "...");
             }
        else
        {textView.setText(file_name);}
        ImageView imageView = (ImageView) holder.view.findViewById(R.id.file_icon);
        imageView.setImageResource(R.drawable.pdf_icon);


    }

    @Override
    public int getItemCount() {
        return files.size();
    }
    public void bubbleSort(Vector<File> files){
        while(!sorted)
        {   sorted=true;
            int j=0;
            while(j<files.size()-1){
                if(files.get(j).lastModified()<files.get(j+1).lastModified())
                {   sorted=false;
                    Collections.swap(files,j,j+1);
                }
                j++;
            }
        }
    }
}
