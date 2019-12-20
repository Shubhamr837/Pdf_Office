package com.shubhamr837.pdfoffice.activity;


import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.adapters.FilesListAdapter;

import java.io.File;
import java.util.Vector;

public class PdfFiles extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public Vector<File> files;
    private Bundle bundle;
    public String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_list_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Open a file");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        bundle = getIntent().getExtras();
        files = (Vector<File>) getIntent().getParcelableExtra("files");
        type = bundle.getString("type");

        mAdapter = new FilesListAdapter(files,type);
        recyclerView.setAdapter(mAdapter);

    }

    }


