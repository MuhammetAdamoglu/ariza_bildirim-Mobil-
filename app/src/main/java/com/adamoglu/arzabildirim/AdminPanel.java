package com.adamoglu.arzabildirim;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AdminPanel extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    SharedPreferences sharedPreferences(){
        return getSharedPreferences("", MODE_PRIVATE);
    }
    SharedPreferences.Editor editor(){
        return getSharedPreferences("", MODE_PRIVATE).edit();
    }

    public String meslek,bildiri,id,kullaniciAdi;
    private String cevap=null;
    private int lastid=0;

    private ArrayList<String[]> listArizlar = new ArrayList<>();
    private ArrayList<String[]> listUyeler = new ArrayList<>();

    public RecyclerView.Adapter mAdapter;

    private RecyclerView mRecyclerView;

    private ScrollView addPerson;

    private MaterialEditText old_p,new_p,new_r_p;

    ConstraintLayout layoth_UyeEkle,layout_changePassword;
    DrawerLayout drawer;

    TextView bt_gonder,bt_degis;
    MaterialEditText userName,pass,a_pass,name,lastname,email,phone;
    MaterialBetterSpinner spinner_meslek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_paneli);

        layoth_UyeEkle=findViewById(R.id.Layouth_Panel);
        layout_changePassword=findViewById(R.id.layout_changePassword);
        addPerson=findViewById(R.id.scrollView);

        userName=findViewById(R.id.editText_username);
        pass=findViewById(R.id.editText_passworld);
        a_pass=findViewById(R.id.editText_againpassword);
        name=findViewById(R.id.editText_name);
        lastname=findViewById(R.id.editText_lastname);
        email=findViewById(R.id.editText_email);
        phone=findViewById(R.id.editText_phone);

        old_p=findViewById(R.id.editText_oldPasword);
        new_p=findViewById(R.id.editText_newPassworld);
        new_r_p=findViewById(R.id.editText_againpassword2);

        bt_gonder=findViewById(R.id.button_gonder);
        bt_degis=findViewById(R.id.button_change);

        spinner_meslek=findViewById(R.id.spinner_jop);

        new AddPerson(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        String [] splitCevap=sharedPreferences().getString("meslek","").split("-");
        id=splitCevap[0];
        meslek="admin";
        bildiri=splitCevap[2];
        kullaniciAdi=splitCevap[3];


        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyCardAdapterForAdmin(this, listArizlar);
        mRecyclerView.setAdapter(mAdapter);

        getUye();

        new ChangePassword(getApplicationContext(),id,old_p,new_p,new_r_p,bt_degis);

        final Handler handler=new Handler();
        Runnable r=new Runnable() {
            @SuppressLint("RestrictedApi")
            public void run() {
                new getArizalar().execute();
                handler.postDelayed(this, 5000);

            }
        };
        handler.postDelayed(r, 0);


    }

    public void get(){
        lastid=0;
        new getArizalar().execute();
    }

    public void getUye(){
        new getUyeler().execute();
    }


    private class getArizalar extends AsyncTask {

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
                    listArizlar.clear();
                    mAdapter.notifyDataSetChanged();
                }else {
                    String[] splitCevap1=cevap.split("-");

                    if(splitCevap1[0].equals("true")){
                        bildiri=splitCevap1[1];

                        lastid=0;
                        new getArizalar().execute();

                    }else{
                        try {
                            if(lastid==0)
                                listArizlar.clear();

                            String splitCevap[] = cevap.split("/");

                            for(int i=0; i<splitCevap.length;i++){
                                String splitCevap2[]=splitCevap[i].split("-");
                                if(listArizlar.size()==0)
                                    listArizlar.add(splitCevap2);
                                else
                                    listArizlar.add(0,splitCevap2);
                            }

                            lastid= Integer.parseInt(listArizlar.get(0)[0]);

                            mAdapter.notifyDataSetChanged();

                        }catch (Exception ex){}
                    }

                }

            }
        }
    }

    private class getUyeler extends AsyncTask {

        protected void onPreExecute(){
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{

                String link=Connect_PHP.GET_UYELER;
                String data  = URLEncoder.encode("post", "UTF-8") + "=" +
                        URLEncoder.encode("", "UTF-8");

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

                try {
                    listUyeler.clear();

                    String splitCevap[] = cevap.split("/");

                    for(int i=0; i<splitCevap.length;i++){
                        String splitCevap2[]=splitCevap[i].split("-");
                        listUyeler.add(splitCevap2);
                    }

                    lastid= Integer.parseInt(listUyeler.get(0)[0]);

                    mAdapter.notifyDataSetChanged();

                }catch (Exception ex){}

            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.panel) {
            mRecyclerView.setVisibility(View.VISIBLE);
            addPerson.setVisibility(View.GONE);
            layout_changePassword.setVisibility(View.GONE);

            get();
            mAdapter = new MyCardAdapterForAdmin(this, listArizlar);
            mRecyclerView.setAdapter(mAdapter);
        } else if (id == R.id.uyeler) {
            mRecyclerView.setVisibility(View.VISIBLE);
            addPerson.setVisibility(View.GONE);
            layout_changePassword.setVisibility(View.GONE);

            getUye();
            mAdapter = new MyCardAdapterForUyeler(this, listUyeler);
            mRecyclerView.setAdapter(mAdapter);
        } else if (id == R.id.uyeekle) {
            mRecyclerView.setVisibility(View.GONE);
            addPerson.setVisibility(View.VISIBLE);
            layout_changePassword.setVisibility(View.GONE);

        } else if(id==R.id.sifredegis){
            mRecyclerView.setVisibility(View.GONE);
            addPerson.setVisibility(View.GONE);
            layout_changePassword.setVisibility(View.VISIBLE);
        }
        else if(id==R.id.cikisyap){
            editor().remove("username").commit();
            editor().remove("password").commit();

            Intent i = new Intent(AdminPanel.this,TcKimlikSorgu.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
