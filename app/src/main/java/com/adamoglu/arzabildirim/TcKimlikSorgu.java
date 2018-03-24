package com.adamoglu.arzabildirim;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class TcKimlikSorgu extends AppCompatActivity {

    private String tc,year,name,lastname,username,password;
    private String cevap=null;

    private MaterialEditText et_tc,et_year,et_name,et_lastname,et_username,et_password;
    private TextView bt_sorgula,bt_girisyap1,bt_geri1;
    private TcKimlikSorgu _this;

    public ProgressBar progressBar;

    private ConstraintLayout cl_kimlik,cl_giris;

    SharedPreferences sharedPreferences(){
        return getSharedPreferences("", MODE_PRIVATE);
    }
    SharedPreferences.Editor editor(){
        return getSharedPreferences("", MODE_PRIVATE).edit();
    }


    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc_kimlik_sorgu);

        _this=this;

        progressBar=new ProgressBar(TcKimlikSorgu.this);


        et_tc=findViewById(R.id.editText_tc);
        et_year=findViewById(R.id.editText_year);
        et_name=findViewById(R.id.editText_name);
        et_lastname=findViewById(R.id.editText_lastname);
        et_username=findViewById(R.id.editText_kullaniciadi);
        et_password=findViewById(R.id.editText_parola);
        bt_sorgula=findViewById(R.id.button_sorgula);
        bt_girisyap1=findViewById(R.id.button_girisyap1);
        bt_geri1=findViewById(R.id.button_geri1);
        cl_kimlik=findViewById(R.id.ConstraintLayout1);
        cl_giris=findViewById(R.id.ConstraintLayout2);

        //İnputlara max min değerleri veriliyor
        et_tc.setBaseColor(Color.parseColor("#FF00897B"));
        et_tc.setMaxCharacters(11);
        et_tc.setMinCharacters(11);

        et_year.setBaseColor(Color.parseColor("#FF00897B"));
        et_year.setMaxCharacters(4);
        et_year.setMinCharacters(4);

        et_name.setBaseColor(Color.parseColor("#FF00897B"));
        et_name.setMaxCharacters(30);
        et_name.setMinCharacters(2);

        et_lastname.setBaseColor(Color.parseColor("#FF00897B"));
        et_lastname.setMaxCharacters(30);
        et_lastname.setMinCharacters(2);

        et_tc.setText(sharedPreferences().getString("tc",""));
        et_year.setText(sharedPreferences().getString("year",""));
        et_name.setText(sharedPreferences().getString("name",""));
        et_lastname.setText(sharedPreferences().getString("lastname",""));

        cl_kimlik.setVisibility(View.VISIBLE);
        cl_giris.setVisibility(View.GONE);


        bt_geri1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Kimlik dogrulama sayfasını açıyor
                count=0;
                cl_kimlik.setVisibility(View.VISIBLE);
                cl_giris.setVisibility(View.GONE);
            }
        });

        bt_girisyap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Giris yapma ekranını acıyor
                if(count++%2==0){
                    cl_kimlik.setVisibility(View.GONE);
                    cl_giris.setVisibility(View.VISIBLE);
                }
            }
        });


        bt_sorgula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Tc sorguluyor

                tc=et_tc.getText().toString();
                year=et_year.getText().toString();
                name=et_name.getText().toString();
                lastname=et_lastname.getText().toString();

                //İnputlar min max kurallarına uyuyor ise
                if(et_tc.isCharactersCountValid() && et_name.isCharactersCountValid() && et_username.isCharactersCountValid() && et_year.isCharactersCountValid())
                    if(!tc.equals("") && !year.equals("") && !name.equals("") && !lastname.equals("")){//Eger inputlar dolu ise
                        progressBar.dialogShow("Kimlik Bilgileriniz Kontrol Ediliyor...");
                        new sorgula().execute();//Bilgiler Php ye gönderiliyor
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Boş Yerleri Doldurunuz", Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void WhenTrue(){
        //Tc kimlik bilgileri doğru ise
        editor().putString("tc",tc).commit();
        editor().putString("year",year).commit();
        editor().putString("name",name.toUpperCase()).commit();
        editor().putString("lastname",lastname.toUpperCase()).commit();

        progressBar.dialogClose();

        Intent i = new Intent(this, Form.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


    private class sorgula  extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{

                String link=Connect_PHP.TC_QUERY;
                //Phpye gönderilecek bilgiler değişkene alınıyor.
                String data  = URLEncoder.encode("tc", "UTF-8") + "=" +
                        URLEncoder.encode(tc, "UTF-8");
                data += "&" + URLEncoder.encode("year", "UTF-8") + "=" +
                        URLEncoder.encode(year, "UTF-8");
                data += "&" + URLEncoder.encode("name", "UTF-8") + "=" +
                        URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("lastname", "UTF-8") + "=" +
                        URLEncoder.encode(lastname, "UTF-8");
                //Linkdeki php açılıyor
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                //Bilgiler php ye gönderiliyor
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                //Php den gelen cevap okunuyor
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
            if(cevap.equals("Y")){//Eger gelen cevap yanlış ise
                progressBar.dialogClose();
                Toast.makeText(getApplicationContext(), "Lütfen Tc Kimlik Bilgilerinizi Kontrol Ediniz", Toast.LENGTH_LONG).show();
            }
            else//Doğru ise
            try {
                WhenTrue();
            }catch (Exception ex){
                Toast.makeText(_this, "Hata", Toast.LENGTH_SHORT).show();
            }finally {
                progressBar.dialogClose();
            }

        }
    }

    String sonuc="";
    int sonuc_id=-1;

    private class girisyap  extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{
                //Phpye gönderilecek bilgiler değişkene alınıyor.
                String link=Connect_PHP.ENTER;
                String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                        URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");
                //Linkdeki php açılıyor
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                //Bilgiler php ye gönderiliyor
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                //Php den gelen cevap okunuyor
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


            if(cevap.equals("H")){//hatalı şifre veya parola ise
                sonuc="Hatalı Parola";
                sonuc_id=2;

            }else if(cevap.split("-")[1].equals("admin")){//mesleği admin ise
                sonuc="Admin Girişi Yapılıyor";
                sonuc_id=0;
            }
            else{
                sonuc="Giriş Yapılıyor";
                sonuc_id=1;
            }

            progressBar.dialogShow(sonuc);
            Handler handler=new Handler();
            Runnable r=new Runnable() {
                public void run() {//2 saniye bekletiliyor...

                    Class activity = null;

                    if(sonuc_id==0){//admin ise
                        editor().putString("meslek",cevap).commit();
                        editor().putString("username",username).commit();
                        editor().putString("password",password).commit();

                        activity=AdminPanel.class;
                    }else if(sonuc_id==1){//admin değil ise
                        editor().putString("meslek",cevap).commit();
                        editor().putString("username",username).commit();
                        editor().putString("password",password).commit();

                        activity=Panel.class;
                    }

                    progressBar.dialogClose();
                    if(activity!=null){
                        //Gerekli activitye yönlendiriliyor.
                        Intent i = new Intent(TcKimlikSorgu.this,activity);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }

                }
            };
            handler.postDelayed(r, 2000);

        }
    }
}
