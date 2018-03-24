package com.adamoglu.arzabildirim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MyCardAdapterForAdmin extends RecyclerView.Adapter<MyCardAdapterForAdmin.ViewHolder> {
    private static String changeID;
    private static String deleteID;
    static String meslek;
    static String tablo;
    static String durum;

    private static ProgressBar progressBar;

    private ArrayList<String[]> mDataset;


    @SuppressLint("StaticFieldLeak")
    static AdminPanel panel;



    static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private AlertAsk alertAsk;
        private ConstraintLayout card_layout;


        private TextView card_name;
        private TextView card_tcNo;
        private TextView card_tel;
        private TextView card_email;
        private TextView card_location;
        private TextView card_complaint;
        private TextView card_who;
        private TextView card_jop;
        private TextView card_okay;
        private TextView card_delete;
        private TextView card_id;

        ViewHolder(View view) {
            super(view);
            context=view.getContext();
            alertAsk=new AlertAsk();

            card_name = view.findViewById(R.id.card_name);
            card_tcNo = view.findViewById(R.id.card_tcNo);
            card_tel = view.findViewById(R.id.card_tel);
            card_email = view.findViewById(R.id.card_email);
            card_location = view.findViewById(R.id.card_location);
            card_complaint = view.findViewById(R.id.card_complaint);
            card_who = view.findViewById(R.id.card_who);
            card_id = view.findViewById(R.id.card_id);
            card_jop = view.findViewById(R.id.card_jop);
            card_okay = view.findViewById(R.id.card_okay);
            card_delete = view.findViewById(R.id.card_delete);
            card_layout=view.findViewById(R.id.card_layouth);

            card_okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//Arıza tamamlama

                    if(!card_okay.getText().toString().equals("GERİ AL")){//Geri alma değil ise
                        //bilgiler eklenip soru soruldu
                        durum=panel.kullaniciAdi;
                        meslek=card_jop.getText().toString();
                        alertAsk.ask(context,card_name.getText().toString(),"Bu Arıza Giderildi Mi?","Evet","Hayır", alertAsk.comlete,card_id.getText().toString(),true);
                    }else {//geri alma ise
                        //Bilgiler eklenip soru soruldu
                        meslek=card_jop.getText().toString();
                        durum="hayir";
                        alertAsk.ask(context,card_name.getText().toString(),"Sorunu Geri Al","Al","Hayır", alertAsk.comlete,card_id.getText().toString(),true);
                    }

                }
            });

            card_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//Arıza silme
                    //Bilgiler eklenip soru soruldu
                    meslek=card_jop.getText().toString();
                    tablo="Arizalar";
                    alertAsk.ask(context,card_name.getText().toString(),"Bu Arıza Silinsin Mi?","Evet","Hayır", alertAsk.delete,card_id.getText().toString(),true);

                }
            });
        }
    }

    MyCardAdapterForAdmin(AdminPanel panel, ArrayList<String[]> myDataset) {
        progressBar=new ProgressBar(panel);
        mDataset = myDataset;
        MyCardAdapterForAdmin.panel =panel;
    }

    @Override
    public MyCardAdapterForAdmin.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admincard, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            holder.card_id.setText(mDataset.get(position)[0]);
            holder.card_tcNo.setText(mDataset.get(position)[1]);
            holder.card_name.setText(mDataset.get(position)[2]);
            holder.card_tel.setText(mDataset.get(position)[4]);
            holder.card_email.setText(mDataset.get(position)[3]);
            holder.card_location.setText(mDataset.get(position)[6]);
            holder.card_complaint.setText(mDataset.get(position)[5]);
            holder.card_jop.setText(mDataset.get(position)[7]);

            if(!mDataset.get(position)[8].equals("hayir")){
                holder.card_who.setText(mDataset.get(position)[8]+" tamamladı");
                holder.card_okay.setText("GERİ AL");
                holder.card_layout.setBackgroundColor(Color.parseColor("#d0d0d0"));
            }else{
                holder.card_who.setText("");
                holder.card_okay.setText("TAMAMLA");
                holder.card_layout.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }catch (Exception ex){

            holder.card_id.setText("");
            holder.card_tcNo.setText("");
            holder.card_name.setText("Hata Oluştu");
            holder.card_tel.setText("");
            holder.card_email.setText("");
            holder.card_location.setText("");
            holder.card_complaint.setText("");
            holder.card_jop.setText("");

            holder.card_okay.setVisibility(View.GONE);
            holder.card_layout.setVisibility(View.GONE);

        }



    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }




    static void tamamla(String changeID, String durum,String meslek){
        MyCardAdapterForAdmin.changeID=changeID;
        MyCardAdapterForAdmin.meslek=meslek;
        MyCardAdapterForAdmin.durum=durum;

        progressBar.dialogShow("Tamamlanıyor...");
        new tamamla().execute();
    }


    static void sil(String deleteID,String tablo,String meslek){
        MyCardAdapterForAdmin.deleteID=deleteID;
        MyCardAdapterForAdmin.tablo=tablo;
        MyCardAdapterForAdmin.durum=durum;

        progressBar.dialogShow("Siliniyor...");
        new sil().execute();
    }

    static private String cevap="";


    private static class tamamla  extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{

                String link=Connect_PHP.COMPLETE;
                String data  = URLEncoder.encode("changeID", "UTF-8") + "=" +
                        URLEncoder.encode(changeID, "UTF-8");
                data += "&" + URLEncoder.encode("durum", "UTF-8") + "=" +
                        URLEncoder.encode(durum, "UTF-8");
                data += "&" + URLEncoder.encode("meslek", "UTF-8") + "=" +
                        URLEncoder.encode(meslek, "UTF-8");


                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));


                cevap=reader.readLine();

                return cevap;
            } catch(Exception e){
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);


            if(cevap.equals("E")){
                panel.get();
            }else
                Toast.makeText(panel, "Bir Sorun Oluştu", Toast.LENGTH_LONG).show();

            progressBar.dialogClose();

        }
    }

    private static class sil  extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{

                String link=Connect_PHP.DATA_DELETE;
                String data  = URLEncoder.encode("deleteID", "UTF-8") + "=" +
                        URLEncoder.encode(deleteID, "UTF-8");
                data += "&" + URLEncoder.encode("tablo", "UTF-8") + "=" +
                        URLEncoder.encode(tablo, "UTF-8");
                data += "&" + URLEncoder.encode("meslek", "UTF-8") + "=" +
                        URLEncoder.encode(meslek, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));


                cevap=reader.readLine();

                return cevap;
            } catch(Exception e){
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);


            if(cevap.equals("E")){
                panel.get();
            }else
                Toast.makeText(panel, "Bir Sorun Oluştu", Toast.LENGTH_LONG).show();

            progressBar.dialogClose();

        }
    }

}