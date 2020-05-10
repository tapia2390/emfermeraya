package com.enfermeraya.enfermeraya.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Menu extends Activity {


    private static final String TAG ="EmailPassword";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user = mAuth.getCurrentUser();
    private GoogleSignInClient mGoogleSignInClient;


    Modelo modelo = Modelo.getInstance();
    String tipoLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);


        // gmail
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Toast.makeText(getApplicationContext(), modelo.tipoLogin, Toast.LENGTH_SHORT).show();

        getPreference();
        modelo.tipoLogin = tipoLogin;
    }




    public void cerrarSesion(View v){

        if(modelo.tipoLogin.equals("normal")){
            cerrarNormal();

        }else if(modelo.tipoLogin.equals("facebook")){

            cerrarFacebook();

        }
        else if(modelo.tipoLogin.equals("gmail")){
            cerrarGmail();
        }

    }



    public void cerrarGmail(){
        mGoogleSignInClient.signOut();
        Logout();
    }


    public void cerrarNormal(){
        Logout();
    }


    public void cerrarFacebook(){
        Toast.makeText(getApplication(),  "Cerrarndo sessión", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Logout();
    }

    public void Logout(){
      try {
          modelo.tipoLogin = "";
          user =  null;
          modelo.uid = "";
          mAuth.signOut();
          setPreference("");
          mAuth.getInstance().signOut();
          Toast.makeText(getApplication(),  "Cerrarndo sessión", Toast.LENGTH_SHORT).show();
          Intent i = new Intent(getApplicationContext(), MainActivity.class);
          startActivity(i);
          overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);
          finish();
      }
      catch (Exception e){}
    }


    public void getPreference(){
        SharedPreferences prefs = getSharedPreferences("tipoLogin", MODE_PRIVATE);
        tipoLogin = prefs.getString("tipoLogin", "");//"No name defined" is the default value.
    }

    public void setPreference(String tipoLogin){
        SharedPreferences.Editor editor = getSharedPreferences("tipoLogin", MODE_PRIVATE).edit();
        editor.putString("tipoLogin", tipoLogin);
        editor.apply();
    }



    public void didTapTerminos(View v){
        Intent i = new Intent(getApplicationContext(), Teminos.class);
        startActivity(i);
        overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);

    }

    public void didTapPerfil(View v){
        Intent i = new Intent(getApplicationContext(), Perfil.class);
        startActivity(i);
        overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);

    }

}
