package com.shubhamr837.pdfoffice.activity;


import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shubhamr837.pdfoffice.MainActivity;
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.adapters.FilesListAdapter;

import java.io.File;
import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

public class FilesSelection extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public Vector<File> files;
    private Bundle bundle;
    public String type;
    public String pdf_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        while (MainActivity.scan_files.isAlive());

        bundle = getIntent().getExtras();
        if(bundle.containsKey("intent"))
            pdf_intent = bundle.getString("intent");

        setContentView(R.layout.files_list_view);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        { actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(bundle.getString("tittle"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.red));
        }
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        type = bundle.getString("type");


        mAdapter = new FilesListAdapter(pdf_intent,type);
        recyclerView.setAdapter(mAdapter);


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


    }


