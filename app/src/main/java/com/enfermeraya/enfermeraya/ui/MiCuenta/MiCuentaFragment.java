package com.enfermeraya.enfermeraya.ui.MiCuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.enfermeraya.enfermeraya.R;

public class MiCuentaFragment extends Fragment {

    private com.enfermeraya.enfermeraya.ui.MiCuenta.MiCuentaViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mi_cuenta, container, false);

        return root;
    }
}
