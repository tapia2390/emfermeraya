package com.enfermeraya.enfermeraya.dapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Servicios;
import com.enfermeraya.enfermeraya.comandos.ComandoSercicio;
import com.enfermeraya.enfermeraya.models.utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> implements Filterable, ComandoSercicio.OnSercicioChangeListener {

    private Context context;
    private List<Servicios> nameList;
    private List<Servicios> filteredNameList;
    ComandoSercicio comandoSercicio;
    Modelo modelo = Modelo.getInstance();

    public ServicioAdapter(Context context, List<Servicios> nameList) {
        super();
        this.context = context;
        this.nameList = nameList;
        this.filteredNameList = nameList;

        comandoSercicio = new ComandoSercicio(this);

    }

    @NonNull
    @Override
    public ServicioAdapter.ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_favoritos_servicio, viewGroup, false);
        return new ServicioAdapter.ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioAdapter.ServicioViewHolder holder, final int position) {
        String direccion = filteredNameList.get(position).getDireccion();
        String titlo = filteredNameList.get(position).getTitulo();
        holder.text_dir.setText(direccion);
        holder.text_nombre.setText(titlo);


        if(filteredNameList.get(position).getEstado().equals("true")){
            holder.image_fav.setBackgroundResource(R.drawable.start);
        }else{
            holder.image_fav.setBackgroundResource(R.drawable.start2);
        }


        holder.image_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(filteredNameList.get(position).getEstado().equals("true")){
                    holder.image_fav.setBackgroundResource(R.drawable.start2);
                    comandoSercicio.updateFavorito("false",filteredNameList.get(position).getKey(),"");
                }else{
                    /*alert*/

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Enter name:");

                    final EditText input_field = new EditText(context);
                    input_field.setText("");

                    builder.setCancelable(false);
                    builder.setView(input_field);
                    input_field.setText("");
                    input_field.setSelection(input_field.getText().length());
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(TextUtils.isEmpty(input_field.getText().toString())) {
                                //input_field.setError("Ingrese el nombre del favorito");
                                Toast.makeText(context, "Ingrese el nombre del favorito", Toast.LENGTH_LONG).show();

                            }else{
                                holder.image_fav.setBackgroundResource(R.drawable.start);
                                comandoSercicio.updateFavorito("true",filteredNameList.get(position).getKey(),input_field.getText().toString());

                            }

                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();

                        }
                    });

                    builder.show();


                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredNameList.size();
    }

    /**
     * <p>Returns a filter that can be used to constrain data with a filtering
     * pattern.</p>
     *
     * <p>This method is usually implemented by {@link RecyclerView.Adapter}
     * classes.</p>
     *
     * @return a filter used to constrain data
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredNameList = nameList;
                } else {
                    List<Servicios> filteredList = new ArrayList<>();
                    for (Servicios name : nameList) {
                        if (name.getDireccion().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(name);
                        }
                        filteredNameList = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = filteredNameList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNameList = (List<Servicios>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void getTipoServicio() {

    }

    @Override
    public void getServicio() {

    }

    @Override
    public void cargoServicio() {

    }

    @Override
    public void errorServicio() {

    }

    @Override
    public void actualizarFavorito() {


        comandoSercicio.getListServicio();
    }

    class ServicioViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_fav;
        private TextView text_dir;
        private TextView text_nombre;
        private LinearLayout layuotdata;

        ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            image_fav = itemView.findViewById(R.id.image_fav);
            text_dir = itemView.findViewById(R.id.text_dir);
            text_nombre = itemView.findViewById(R.id.text_nombre);
            layuotdata = itemView.findViewById(R.id.layuotdata);
        }
    }

}