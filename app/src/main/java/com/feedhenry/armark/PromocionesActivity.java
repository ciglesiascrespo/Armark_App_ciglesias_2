package com.feedhenry.armark;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.feedhenry.armark.fragmentos.Promociones_fragment;

public class PromocionesActivity extends AppCompatActivity {

    String idCategoria ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_promociones);
        setSupportActionBar(toolbar);
        String nombre = getIntent().getStringExtra("nombreCategoria");
        getSupportActionBar().setTitle(nombre);


        idCategoria = getIntent().getExtras().getString("idCategoria");

        Log.e("PromocionesActivity","idCategoria: " + idCategoria);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Promociones_fragment promocionesFragment =  Promociones_fragment.newInstance( 2, "CATEGORIA", idCategoria);

        fragmentTransaction.add(R.id.fragment_container, promocionesFragment, "HELLO");
        fragmentTransaction.commit();

    }
}
