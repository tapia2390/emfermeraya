package com.enfermeraya.enfermeraya.ui.Ayuda;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.clases.ClassTerminosYCondiciones;
import com.enfermeraya.enfermeraya.views.Contactanos;
import com.enfermeraya.enfermeraya.views.InfoApp;
import com.enfermeraya.enfermeraya.views.MainActivity;
import com.enfermeraya.enfermeraya.views.PreguntasFrecuetes;
import com.enfermeraya.enfermeraya.views.Teminos;

public class AyudaFragment extends Fragment {

    private AyudaViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(AyudaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ayuda, container, false);

        final LinearLayout lypreguntas = (LinearLayout) root.findViewById(R.id.lypreguntas);
        final LinearLayout lycontacto = (LinearLayout) root.findViewById(R.id.lycontacto);
        final LinearLayout lycondiciones = (LinearLayout) root.findViewById(R.id.lycondiciones);
        final LinearLayout lyinfo = (LinearLayout) root.findViewById(R.id.lyinfo);

        lypreguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PreguntasFrecuetes.class);
                startActivity(i);
            }
        });

        lycontacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Contactanos.class);
                startActivity(i);
            }
        });


        lycondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Teminos.class);
                startActivity(i);
            }
        });

        lyinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), InfoApp.class);
                startActivity(i);
            }
        });



        return root;

    }
}
