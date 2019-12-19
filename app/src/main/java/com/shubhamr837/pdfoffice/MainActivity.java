package com.shubhamr837.pdfoffice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shubhamr837.pdfoffice.adapters.ImageAdapter;
import com.shubhamr837.pdfoffice.ui.EmailPasswordActivity;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user ;
    private static final int AUTHENTICATION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
            authenticate();

    }
    public void authenticate(){
        Intent authentication_intent = new Intent(this,EmailPasswordActivity.class);
        startActivityForResult(authentication_intent,AUTHENTICATION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode){
            case AUTHENTICATION_REQUEST_CODE:
                if(resultCode==RESULT_OK) {
                    Toast.makeText(getApplicationContext(),"Sucessfully Signed in",Toast.LENGTH_SHORT).show();
                }
                else if(resultCode==RESULT_CANCELED){
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Authentication failure",Toast.LENGTH_SHORT).show();
                }
        }
    }
}
