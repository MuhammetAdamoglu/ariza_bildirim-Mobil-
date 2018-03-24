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

public class MyCardAdapterForUyeler extends RecyclerView.Adapter<MyCardAdapterForUyeler.ViewHolder> {
    private static String deleteID;
    static String tablo;

    private static ProgressBar progressBar;

    private ArrayList<String[]> mDataset;


    @SuppressLint("StaticFieldLeak")
    static AdminPanel panel;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private AlertAsk alertAsk;
        private ConstraintLayout card_layout;


        private TextView card_name;
        private TextView card_username;
        private TextView card_tel;
        private TextView card_email;
        private TextView card_jop;
        private TextView card_delete;
        private TextView card_id;

        ViewHolder(View view) {
            super(view);
            context=view.getContext();
            alertAsk=new AlertAsk();

            card_name = view.findViewById(R.id.card_name);
            card_username = view.findViewById(R.id.card_username);
            card_tel = view.findViewById(R.id.card_tel);
            card_email = view.findViewById(R.id.card_email);
            card_id = view.findViewById(R.id.card_id);
            card_jop = view.findViewById(R.id.card_jop);
            card_delete = view.findViewById(R.id.card_delete);
            card_layout=view.findViewById(R.id.card_layouth);


            card_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tablo="Uyeler";
                    alertAsk.ask(context,card_name.getText().toString(),"Bu Üye Silinsin Mi?","Evet","Hayır", alertAsk.deleteUye,card_id.getText().toString(),true);

                }
            });

        }


    }

    MyCardAdapterForUyeler(AdminPanel panel, ArrayList<String[]> myDataset) {
        progressBar=new ProgressBar(panel);
        mDataset = myDataset;
        MyCardAdapterForUyeler.panel =panel;
    }

    @Override
    public MyCardAdapterForUyeler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.carduyeler, parent, false);

        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.card_id.setText(mDataset.get(position)[0]);
        holder.card_username.setText(mDataset.get(position)[1]);
        holder.card_name.setText(mDataset.get(position)[2]+" "+mDataset.get(position)[3]);
        holder.card_tel.setText(mDataset.get(position)[5]);
        holder.card_email.setText(mDataset.get(position)[4]);
        holder.card_jop.setText(mDataset.get(position)[6]);


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



    static void sil(String deleteID,String tablo){
        MyCardAdapterForUyeler.deleteID=deleteID;
        MyCardAdapterForUyeler.tablo=tablo;

        progressBar.dialogShow("Siliniyor...");
        new sil().execute();
    }

    static private String cevap="";


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
                panel.getUye();
            }else
                Toast.makeText(panel, "Bir Sorun Oluştu", Toast.LENGTH_LONG).show();

            progressBar.dialogClose();

        }
    }

}