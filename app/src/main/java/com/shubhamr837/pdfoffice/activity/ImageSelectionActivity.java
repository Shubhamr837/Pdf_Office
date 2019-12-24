package com.shubhamr837.pdfoffice.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.adapters.GridAdapter;


public class ImageSelectionActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener{
    int PICK_IMAGE=1;
    public Integer[] mThumbIds = {
            R.drawable.camera_icon,R.drawable.gallery_icon
    };
    public Integer[] mStrings = {
            R.string.select_from_camera,R.string.select_from_gallery
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Image");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        GridView gridview = (GridView) findViewById(R.id.gridview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red));
        }

        gridview.setAdapter(new GridAdapter(this,mThumbIds,mStrings));
        gridview.setOnItemClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position){
            case 0:

            case 1:new

        }

    }
}
