package com.adamoglu.arzabildirim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Informations extends AppCompatActivity {

    private String cevap,username,password;
    private Context context;

    private ProgressBar progressBar;

    SharedPreferences sharedPreferences(){
        return getSharedPreferences("", MODE_PRIVATE);
    }
    SharedPreferences.Editor editor(){
        return getSharedPreferences("", MODE_PRIVATE).edit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        context=getApplicationContext();

        progressBar=new ProgressBar(Informations.this);

        progressBar.dialogShow("Bilgiler Alınıyor...");
        new informations().execute();
    }


    static private ArrayList<String> arizalar = new ArrayList<>();
    static private ArrayList<String> fakulte = new ArrayList<>();
    static private ArrayList<String> sinif = new ArrayList<>();

    static ArrayList getArizalar(){
        return arizalar;
    }
    static ArrayList getFakulte(){
        return fakulte;
    }
    static ArrayList getSinif(){
        return sinif;
    }

    private class informations  extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{

                //Link veriliyor
                String link=Connect_PHP.INFORMATIONS;
                //Gönderilecek veri ve id'si belileniyor
                String data  = URLEncoder.encode("post", "UTF-8") + "=" +
                        URLEncoder.encode("control", "UTF-8");
                //Link açılıyor
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                //Veriler php'ye aktarılıyor
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();
                //Php den gelen cevap okunuyor
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


            try {
                //Gelen cevap parçalanıyor
                String[] separated = cevap.split("-");
                String[] separated_arizalar = separated[0].split("/");
                String[] separated_fakulte = separated[1].split("/");
                String[] separated_sinif = separated[2].split("/");

                //Cevap dizilere kaydediliyor.
                for(int i=0; i<separated_arizalar.length; i++){
                    arizalar.add(separated_arizalar[i]);
                }
                for(int i=0; i<separated_fakulte.length; i++){
                    fakulte.add(separated_fakulte[i]);
                }
                for(int i=0; i<separated_sinif.length; i++){
                    sinif.add(separated_sinif[i]);
                }
                progressBar.dialogClose();

                //Hafızadaki kullanıcı adı ve sifre var ise değişkenlere alınıyor
                username=sharedPreferences().getString("username","");
                password=sharedPreferences().getString("password","");

                if(!username.equals("")){//Eger boş değil ise
                    //Giriş yapılıyor
                    new EnterPanel(getApplicationContext(),username,password,progressBar);
                }else{//Boş ise
                    //Anasayfaya yönlendiriliyor
                    Intent i = new Intent(Informations.this,TcKimlikSorgu.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }

            }catch (Exception ex){
                Toast.makeText(context, "Hata", Toast.LENGTH_SHORT).show();
            }finally {
                progressBar.dialogClose();
            }

        }
    }





}
