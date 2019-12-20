package com.shubhamr837.pdfoffice.adapters;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Vector;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.MyViewHolder>{
   public Vector<File> files;
   public String type ;
   public FilesListAdapter(Vector<File> files,String type){
       this.files=files;
       this.type=type;
   }
    @NonNull
    @Override
    public FilesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FilesListAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
