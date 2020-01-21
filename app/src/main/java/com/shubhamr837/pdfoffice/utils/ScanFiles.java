package com.shubhamr837.pdfoffice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shubhamr837.pdfoffice.activity.FilesSelection;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Stack;
import java.util.Vector;

import static com.shubhamr837.pdfoffice.MainActivity.doc_files;
import static com.shubhamr837.pdfoffice.MainActivity.doc_files_list;
import static com.shubhamr837.pdfoffice.MainActivity.pdf_files;
import static com.shubhamr837.pdfoffice.MainActivity.pdf_files_list;
import static com.shubhamr837.pdfoffice.MainActivity.txt_files;
import static com.shubhamr837.pdfoffice.MainActivity.txt_files_list;
import static com.shubhamr837.pdfoffice.utils.Utils.bubbleSort;

public class ScanFiles implements Runnable {
    private Context mContext;
    private String pdf_files_last_modified_key = "PDF_FILES_LAST_MODIFIED_KEY";
    private String txt_files_last_modified_key = "TXT_FILES_LAST_MODIFIED_KEY";
    private String doc_files_last_modified_key = "DOC_FILES_LAST_MODIFIED_KEY";
    public ScanFiles(Context context){
        this.mContext=context;
    }

    public void run(){
        File f = Environment.getExternalStorageDirectory();
        Stack<File> stack = new Stack<File>();
        stack.push(f);
        while(!stack.isEmpty()) {
            f = stack.pop();
            File[] file = f.listFiles();
            for (File ff : file) {
                if (ff.isDirectory()) stack.push(ff);
                else if (ff.isFile() && ff.getPath().endsWith(".pdf")) {
                    if(ff.lastModified()>getLastModified(pdf_files_last_modified_key))
                    pdf_files.add(ff);

                }
                else if (ff.isFile() &&( ff.getPath().endsWith(".docx")||ff.getPath().endsWith(".DOCX"))){
                    if(ff.lastModified()>getLastModified(doc_files_last_modified_key))
                    doc_files.add(ff);
                }
                else if (ff.isFile() && ff.getPath().endsWith(".txt")){
                    if(ff.lastModified()>getLastModified(txt_files_last_modified_key))
                    txt_files.add(ff);
                }
            }
        }
        bubbleSort(pdf_files);bubbleSort(doc_files);bubbleSort(txt_files);
        storeLastModified(pdf_files_last_modified_key,pdf_files.get(0).lastModified());
        storeLastModified(doc_files_last_modified_key,doc_files.get(0).lastModified());
        storeLastModified(txt_files_last_modified_key,txt_files.get(0).lastModified());
        storeObject(pdf_files_list,pdf_files);
        storeObject(doc_files_list,doc_files);
        storeObject(txt_files_list,txt_files);
        if(FilesSelection.customDialogFragment.isVisible())
            FilesSelection.customDialogFragment.dismiss();
    }
    public void storeObject(String key, Vector<?> list){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }
    public void storeLastModified(String key, long lastModified){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lastModified);
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }
    public long getLastModified(String key)
    {
        long last_modified;
        String json;
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        if(appSharedPrefs.contains(key))
        {json = appSharedPrefs.getString(key, "");
        last_modified = gson.fromJson(json,long.class);
        return last_modified;}
        return 0;
    }

}
