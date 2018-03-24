package com.adamoglu.arzabildirim;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Sucsesfull extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucsesfull);

        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                Intent i = new Intent(Sucsesfull.this,TcKimlikSorgu.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        };
        handler.postDelayed(r, 2000);
    }
}
