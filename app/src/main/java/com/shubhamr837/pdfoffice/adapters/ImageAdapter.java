package com.shubhamr837.pdfoffice.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.shubhamr837.pdfoffice.R;

public class ImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    public ImageAdapter() {
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return count;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
            holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
            holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.chkImage.setId(position);
        holder.imgThumb.setId(position);
        holder.chkImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int id = cb.getId();
                if (thumbnailsselection[id]) {
                    cb.setChecked(false);
                    thumbnailsselection[id] = false;
                } else {
                    cb.setChecked(true);
                    thumbnailsselection[id] = true;
                }
            }
        });
        holder.imgThumb.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int id = holder.chkImage.getId();
                if (thumbnailsselection[id]) {
                    holder.chkImage.setChecked(false);
                    thumbnailsselection[id] = false;
                } else {
                    holder.chkImage.setChecked(true);
                    thumbnailsselection[id] = true;
                }
            }
        });
        try {
            setBitmap(holder.imgThumb, ids[position]);
        } catch (Throwable e) {
        }
        holder.chkImage.setChecked(thumbnailsselection[position]);
        holder.id = position;
        return convertView;
    }
}


/**
 * Inner class
 * @author tasol
 */
class ViewHolder {
    ImageView imgThumb;
    CheckBox chkImage;
    int id;
}

}