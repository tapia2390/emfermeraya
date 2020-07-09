package com.enfermeraya.enfermeraya.ui.MisServicios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Servicios;
import com.enfermeraya.enfermeraya.comandos.ComandoSercicio;
import com.enfermeraya.enfermeraya.dapter.ServicioAdapter;
import com.enfermeraya.enfermeraya.dapter.ServicioAdapter2;
import com.enfermeraya.enfermeraya.models.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MisServiciosFragment extends Fragment implements ComandoSercicio.OnSercicioChangeListener{

    Utility utility;
    private MisServiciosViewModel slideshowViewModel;
    ComandoSercicio comandoSercicio;
    ServicioAdapter2 servicioAdapter;
    List<Servicios> serList = new ArrayList<>();
    Modelo modelo = Modelo.getInstance();
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(MisServiciosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mis_servicios, container, false);

        final SearchView search2 = (SearchView) root.findViewById(R.id.search_view);
         recyclerView = root.findViewById(R.id.recycler_view2);
        utility = new Utility();
        comandoSercicio = new ComandoSercicio(this);


        if (utility.estado(getActivity())) {
            comandoSercicio.getListServicio();


        }else{
            alerta("Sin Internet","Valide la conexión a internet");
        }



        search2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {


                    servicioAdapter.getFilter().filter(queryString);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryString) {

                  servicioAdapter.getFilter().filter(queryString);

                return false;
            }
        });


        return root;
    }


    public void alerta(String titulo, String descripcion){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance

                        sDialog.hide();
                    }
                })
                .show();
    }

    @Override
    public void getTipoServicio() {

    }

    @Override
    public void getServicio() {
        serList = modelo.listServicios;
        servicioAdapter = new ServicioAdapter2(getActivity(), serList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(servicioAdapter);
    }

    @Override
    public void cargoServicio() {

    }

    @Override
    public void errorServicio() {

    }

    @Override
    public void actualizarFavorito() {

    }
}
