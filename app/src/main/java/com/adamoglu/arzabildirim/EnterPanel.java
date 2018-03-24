package com.adamoglu.arzabildirim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Abra on 19.03.2018.
 */

public class EnterPanel {

    SharedPreferences.Editor editor(){
        return context.getSharedPreferences("", MODE_PRIVATE).edit();
    }
    private String username,password,cevap;
    private Context context;
    private ProgressBar progressBar;

    EnterPanel(Context context,String username,String password, ProgressBar progressBar){

        this.username=username;
        this.password=password;
        this.progressBar=progressBar;
        this.context=context;

        new enter().execute();

    }



    String sonuc = "";
    int sonuc_id=-1;
    private class enter extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{

                String link=Connect_PHP.ENTER;
                String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                        URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));


                cevap=reader.readLine();

                return "";
            } catch(Exception e){
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if(cevap.equals("H")){
                editor().remove("username").commit();
                editor().remove("password").commit();

                Intent i = new Intent(context,TcKimlikSorgu.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);

            }else if(cevap.split("-")[1].equals("admin")){
                sonuc="Admin Girişi Yapılıyor";
                sonuc_id=0;
            }
            else{
                sonuc="Giriş Yapılıyor";
                sonuc_id=1;
            }

            if(sonuc_id==-1)
                return;

            progressBar.dialogShow(sonuc);
            Handler handler=new Handler();
            Runnable r=new Runnable() {
                public void run() {

                    Class activity = null;

                    if(sonuc_id==0){
                        editor().putString("meslek",cevap).commit();
                        editor().putString("username",username).commit();
                        editor().putString("password",password).commit();

                        activity=AdminPanel.class;
                    }else if(sonuc_id==1){
                        editor().putString("meslek",cevap).commit();
                        editor().putString("username",username).commit();
                        editor().putString("password",password).commit();

                        activity=Panel.class;
                    }

                    progressBar.dialogClose();
                    if(activity!=null){

                        Intent i = new Intent(context,activity);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(i);
                    }

                }
            };
            handler.postDelayed(r, 2000);


        }
    }
}
