package com.adamoglu.arzabildirim;



import android.app.ProgressDialog;
import android.content.Context;

class ProgressBar {
    private ProgressDialog dialog;

    ProgressBar(Context context){
        dialog=new ProgressDialog(context);
    }


    void dialogShow(String message){
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
    }

    void dialogClose(){
        dialog.cancel();
    }
}
