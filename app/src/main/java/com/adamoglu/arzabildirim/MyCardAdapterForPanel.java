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

public class MyCardAdapterForPanel extends RecyclerView.Adapter<MyCardAdapterForPanel.ViewHolder> {
    private static String changeID;
    static String durum;
    static String meslek;

    private static ProgressBar progressBar;

    private ArrayList<String[]> mDataset;


    @SuppressLint("StaticFieldLeak")
    static Panel panel;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private AlertAsk alertAsk;
        private ConstraintLayout card_layout;

        private TextView card_name;
        private TextView card_tel;
        private TextView card_email;
        private TextView card_location;
        private TextView card_complaint;
        private TextView card_okay;
        private TextView card_id;

        ViewHolder(View view) {
            super(view);
            context=view.getContext();
            alertAsk=new AlertAsk();

            card_name = view.findViewById(R.id.card_name);
            card_tel = view.findViewById(R.id.card_tel);
            card_email = view.findViewById(R.id.card_email);
            card_location = view.findViewById(R.id.card_location);
            card_complaint = view.findViewById(R.id.card_complaint);
            card_id = view.findViewById(R.id.card_id);
            card_okay = view.findViewById(R.id.card_okay);

            card_layout=view.findViewById(R.id.card_layouth);

            card_okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    durum=panel.kullaniciAdi;
                    if(!card_okay.getText().toString().equals("TAMAMLANDI")){
                        alertAsk.ask(context,card_name.getText().toString(),"Bu Arıza Giderildi Mi?","Evet","Hayır", alertAsk.comlete,card_id.getText().toString(),false);
                    }

                }
            });

        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    MyCardAdapterForPanel(Panel panel, ArrayList<String[]> myDataset) {
        progressBar=new ProgressBar(panel);
        mDataset = myDataset;
        MyCardAdapterForPanel.panel =panel;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyCardAdapterForPanel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            holder.card_id.setText(mDataset.get(position)[0]);
            holder.card_name.setText(mDataset.get(position)[1]);
            holder.card_tel.setText(mDataset.get(position)[3]);
            holder.card_email.setText(mDataset.get(position)[2]);
            holder.card_location.setText(mDataset.get(position)[5]);
            holder.card_complaint.setText(mDataset.get(position)[4]);

            if(!mDataset.get(position)[6].equals("hayir")){
                holder.card_okay.setText("TAMAMLANDI");
                holder.card_okay.setTextColor(Color.parseColor("#606060"));
                holder.card_layout.setBackgroundColor(Color.parseColor("#d0d0d0"));
            }else{
                holder.card_okay.setText("TAMAMLA");
                holder.card_okay.setTextColor(Color.parseColor("#2196F3"));
                holder.card_layout.setBackgroundColor(Color.parseColor("#ffffff"));
            }

        }catch (Exception ex){
            holder.card_id.setText("");
            holder.card_name.setText("Hata Oluştu");
            holder.card_tel.setText("");
            holder.card_email.setText("");
            holder.card_location.setText("");
            holder.card_complaint.setText("");

            holder.card_okay.setVisibility(View.GONE);
            holder.card_layout.setVisibility(View.GONE);
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }




    static void tamamla(String changeID, String durum){
        MyCardAdapterForPanel.changeID=changeID;
        MyCardAdapterForPanel.durum=durum;

        progressBar.dialogShow("Tamamlanıyor...");
        new tamamla().execute();
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
                        URLEncoder.encode(panel.meslek, "UTF-8");



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