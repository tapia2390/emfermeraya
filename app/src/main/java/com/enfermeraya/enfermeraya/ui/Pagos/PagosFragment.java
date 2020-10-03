package com.enfermeraya.enfermeraya.ui.Pagos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.views.CrearTarjeta;
import com.enfermeraya.enfermeraya.views.CrearUserCulqi;
import com.enfermeraya.enfermeraya.R;

public class PagosFragment extends Fragment {

    private PagosViewModel galleryViewModel;
    private Button btntarjeta;
    Modelo modelo =  Modelo.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(PagosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pagos, container, false);
        btntarjeta = (Button) root.findViewById(R.id.btntarjeta);

        btntarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modelo.usuario.getIdculqi() == null || modelo.usuario.getIdculqi().equals("")){
                    Intent i = new Intent(getActivity().getApplicationContext(), CrearUserCulqi.class);
                    startActivity(i);
                }else{
                    modelo.qulqiId = modelo.usuario.getIdculqi();
                    Intent i = new Intent(getActivity().getApplicationContext(), CrearTarjeta.class);
                    startActivity(i);
                }

            }
        });
        return root;
    }

    //https://apidocs.culqi.com/#/tokens

}
