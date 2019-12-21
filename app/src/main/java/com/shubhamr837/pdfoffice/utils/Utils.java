package com.shubhamr837.pdfoffice.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;
import java.util.Collections;
import java.util.Vector;

public class Utils {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public static void bubbleSort(Vector<File> files){
        boolean sorted=false;
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
