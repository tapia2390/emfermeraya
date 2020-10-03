package com.enfermeraya.enfermeraya.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.comandos.ComandoPerfil;
import com.enfermeraya.enfermeraya.culqi.Culqi.Card;
import com.enfermeraya.enfermeraya.culqi.Culqi.Cliente;
import com.enfermeraya.enfermeraya.culqi.Culqi.ClienteCallback;
import com.enfermeraya.enfermeraya.culqi.Culqi.Customer;
import com.enfermeraya.enfermeraya.culqi.Culqi.Token;
import com.enfermeraya.enfermeraya.culqi.Culqi.TokenCallback;
import com.enfermeraya.enfermeraya.notificacion.Client;
import com.enfermeraya.enfermeraya.views.CrearTarjeta;
import com.enfermeraya.enfermeraya.views.MainActivity;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CrearUserCulqi extends Activity implements ComandoPerfil.OnPerfilChangeListener {

    String country_code  ="PE";
    EditText txtfirst_name,txtlast_name,txtemail,txtaddress,txtaddress_city,txtphone_number;
    ProgressDialog progress;
    Modelo modelo =  Modelo.getInstance();
    ComandoPerfil comandoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crear_user_culqi);


        progress = new ProgressDialog(this);
        progress.setMessage("Validando informacion del cliente");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        txtfirst_name = (EditText) findViewById(R.id.txtfirst_name);
        txtlast_name = (EditText) findViewById(R.id.txtlast_name);
        txtemail = (EditText) findViewById(R.id.txtemail);
        txtaddress = (EditText) findViewById(R.id.txtaddress);
        txtaddress_city = (EditText) findViewById(R.id.txtaddress_city);
        txtphone_number = (EditText) findViewById(R.id.txtphone_number);
        comandoPerfil = new ComandoPerfil(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void  tarjeta(View v){

        if(txtfirst_name.equals("") || txtlast_name.equals("") || txtemail.equals("") ||
                txtaddress.equals("") || txtaddress_city.equals("") || txtphone_number.equals("")){
            alertaError();
        }else{

           try {
               //progress.show();
           }
           catch (Exception ex ){}

            Customer customer = new Customer(txtfirst_name.getText().toString(), txtlast_name.getText().toString(),txtemail.getText().toString(),
                    txtaddress.getText().toString(),txtaddress_city.getText().toString(), country_code ,txtphone_number.getText().toString());

            Cliente client = new Cliente(modelo.apikeyculqi);

            client.createCliente(getApplicationContext(), customer, new ClienteCallback() {
                @Override
                public void onSuccess(JSONObject customer) {
                    try {

                        modelo.qulqiId =customer.get("id").toString();
                        //idculqi
                        comandoPerfil.actualizarUsuarioidculqi();






                    } catch (Exception ex){
                       // progress.hide();
                    }
                   // progress.hide();
                }

                @Override
                public void onError(Exception error) {
                    //progress.hide();
                    viewtarjeta();
                }
            });

        }

    }

    public void viewtarjeta(){
        Intent i = new Intent(getApplicationContext(), CrearTarjeta.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
        finish();
    }

    //alerta
    public void alerta(String titulo,String decripcion){

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(decripcion)
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismissWithAnimation();
                        viewtarjeta();

                    }
                })

                .show();
    }

    //alerta
    public void alertaError(){

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campos obligatorios")
                .setContentText("Todos los campos son requeridos")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @Override
    public void cargoUSuario() {

    }

    @Override
    public void setUsuarioListener() {
        alerta("Cliente","Cliente creado exitosamente");
    }

    @Override
    public void errorSetUsuario() {

    }
}