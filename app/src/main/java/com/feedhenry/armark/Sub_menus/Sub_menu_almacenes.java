package com.feedhenry.armark.Sub_menus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.feedhenry.armark.ArAlmacenActivity;
import com.feedhenry.armark.IrAlmacenActivity;
import com.feedhenry.armark.R;
import com.feedhenry.armark.fragmentos.Detalles_almacenes_fragment;
import com.feedhenry.armark.fragmentos.Promociones_fragment;

public class Sub_menu_almacenes extends AppCompatActivity {
    private String idalmacen, idwebAlmacenes, varControl;
    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu_almacenes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sub_almaneces);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String nombre = getIntent().getStringExtra("nombrealmacen");
        getSupportActionBar().setTitle(nombre);


        // recibimos parametros o identificadores
        varControl = getIntent().getStringExtra("varcontrol");
        idwebAlmacenes = getIntent().getStringExtra("idwebalmacenes");
        idalmacen = getIntent().getStringExtra("idalmacen");
        Log.d("varcon", varControl);

        //  adaptador para los fragmentos
        SeccionPageradapter seccionPageradapter1 = new SeccionPageradapter(getSupportFragmentManager(),
                Sub_menu_almacenes.this);


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_sub_almacen);
        assert mViewPager != null;
        mViewPager.setAdapter(seccionPageradapter1);
        // mViewPager.setOffscreenPageLimit(1); //  Guarda la cantidad (int 0)de fragment que esta en el viewPager.  este caso es 0, las destruye de una todas las posiciones de fragment

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_sub_almacen);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_almacenes, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.id_menu_ar:
                Intent intentAr = new Intent(this, ArAlmacenActivity.class);
                startActivity(intentAr);
                /* Intent intentAr = new Intent(getApplicationContext(), ArAlmacenActivity.class);
                getApplicationContext().startActivity(intentAr);*/

                break;
            case R.id.id_menu_mapa:
                Intent intentMap = new Intent(this, IrAlmacenActivity.class);
                startActivity(intentMap);

                /*Intent intentMap = new Intent(getApplicationContext(), IrAlmacenActivity.class);
                getApplicationContext().startActivity(intentMap);*/
                break;
        }


        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (varControl != null) {
            if (varControl.equals("PROMOCIONES")) {
                mViewPager.setCurrentItem(1);
            } else if (varControl.equals("CATEGORIAS")) {
                mViewPager.setCurrentItem(2);
            }
        }
    }

    //**********************INICIO************************************************************************************************
    public class SeccionPageradapter extends FragmentStatePagerAdapter {
        final int PAGE_COUNT = 3;
        private Context context;

        public SeccionPageradapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            //PaginaActual = position;

            switch (position) {
                case 0:

                    return Detalles_almacenes_fragment.newInstance(position + 1, idwebAlmacenes);

                case 1:

                    return Promociones_fragment.newInstance(position + 2, "ALMACEN", idwebAlmacenes);

                case 2:

                    return Detalles_almacenes_fragment.newInstance(position + 1, idwebAlmacenes);

                default:
                    return null;

            }

        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Detalle";
                case 1:
                    return "Promociones";
                case 2:
                    return "Categorias";

            }
            return null;
        }
    }
    //*************************************************************************************************************************

}
