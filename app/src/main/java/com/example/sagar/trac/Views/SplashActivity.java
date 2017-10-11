package com.example.sagar.trac.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sagar.trac.R;
import com.example.sagar.trac.Utils.Trac;

public class SplashActivity extends AppCompatActivity {
Trac trac;
    EditText ipaddress_txt;
    Button ip_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        trac=(Trac)getApplication();


        if (trac.getLoginStatus()){
            Intent intent= new Intent(SplashActivity.this,LoginAcivity.class);
            startActivity(intent);
            finish();
        }else {
            //TODO enter wifi ip
            String ipaddress="https://trackker.000webhostapp.com/";

            trac.setIPAddress(ipaddress);
            trac.setLoginStatus(true);
            Intent intent= new Intent(SplashActivity.this,LoginAcivity.class);
            startActivity(intent);
            finish();
        }




//TODO change elements of xml
    }
}
