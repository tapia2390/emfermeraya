package com.enfermeraya.enfermeraya.dapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Favoritos;
import com.enfermeraya.enfermeraya.comandos.ComandoSercicio;

import java.util.ArrayList;
import java.util.List;

public class FavoritoAdapter extends RecyclerView.Adapter<FavoritoAdapter.FavoritoViewHolder> implements Filterable , ComandoSercicio.OnSercicioChangeListener{

    private Context context;
    private List<Favoritos> nameList;
    private List<Favoritos> filteredNameList;
    ComandoSercicio comandoSercicio;
    Modelo modelo = Modelo.getInstance();

    public FavoritoAdapter(Context context, List<Favoritos> nameList) {
        super();
        this.context = context;
        this.nameList = nameList;
        this.filteredNameList = nameList;
        comandoSercicio = new ComandoSercicio(this);
    }

    @NonNull
    @Override
    public FavoritoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_favoritos_servicio, viewGroup, false);
        return new FavoritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritoViewHolder holder, final int position) {
        holder.text_dir.setText(filteredNameList.get(position).getDireccion());
        holder.text_nombre.setText(filteredNameList.get(position).getTitulo());

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
                   notify();
                    notifyDataSetChanged();
                }else{
                    holder.image_fav.setBackgroundResource(R.drawable.start);
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
                            if(input_field.getText().toString().equals("")) {
                                Toast.makeText(context, "Ingrese el nombre del favorito", Toast.LENGTH_LONG).show();
                                //input_field.setError("Ingrese el nombre del favorito");
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

                    notify();
                    notifyDataSetChanged();
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
                    List<Favoritos> filteredList = new ArrayList<>();
                    for (Favoritos name : nameList) {
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
                filteredNameList = (List<Favoritos>) results.values;
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


    class FavoritoViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_fav;
        private TextView text_dir;
        private TextView text_nombre;
        private LinearLayout layuotdata;

        FavoritoViewHolder(@NonNull View itemView) {
            super(itemView);
            image_fav = itemView.findViewById(R.id.image_fav);
            text_dir = itemView.findViewById(R.id.text_dir);
            text_nombre = itemView.findViewById(R.id.text_nombre);
            layuotdata = itemView.findViewById(R.id.layuotdata);
        }
    }

}