package com.adamoglu.arzabildirim;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Panel extends AppCompatActivity {

    SharedPreferences sharedPreferences(){
        return getSharedPreferences("", MODE_PRIVATE);
    }
    SharedPreferences.Editor editor(){
        return getSharedPreferences("", MODE_PRIVATE).edit();
    }

    public String meslek,bildiri,id,kullaniciAdi;
    private String cevap=null;
    private int lastid=0;

    private TextView cikis,sifreDegis;
    private MaterialEditText old_p,new_p,new_r_p;
    private ConstraintLayout layout_changePassword;
    TextView bt_degis;
    private ArrayList<String[]> list = new ArrayList<>();

    public RecyclerView.Adapter mAdapter;

    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        cikis=findViewById(R.id.textView_cikis);
        sifreDegis=findViewById(R.id.textView_sifreDegis);

        layout_changePassword=findViewById(R.id.layout_changePassword);

        String [] splitCevap=sharedPreferences().getString("meslek","").split("-");
        id=splitCevap[0];
        meslek=splitCevap[1];
        bildiri=splitCevap[2];
        kullaniciAdi=splitCevap[3];

        old_p=findViewById(R.id.editText_oldPasword);
        new_p=findViewById(R.id.editText_newPassworld);
        new_r_p=findViewById(R.id.editText_againpassword2);

        bt_degis=findViewById(R.id.button_change);

        new ChangePassword(getApplicationContext(),id,old_p,new_p,new_r_p,bt_degis);

        final RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyCardAdapterForPanel(this,list);
        mRecyclerView.setAdapter(mAdapter);

        final Handler handler=new Handler();
        Runnable r=new Runnable() {
            @SuppressLint("RestrictedApi")
            public void run() {
                new getData().execute();
                handler.postDelayed(this, 5000);

            }
        };
        handler.postDelayed(r, 0);


        cikis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor().remove("username").commit();
                editor().remove("password").commit();

                Intent i = new Intent(Panel.this,TcKimlikSorgu.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });


        sifreDegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(++count%2==1){
                    sifreDegis.setText("Panel");
                    mRecyclerView.setVisibility(View.GONE);
                    layout_changePassword.setVisibility(View.VISIBLE);
                }else {
                    sifreDegis.setText("Şifre Değiş");
                    mRecyclerView.setVisibility(View.VISIBLE);
                    layout_changePassword.setVisibility(View.GONE);
                }

            }
        });


    }

    public void get(){
        lastid=0;
        new getData().execute();
    }

    private class getData extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{

                String link=Connect_PHP.GET_DATA;
                String data  = URLEncoder.encode("meslek", "UTF-8") + "=" +
                        URLEncoder.encode(meslek, "UTF-8");
                data += "&" + URLEncoder.encode("lastid", "UTF-8") + "=" +
                        URLEncoder.encode(String.valueOf(lastid), "UTF-8");
                data += "&" + URLEncoder.encode("bildiri", "UTF-8") + "=" +
                        URLEncoder.encode(String.valueOf(bildiri), "UTF-8");
                data += "&" + URLEncoder.encode("id", "UTF-8") + "=" +
                        URLEncoder.encode(String.valueOf(id), "UTF-8");


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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);



            if(cevap!=null){

                if(cevap.equals("EMPTY")){
                    list.clear();
                    mAdapter.notifyDataSetChanged();
                }else {
                    String[] splitCevap1=cevap.split("-");


                    if(splitCevap1[0].equals("true")){
                        bildiri=splitCevap1[1];

                        lastid=0;
                        new getData().execute();

                    }else{
                        try {
                            if(lastid==0)
                                list.clear();

                            String splitCevap[] = cevap.split("/");

                            for(int i=0; i<splitCevap.length;i++){
                                String splitCevap2[]=splitCevap[i].split("-");
                                if(list.size()==0)
                                    list.add(splitCevap2);
                                else
                                    list.add(0,splitCevap2);
                            }

                            lastid= Integer.parseInt(list.get(0)[0]);

                            mAdapter.notifyDataSetChanged();

                        }catch (Exception ex){}
                    }
                }

            }

        }
    }
}
