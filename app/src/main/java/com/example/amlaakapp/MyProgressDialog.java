package com.example.amlaakapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

public class MyProgressDialog {
    private ProgressDialog progressDialog;

    public MyProgressDialog(Context context) {
        progressDialog = new ProgressDialog (context, R.style.progress_dialoge);
        final ProgressBar progressBar = new ProgressBar (context, null, android.R.attr.progressBarStyle);
        progressDialog.setIndeterminateDrawable (progressBar.getIndeterminateDrawable ());
    }

    public void showDialog () {
        if(progressDialog!=null && !progressDialog.isShowing()) {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }

    public void dismissDialog () {
        if(progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void cancelAble(){
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

}
