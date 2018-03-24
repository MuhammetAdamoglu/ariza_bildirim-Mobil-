package com.adamoglu.arzabildirim;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ChangePassword {
    private MaterialEditText old_p,new_p,new_r_p;
    private Context context;
    private String ID;
    private String passID;

    private String old_pass,new_pass,new_r_pass;

    static final String CONTROL="CONTROL";
    static final String UPDATE="UPDATE";

    ChangePassword(Context context, final String id, MaterialEditText old_p, MaterialEditText new_p, MaterialEditText new_r_p, TextView bt_change){
        this.context=context;


        old_p.setBaseColor(Color.parseColor("#FF00897B"));
        old_p.setMaxCharacters(30);
        old_p.setMinCharacters(5);

        new_p.setBaseColor(Color.parseColor("#FF00897B"));
        new_p.setMaxCharacters(30);
        new_p.setMinCharacters(5);

        new_r_p.setBaseColor(Color.parseColor("#FF00897B"));
        new_r_p.setMaxCharacters(30);
        new_r_p.setMinCharacters(5);

        this.old_p=old_p;
        this.new_p=new_p;
        this.new_r_p=new_r_p;

        bt_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                control(id);
            }
        });

    }

    public void control(String id){

        old_pass=old_p.getText().toString();
        new_pass=new_p.getText().toString();
        new_r_pass=new_r_p.getText().toString();

        if(!old_pass.equals("") && !new_pass.equals("") && !new_r_pass.equals("")){//Boş değil ise
            //min ve max kurallarına uyuluyorsa
            if(old_p.isCharactersCountValid() && new_p.isCharactersCountValid() && new_r_p.isCharactersCountValid()){
                if(!new_pass.contains(" ")){//şifrede boşluk yok ise
                    if(new_p.getText().toString().equals(new_r_p.getText().toString())){//yeni şifre ve tekrar şifre uyuşuyorsa
                        //eski şifre kontrol edildi
                        this.ID=id;
                        new control(ID,CONTROL,old_p.getText().toString()).execute();
                    }else{
                        Toast.makeText(context, "Şifre Uyuşmuyor", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Şifrede Boşluk Olamaz", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    String cevap;

    private class control  extends AsyncTask {

        private String ID;
        private String passID;
        private String password;

        control(String ID, String passID, String password){
            this.ID=ID;
            this.passID=passID;
            this.password=password;
        }

        private void onReset(){
            old_p.setText("");
            new_p.setText("");
            new_r_p.setText("");
            Toast.makeText(context, "Şifre Güncellendi", Toast.LENGTH_SHORT).show();
        }

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{


                String link=Connect_PHP.PASSWORD;
                String data  = URLEncoder.encode("ID", "UTF-8") + "=" +
                        URLEncoder.encode(ID, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("passwordID", "UTF-8") + "=" +
                        URLEncoder.encode(passID, "UTF-8");


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

            try {
                switch (cevap) {
                    case "C_E"://eski şifre doğru ise
                        //yeni şifre güncellenmesi için gphp ye gönderildi
                        new control(ID,UPDATE,new_p.getText().toString()).execute();
                        break;
                    case "C_H"://eski şifre yanlş ise
                        Toast.makeText(context, "Şifre Yanlış", Toast.LENGTH_SHORT).show();
                        break;
                    case "U_E"://güncelleme başarılı ise
                        //inputlar sıfırlandı
                        onReset();
                        break;
                    case "U_H"://güncellemem başarısız ise
                        Toast.makeText(context, "Bir Sorun Oluştu", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (Exception ex){}



        }
    }
}
