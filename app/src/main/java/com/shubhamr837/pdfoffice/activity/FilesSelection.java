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

import com.shubhamr837.pdfoffice.Fragments.CustomDialogFragment;
import com.shubhamr837.pdfoffice.MainActivity;
import com.shubhamr837.pdfoffice.R;
import com.shubhamr837.pdfoffice.adapters.FilesListAdapter;
import com.shubhamr837.pdfoffice.utils.FileAction;
import com.shubhamr837.pdfoffice.utils.FileType;

import java.io.File;

import java.util.Vector;

public class FilesSelection extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public Vector<File> files;
    private Bundle bundle;
    public FileType type;
    public FileType to;
    public FileAction action;
    public static CustomDialogFragment customDialogFragment = new CustomDialogFragment("Please wait", "Scanning files...", false);
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        action = (FileAction) bundle.get("action");

        setContentView(R.layout.files_list_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(bundle.getString("title"));
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

        type = (FileType) bundle.get("type");
        to = (FileType) bundle.get("to");


        mAdapter = new FilesListAdapter(action, type, to, getSupportFragmentManager());
        recyclerView.setAdapter(mAdapter);
        if ((MainActivity.getInstance().scan_files.isAlive())) {
            customDialogFragment.setCancelable(false);
            customDialogFragment.show(getSupportFragmentManager(), "scan_files");
        }


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


