package com.shubhamr837.pdfoffice.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shubhamr837.pdfoffice.MainActivity;
import com.shubhamr837.pdfoffice.R;

public class GridAdapter extends BaseAdapter {
    private static Context mContext;


    public Integer[] mThumbIds ;
    public Integer[] mStrings ;

    public GridAdapter(Context context,Integer[] mThumbIds,Integer[] mStrings){
        this.mContext = context;
        this.mStrings=mStrings;
        this.mThumbIds=mThumbIds;
    }


    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item_view = new View(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            item_view = inflater.inflate(R.layout.gridelementview,null);
        }
        else {
            item_view = convertView;
        }
        ImageView imageView = (ImageView) item_view.findViewById(R.id.thumbnail);
        imageView.setImageResource(mThumbIds[position]);

        TextView textView = (TextView) item_view.findViewById(R.id.operation_name);
        textView.setText(mStrings[position]);
        return item_view;
    }

}
