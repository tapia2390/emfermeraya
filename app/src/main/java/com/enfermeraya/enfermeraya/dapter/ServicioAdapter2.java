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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Servicios;
import com.enfermeraya.enfermeraya.comandos.ComandoHistorial;
import com.enfermeraya.enfermeraya.comandos.ComandoSercicio;
import com.enfermeraya.enfermeraya.views.DetalleHistorial;

import java.util.ArrayList;
import java.util.List;

public class ServicioAdapter2 extends RecyclerView.Adapter<ServicioAdapter2.ServicioViewHolder> implements Filterable, ComandoSercicio.OnSercicioChangeListener, ComandoHistorial.OnHistorialChangeListener {

    private Context context;
    private List<Servicios> nameList;
    private List<Servicios> filteredNameList;
    ComandoSercicio comandoSercicio;
    ComandoHistorial comandoHistorial;
    Modelo modelo = Modelo.getInstance();

    public ServicioAdapter2(Context context, List<Servicios> nameList) {
        super();
        this.context = context;
        this.nameList = nameList;
        this.filteredNameList = nameList;

        comandoSercicio = new ComandoSercicio(this);
        comandoHistorial =  new ComandoHistorial(this);

    }

    @NonNull
    @Override
    public ServicioAdapter2.ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_service, viewGroup, false);
        return new ServicioAdapter2.ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioAdapter2.ServicioViewHolder holder, final int position) {
        String direccion = filteredNameList.get(position).getDireccion();
        String titlo = filteredNameList.get(position).getInformacion();
        String fecha = filteredNameList.get(position).getFecha();
        String estado = filteredNameList.get(position).getEstado();
        double calificacion = filteredNameList.get(position).getCalificaion();
        holder.text_dir.setText(direccion);
        holder.text_nombre.setText(titlo);
        holder.text_fecha.setText(fecha);


        holder.text_estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelo.servicios = filteredNameList.get(position);
                Intent i = new Intent(context, DetalleHistorial.class);
                context.startActivity(i);
            }
        });





        if(filteredNameList.get(position).getEstado().equals("true")){
            holder.image_fav.setBackgroundResource(R.drawable.start);
        }else{
            holder.image_fav.setBackgroundResource(R.drawable.start2);
        }


        ///modal calificiacion
        holder.btn_calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View layout= null;
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    layout = inflater.inflate(R.layout.dialog_calificacion, null);
                    final RatingBar ratingBar = (RatingBar)layout.findViewById(R.id.ratingBar);
                    builder.setTitle("Calificacion");
                    builder.setMessage("Gracias por calificarnos, nos ayudará a brindarle el mejor servicio. .");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {




                            Float value = ratingBar.getRating();
                            double califi = Double.parseDouble(value.toString());
                            Toast.makeText(context,"su calificación es : "+califi,Toast.LENGTH_LONG).show();
                            comandoHistorial.calificar(filteredNameList.get(position).getKey(),califi);
                            holder.text_estado.setVisibility(View.GONE);
                            holder.btn_calificar.setVisibility(View.GONE);
                            holder.start.setVisibility(View.VISIBLE);
                            holder.txt_calificacion.setText(""+califi);
                        }
                    });
                    builder.setNegativeButton("No,Gracias", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(false);
                    builder.setView(layout);
                    builder.show();

                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public void errorHistorial() {

    }

    @Override
    public void okHistorial() {

    }

    class ServicioViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_fav;
        private TextView text_dir;
        private TextView text_nombre;
        private TextView text_fecha;
        private Button text_estado;
        private LinearLayout layuotdata;
        private LinearLayout layoutcalificacion;
        private Button btn_calificar;
        private LinearLayout start;
        private TextView txt_calificacion;

        ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            image_fav = itemView.findViewById(R.id.image_fav);
            text_dir = itemView.findViewById(R.id.text_dir);
            text_nombre = itemView.findViewById(R.id.text_nombre);
            text_fecha = itemView.findViewById(R.id.text_fecha);
            layuotdata = itemView.findViewById(R.id.layuotdata);
            text_estado = itemView.findViewById(R.id.text_estado);
            layoutcalificacion = itemView.findViewById(R.id.layoutcalificacion);
            btn_calificar = itemView.findViewById(R.id.btn_calificar);
            start = itemView.findViewById(R.id.start);
            txt_calificacion = itemView.findViewById(R.id.txt_calificacion);

        }
    }

}