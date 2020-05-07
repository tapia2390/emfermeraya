package com.enfermeraya.enfermeraya.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.Splash;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.comandos.ComandoTerminosYCondiciones;

public class Teminos extends Activity implements ComandoTerminosYCondiciones.OnComandoTerminosYCondicionesChangeListener {



    TextView txt_terminosycondiciones;
    ComandoTerminosYCondiciones comandoTerminosYCondiciones;
    TextView terminos_condiciones_texto;
    Button terminos_condiciones;
    Modelo modelo = Modelo.getInstance();
    String uidProducto;
    String abiertascerrada;
    int posicionServicio = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_teminos);


        if (savedInstanceState != null) {
            Intent i = new Intent(getApplicationContext(), Splash.class);
            startActivity(i);
            finish();
            return;
        }

        txt_terminosycondiciones = (TextView) findViewById(R.id.terminos_condiciones_texto);


        comandoTerminosYCondiciones = new ComandoTerminosYCondiciones(this);
        comandoTerminosYCondiciones.getTerminos_y_Condiciones();

        terminos_condiciones_texto = (TextView) findViewById(R.id.terminos_condiciones_texto);
        terminos_condiciones = (Button) findViewById(R.id.terminos_condiciones);


        txt_terminosycondiciones.setText(modelo.terminosycondiciones);

    }

    @Override
    public void terminos_y_Condiciones() {

        terminos_condiciones.setText(""+modelo.classTerminosYCondiciones.getTitulo());
        terminos_condiciones_texto.setText(""+modelo.classTerminosYCondiciones.getTexto());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}

