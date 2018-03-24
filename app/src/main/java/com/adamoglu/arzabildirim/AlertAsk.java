package com.adamoglu.arzabildirim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


class AlertAsk {
    final int comlete =0;
    final int delete=1;
    final int deleteUye=2;

    void ask(Context context, String title, String ask, String buttonPos, String buttonNeg, final int askID, final String ID, final boolean isAdmin)
    {
        AlertDialog alert =new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle(title)
                .setMessage(ask)

                .setPositiveButton(buttonPos, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        if(comlete==askID){//tamamlama işlemi ise
                            if(isAdmin)//admin ise
                                MyCardAdapterForAdmin.tamamla(ID,MyCardAdapterForAdmin.durum,MyCardAdapterForAdmin.meslek);
                            else//değilse
                                MyCardAdapterForPanel.tamamla(ID,MyCardAdapterForPanel.durum);

                        }else if(delete==askID){//silme işlemi ise
                                MyCardAdapterForAdmin.sil(ID,MyCardAdapterForAdmin.tablo,MyCardAdapterForAdmin.meslek);
                        }
                        else if(deleteUye==askID){//üye silme işlemi ise
                            MyCardAdapterForUyeler.sil(ID,MyCardAdapterForUyeler.tablo);
                        }


                        dialog.dismiss();
                    }

                })

                .setNegativeButton(buttonNeg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alert.show();

    }
}
