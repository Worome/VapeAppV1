package com.trianacodes.script.vapeapp.actividades;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.trianacodes.script.vapeapp.R;
import com.trianacodes.script.vapeapp.fragments.AromaListaFragment;
import com.trianacodes.script.vapeapp.fragments.AromaNuevoFragment;
import com.trianacodes.script.vapeapp.fragments.DefectoFragment;
import com.trianacodes.script.vapeapp.interfaces.fragmentos;

public class GeneralActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, fragmentos{

    Fragment defecto;
    Fragment aromaNuevo;
    Fragment aromaLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        defecto = new DefectoFragment();
        aromaNuevo = new AromaNuevoFragment();
        aromaLista = new AromaListaFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, defecto).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragmentoSeleccionado = null;
        boolean estaSeleccionado = false;

        if (id == R.id.nav_inicio) {
            //Vuelvo al activity Inicio
            finish();
        } else if (id == R.id.nav_nuevo) {
            /* Para que no se muestre error al hacer esta asignación, en el archivo java del
            * Fragment hay que hacer que su clase extienda de android.support.v4.app.Fragment */
            fragmentoSeleccionado = new AromaNuevoFragment();
            estaSeleccionado = true;
        } else if (id == R.id.nav_consulta) {
            fragmentoSeleccionado = new AromaListaFragment();
            estaSeleccionado = true;
        } else if (id == R.id.nav_info) {
            estaSeleccionado = true;
        } else if (id == R.id.nav_salir){
            estaSeleccionado = true;
        }

        if (estaSeleccionado){
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_fragments,fragmentoSeleccionado).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
