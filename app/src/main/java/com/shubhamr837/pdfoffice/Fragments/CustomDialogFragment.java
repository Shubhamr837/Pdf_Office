package com.shubhamr837.pdfoffice.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.shubhamr837.pdfoffice.R;

import org.w3c.dom.Text;

public class CustomDialogFragment extends androidx.fragment.app.DialogFragment {

    public String dialog_text;
    public String dialog_tittle;
    public boolean dialog_button;

    public CustomDialogFragment(String dialog_tittle,String dialog_text,boolean dialog_button){
        super();
        this.dialog_button=dialog_button;
        this.dialog_tittle=dialog_tittle;
        this.dialog_text=dialog_text;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
    View view = inflater.inflate(R.layout.dialog_rounded,container,false);
    if (getDialog() != null && getDialog().getWindow() != null) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }
        ((TextView)view.findViewById(R.id.dialog_tittle)).setText(dialog_tittle);
        ((TextView)view.findViewById(R.id.dialog_text)).setText(dialog_text);
        if(dialog_button)
            ((TextView)view.findViewById(R.id.download_button)).setVisibility(View.VISIBLE);
        else
            ((TextView)view.findViewById(R.id.download_button)).setVisibility(View.INVISIBLE);
    return view;
}
}
