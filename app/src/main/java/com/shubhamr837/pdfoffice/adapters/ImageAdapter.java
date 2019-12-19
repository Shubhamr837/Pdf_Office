package com.shubhamr837.pdfoffice.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shubhamr837.pdfoffice.R;

public class ImageAdapter extends BaseAdapter {
private Context mContext;
    public Integer[] mThumbIds = {
            R.drawable.pdf_to_word, R.drawable.word_to_pdf,
            R.drawable.pdf_to_text,R.drawable.text_to_pdf
    };
    public Integer[] mStrings = {
            R.string.pdf_to_word,R.string.word_to_pdf,
            R.string.pdf_to_text,R.string.text_to_pdf
    };

    public ImageAdapter(Context context){
    this.mContext = context;
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
            ImageView imageView = item_view.findViewById(R.id.thumbnail);
            imageView.setImageResource(mThumbIds[position]);

            TextView textView = item_view.findViewById(R.id.operation_name);
            textView.setText(mStrings[position]);
        }
        else {
            item_view = convertView;
        }
        return item_view;
    }
}
