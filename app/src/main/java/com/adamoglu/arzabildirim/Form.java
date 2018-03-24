package com.adamoglu.arzabildirim;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class Form extends AppCompatActivity {

    private MaterialBetterSpinner spinner_ariza,spinner_sinif,spinner_fakulte;
    private CardView cardViewForm;
    private MaterialEditText et_email,et_telefon,et_sikayet;
    private TextView bt_gonder;

    private String ariza="",email,fakulte="",sinif="",telefon,sikayet;
    private String tc,year,name,lastname;

    private String cevap="";

    private ProgressBar progressBar;

    private boolean isAnime=false;

    ArrayList<String> a = new ArrayList<>();
    ArrayList<String> b = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();

    SharedPreferences sharedPreferences(){
        return getSharedPreferences("", MODE_PRIVATE);
    }
    SharedPreferences.Editor editor(){
        return getSharedPreferences("", MODE_PRIVATE).edit();
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);


        a=Informations.getArizalar();
        b=Informations.getFakulte();
        c=Informations.getSinif();

        tc=sharedPreferences().getString("tc","");
        year=sharedPreferences().getString("year","");
        name=sharedPreferences().getString("name","");
        lastname=sharedPreferences().getString("lastname","");

        spinner_ariza = findViewById(R.id.spinner_ariza);
        spinner_fakulte = findViewById(R.id.spinner_fakulte);
        spinner_sinif = findViewById(R.id.spinner_sinif);
        cardViewForm=findViewById(R.id.cardViewForm);
        et_email=findViewById(R.id.editText_email);
        et_telefon=findViewById(R.id.editText_telefon);
        et_sikayet=findViewById(R.id.editText_sikayet);
        bt_gonder=findViewById(R.id.button_gonder);

        et_email.setBaseColor(Color.parseColor("#FF00897B"));
        et_email.setMaxCharacters(40);
        et_email.setMinCharacters(2);

        et_sikayet.setBaseColor(Color.parseColor("#FF00897B"));
        et_sikayet.setMaxCharacters(100);
        et_sikayet.setMinCharacters(5);

        ArrayAdapter adapter_ariza=createAdapter(a);
        ArrayAdapter adapter_fakulte=createAdapter(b);
        ArrayAdapter adapter_sinif=createAdapter(c);

        spinner_ariza.setAdapter(adapter_ariza);
        spinner_fakulte.setAdapter(adapter_fakulte);
        spinner_sinif.setAdapter(adapter_sinif);

        CreateListener(spinner_ariza);

        et_telefon.setBaseColor(Color.parseColor("#FF00897B"));
        et_telefon.setMaxCharacters(10);
        et_telefon.setMinCharacters(10);

        progressBar=new ProgressBar(Form.this);

        bt_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Form gönderme

                email=et_email.getText().toString();
                sikayet=et_sikayet.getText().toString();
                telefon=et_telefon.getText().toString();
                ariza=spinner_ariza.getText().toString();
                fakulte=spinner_fakulte.getText().toString();
                sinif=spinner_sinif.getText().toString();

                //inputlar max ve min kurallarınıa uyuyor ise
                if(et_email.isCharactersCountValid() && et_sikayet.isCharactersCountValid() && et_telefon.isCharactersCountValid())
                    //inputlar boş değil ise
                    if(!email.equals("") && !sikayet.equals("") && !telefon.equals("") && !ariza.equals("") && !fakulte.equals("") && !sinif.equals(""))
                        //E-mail geçerli ise
                        if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                            //Telefon geçerli ise
                            if(telefon.toCharArray()[0]=='5'){
                                //Form gönderiliyor
                                progressBar.dialogShow("Form Gönderiliyor...");
                                new gonder().execute();
                            }
                            else
                                Toast.makeText(Form.this, "Geçersiz Telefon", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Form.this, "Geçersiz E-Posta", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Boş Yerleri Doldurunuz", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private ArrayAdapter createAdapter(ArrayList array){


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, array);

        return adapter;
    }

    private void CreateListener(final MaterialBetterSpinner spinner){

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!isAnime){
                    Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.anime_down);
                    cardViewForm.startAnimation(slide_down);
                    isAnime=true;
                }
                cardViewForm.setVisibility(View.VISIBLE);
            }
        });

    }
    public void WhenTrue(){
        //Eger form başarı ile gönderildiyse
        Intent i = new Intent(this, Sucsesfull.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private class gonder  extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{
                //Bilgiler değişkene ekleniyor
                String link=Connect_PHP.SEND_DATA;
                String data  = URLEncoder.encode("tc", "UTF-8") + "=" +
                        URLEncoder.encode(tc, "UTF-8");
                data += "&" + URLEncoder.encode("year", "UTF-8") + "=" +
                        URLEncoder.encode(year, "UTF-8");
                data += "&" + URLEncoder.encode("name", "UTF-8") + "=" +
                        URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("lastname", "UTF-8") + "=" +
                        URLEncoder.encode(lastname, "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" +
                        URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("tel", "UTF-8") + "=" +
                        URLEncoder.encode(telefon, "UTF-8");
                data += "&" + URLEncoder.encode("sikayet", "UTF-8") + "=" +
                        URLEncoder.encode(sikayet, "UTF-8");
                data += "&" + URLEncoder.encode("fakulte", "UTF-8") + "=" +
                        URLEncoder.encode(fakulte, "UTF-8");
                data += "&" + URLEncoder.encode("sinif", "UTF-8") + "=" +
                        URLEncoder.encode(sinif, "UTF-8");
                data += "&" + URLEncoder.encode("ariza", "UTF-8") + "=" +
                        URLEncoder.encode(ariza, "UTF-8");
                //Link açılıyor
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                //Bilgiler php ye gönderiliyor.
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();
                //Php den gelen veri okunuyor.
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

            progressBar.dialogClose();

            if(cevap.equals("E"))//Başarılı bir şekilde form gönderildiyse
                WhenTrue();
            else//Gönderilemediyse
                Toast.makeText(Form.this, "Form Gönderilemedi, Formu Kontrol Ediniz", Toast.LENGTH_LONG).show();

        }
    }
}
