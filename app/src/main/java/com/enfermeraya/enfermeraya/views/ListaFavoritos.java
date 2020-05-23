package com.enfermeraya.enfermeraya.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.enfermeraya.enfermeraya.R;

public class ListaFavoritos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_favoritos);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
