package com.adamoglu.arzabildirim;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AddPerson {

    private AdminPanel adminPanel;
    private Context context;
    private ProgressBar progressBar;
    private String userName,pass,a_pass,name,lastname,email,phone,jop;


    AddPerson(final AdminPanel adminPanel){
        this.adminPanel=adminPanel;
        this.context=adminPanel.getApplicationContext();

        progressBar=new ProgressBar(adminPanel);


        ArrayAdapter adapter_ariza=createAdapter(Informations.getArizalar());
        adminPanel.spinner_meslek.setAdapter(adapter_ariza);

        adminPanel.userName.setBaseColor(Color.parseColor("#FF00897B"));
        adminPanel.userName.setMaxCharacters(30);
        adminPanel.userName.setMinCharacters(3);

        adminPanel.pass.setBaseColor(Color.parseColor("#FF00897B"));
        adminPanel.pass.setMaxCharacters(30);
        adminPanel.pass.setMinCharacters(5);

        adminPanel.a_pass.setBaseColor(Color.parseColor("#FF00897B"));
        adminPanel.a_pass.setMaxCharacters(30);
        adminPanel.a_pass.setMinCharacters(5);

        adminPanel.name.setBaseColor(Color.parseColor("#FF00897B"));
        adminPanel.name.setMaxCharacters(30);
        adminPanel.name.setMinCharacters(2);

        adminPanel.lastname.setBaseColor(Color.parseColor("#FF00897B"));
        adminPanel.lastname.setMaxCharacters(30);
        adminPanel.lastname.setMinCharacters(2);

        adminPanel.email.setBaseColor(Color.parseColor("#FF00897B"));
        adminPanel.email.setMaxCharacters(40);
        adminPanel.email.setMinCharacters(2);

        adminPanel.phone.setBaseColor(Color.parseColor("#FF00897B"));
        adminPanel.phone.setMaxCharacters(10);
        adminPanel.phone.setMinCharacters(10);

        adminPanel.bt_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(adminPanel.userName.isCharactersCountValid() && adminPanel.pass.isCharactersCountValid() && adminPanel.a_pass.isCharactersCountValid() && adminPanel.name.isCharactersCountValid() && adminPanel.lastname.isCharactersCountValid() && adminPanel.email.isCharactersCountValid() && adminPanel.phone.isCharactersCountValid()){

                    userName=adminPanel.userName.getText().toString();
                    pass=adminPanel.pass.getText().toString();
                    a_pass=adminPanel.a_pass.getText().toString();
                    name=adminPanel.name.getText().toString();
                    lastname=adminPanel.lastname.getText().toString();
                    email=adminPanel.email.getText().toString();
                    phone=adminPanel.phone.getText().toString();
                    jop=adminPanel.spinner_meslek.getText().toString();

                    if(!userName.equals("") && !pass.equals("") && !a_pass.equals("") && !name.equals("") && !lastname.equals("") && !email.equals("") && !phone.equals("")&& !jop.equals("")){
                        if(!pass.contains(" ")){
                            if(pass.equals(a_pass)){
                                if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                                    if(phone.toCharArray()[0]=='5'){
                                        progressBar.dialogShow("Yeni Üye Ekleniyor...");
                                        new gonder().execute();
                                    }
                                    else
                                        Toast.makeText(adminPanel, "Geçersiz Telefon", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(adminPanel, "Geçersiz E-Posta", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(adminPanel, "Şifreler Uyuşmuyor", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(adminPanel, "Şifrede Boşluk Olamaz", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });

    }

    private ArrayAdapter createAdapter(ArrayList array){


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(adminPanel,
                android.R.layout.simple_dropdown_item_1line, array);

        return adapter;
    }

    private String cevap="";
    private class gonder  extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{

                String link=Connect_PHP.ADD_PERSON;
                String data  = URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(pass, "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" +
                        URLEncoder.encode(userName, "UTF-8");
                data += "&" + URLEncoder.encode("name", "UTF-8") + "=" +
                        URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("lastname", "UTF-8") + "=" +
                        URLEncoder.encode(lastname, "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" +
                        URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("meslek", "UTF-8") + "=" +
                        URLEncoder.encode(jop, "UTF-8");
                data += "&" + URLEncoder.encode("tel", "UTF-8") + "=" +
                        URLEncoder.encode(phone, "UTF-8");


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

            progressBar.dialogClose();

            switch (cevap) {
                case "E":
                    ClearInputs();
                    break;
                case "Z":
                    Toast.makeText(adminPanel, "Bu Kullanıcı Adı Veya E-Posta Zaten Var", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(adminPanel, "Bir Sorun Oluştu", Toast.LENGTH_LONG).show();
                    break;
            }

        }

        private void ClearInputs(){
            adminPanel.userName.setText("");
            adminPanel.pass.setText("");
            adminPanel.a_pass.setText("");
            adminPanel.name.setText("");
            adminPanel.lastname.setText("");
            adminPanel.email.setText("");
            adminPanel.phone.setText("");
        }
    }

}
