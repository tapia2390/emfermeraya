package com.enfermeraya.enfermeraya.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.culqi.Culqi.Card;
import com.enfermeraya.enfermeraya.culqi.Culqi.Cargo;
import com.enfermeraya.enfermeraya.culqi.Culqi.CargoCallback;
import com.enfermeraya.enfermeraya.culqi.Culqi.ClassCargo;
import com.enfermeraya.enfermeraya.culqi.Culqi.Token;
import com.enfermeraya.enfermeraya.culqi.Culqi.TokenCallback;
import com.enfermeraya.enfermeraya.culqi.Validation.Validation;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CrearTarjeta extends Activity {

    Validation validation;
    ProgressDialog progress;
    TextView txtcardnumber, txtcvv, txtmonth, txtyear, txtemail;
    Button btnPay,btn_pagar;
    String kind_card, result;
    Modelo modelo = Modelo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crear_tarjeta);


        validation = new Validation();

        progress = new ProgressDialog(this);
        progress.setMessage("Validando informacion de la tarjeta");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        txtcardnumber = (TextView) findViewById(R.id.txt_cardnumber);
        txtcvv = (TextView) findViewById(R.id.txt_cvv);
        txtmonth = (TextView) findViewById(R.id.txt_month);
        txtyear = (TextView) findViewById(R.id.txt_year);
        txtemail = (TextView) findViewById(R.id.txt_email);

        btnPay = (Button) findViewById(R.id.btn_pay);
        btn_pagar = (Button) findViewById(R.id.btn_pagar);


        txtcardnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = txtcardnumber.getText().toString();
                if(s.length() == 0) {
                    txtcardnumber.setBackgroundResource(R.drawable.border_error);
                }

                if(validation.luhn(text)) {
                    txtcardnumber.setBackgroundResource(R.drawable.border_sucess);
                } else {
                    txtcardnumber.setBackgroundResource(R.drawable.border_error);
                }


            }
        });

        txtyear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = txtyear.getText().toString();
                if(validation.year(text)){
                    txtyear.setBackgroundResource(R.drawable.border_error);
                } else {
                    txtyear.setBackgroundResource(R.drawable.border_sucess);
                }
            }
        });

        txtmonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = txtmonth.getText().toString();
                if(validation.month(text)){
                    txtmonth.setBackgroundResource(R.drawable.border_error);
                } else {
                    txtmonth.setBackgroundResource(R.drawable.border_sucess);
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                progress.show();

                Card card = new Card(txtcardnumber.getText().toString(), txtcvv.getText().toString(), 9, 2020, txtemail.getText().toString());

                Token token = new Token(modelo.apikeyculqi);

                token.createToken(getApplicationContext(), card, new TokenCallback() {
                    @Override
                    public void onSuccess(JSONObject token) {
                        try {
                            result =token.get("id").toString();
                            modelo.tokenculqi = token.get("id").toString();



                            alerta("Tarjeta","Tarjeta creada exitosamente");

                        } catch (Exception ex){
                            progress.hide();
                        }
                        progress.hide();
                    }

                    @Override
                    public void onError(Exception error) {
                        progress.hide();
                    }
                });

            }
        });

        btn_pagar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                progress.show();

                ClassCargo car = new ClassCargo("10000", "PEN",  txtemail.getText().toString(), modelo.tokenculqi);

                Cargo cargo = new Cargo(modelo.apikeyculqi);

                cargo.createCargo(getApplicationContext(), car, new CargoCallback() {
                    @Override
                    public void onSuccess(JSONObject cargo) {
                        try {
                           intent();

                        } catch (Exception ex){
                            progress.hide();
                        }
                        progress.hide();
                    }

                    @Override
                    public void onError(Exception error) {
                        progress.hide();
                    }
                });

            }
        });

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
                        //intent();

                    }
                })

                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void intent(){
        Intent i = new Intent(getApplicationContext(), MenuLateral.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);
        finish();
    }
}