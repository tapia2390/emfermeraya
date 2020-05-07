package com.enfermeraya.enfermeraya.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.comandos.ComandoValidarUsuario;
import com.enfermeraya.enfermeraya.models.utility.Utility;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends Activity implements ComandoValidarUsuario.OnValidarUsuarioChangeListener {


    private FirebaseAuth mAuth;
    String tipoLogin;


    //gmail
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private  String TAG = "MainActivity";
    private int RC_SIGN_IN = 1;

    //facebook
    LoginButton loginButtonFacebook;
    public static CallbackManager callbackManager;
    private int RESULT_CODE_SINGINFACEBOOK=64206;


    //auth
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText email, password;
    Modelo modelo = Modelo.getInstance();


    Utility utility;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        if (savedInstanceState != null) {
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(i);
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        // gogole
        signInButton = (SignInButton) findViewById(R.id.login_gmail);
        //facebook
        loginButtonFacebook = (LoginButton) findViewById(R.id.login_button);


        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        //instanciamos la clase utility
        utility = new Utility();

        getPreference();
        modelo.tipoLogin = tipoLogin;
        preferences();


        //gogle
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //fin google

        //facebook

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        //fin facebook


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
    //google
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        if(requestCode == RESULT_CODE_SINGINFACEBOOK){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(MainActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        modelo.tipoLogin = "gmail";
                        setPreference(modelo.tipoLogin);
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "acc failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(FirebaseUser fUser){
        signInButton.setVisibility(View.VISIBLE);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            Toast.makeText(MainActivity.this,personName + personEmail ,Toast.LENGTH_SHORT).show();
        }

    }

    //fin google

    //facebook

    private void handleFacebookAccessToken(AccessToken accessToken) {
        loginButtonFacebook.setVisibility(View.GONE);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error al registarse", Toast.LENGTH_LONG).show();
                }

                modelo.tipoLogin = "facebook";
                setPreference(modelo.tipoLogin);
                loginButtonFacebook.setVisibility(View.VISIBLE);
            }
        });
    }



    //fin facebook

    //auth
    private void preferences() {


        if (utility.estado(getApplicationContext())) {


            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in

                        modelo.uid = user.getUid();
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        loadswet("Validando la información...");

                        try {
                            hideDialog();
                        } catch (Exception e){}

                        //catgamos el tipo de usuario logeuado g,f,n

                        logueado();
                    } else {
                        try {
                            hideDialog();
                        } catch (Exception e){}
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out" + user + "no logueado");
                        // Toast.makeText(getApplication(),"Error con los datos registrados.", Toast.LENGTH_SHORT).show();

                    }

                }
            };
        }

    }




    @Override
    public void onStart() {
        super.onStart();

        if (mAuth == null || mAuthListener == null) {
            return;
        } else {
            mAuth.addAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public void registrarse(View v){
        Intent i = new Intent(getApplicationContext(), Registrarse.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

    }


    public  void validar(View v){
        validarDatos();
    }

    private void validarDatos() {

        String correo = email.getText().toString();
        String password2 = password.getText().toString();

        if (utility.estado(getApplicationContext())) {
            if(correo.equals("") || password2.equals("")){
                alerta("Campos Obligatorios","todos los campos son requeridos!");
            }else if (!utility.validarEmail(correo)){
                alerta("Correo Electronico","Correo Electrónico no válido");
            }
            else if (password2.length()  < 7){
                alerta("Contraseña Incorrecta","La contraseña no es válida.");
            }
            else{
                //cloadswet("Validando la información...");
                registro(correo,password2);

            }

        }

    }

    private void registro(final String correo, final String password) {

        loadswet("Validando la información...");
        mAuth.signInWithEmailAndPassword(correo,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (task.getException() instanceof FirebaseAuthException) {

                            FirebaseAuthException ex = (FirebaseAuthException) task.getException();

                            if (ex == null) {
                                return;
                            }

                            String error = ex.getErrorCode();

                            if (error.equals("ERROR_INVALID_EMAIL")) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + "correo nada que ver");
                                // Toast.makeText(getApplication(), "...." + "correo nada que ver", Toast.LENGTH_SHORT).show();
                                showAlertDialogLogin();

                                try {
                                    hideDialog();
                                } catch (Exception e){}

                            }
                            if (error.equals("ERROR_USER_NOT_FOUND")) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + "USUARIO NUEVO");
                                // Toast.makeText(getApplication(), "...." + "USUARIO NUEVO", Toast.LENGTH_SHORT).show();
                                showAlertDialogLogin();

                                try {
                                    hideDialog();
                                } catch (Exception e){}

                            }
                            if (error.equals("ERROR_WRONG_PASSWORD")) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + "CONTRASEÑA INCORRECTA");
                                //Toast.makeText(getApplication(), "...." + "CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
                                showAlertDialogLogin();

                                try {
                                    hideDialog();

                                } catch (Exception e){}

                            }
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.getException());
                                //Toast.makeText(getApplication(), "...." + "FALLO EN LA AUTENTICACION", Toast.LENGTH_SHORT).show();
                                showAlertDialogLogin();

                                try {
                                    hideDialog();

                                } catch (Exception e){}

                            } else {
                                try {
                                    hideDialog();

                                } catch (Exception e){
                                     Log.v("Error", e.getLocalizedMessage());
                                }

                                modelo.tipoLogin = "normal";
                                setPreference(modelo.tipoLogin);
                                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                finish();

                            }
                        }

                        if (task.getException() instanceof FirebaseNetworkException) {
                            FirebaseNetworkException ex = (FirebaseNetworkException) task.getException();
                            showAlertDialogRed(getApplication(), "" + ex.getLocalizedMessage());


                            try {
                                hideDialog();
                            } catch (Exception e){}
                        }else{
                            try {
                                hideDialog();
                            } catch (Exception e){}
                        }

                    }
                });



    }



    //alerta swit alert
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

                    }
                })

                .show();
    }

    //posgres dialos sweetalert

    public void loadswet(String text){
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(text);
        pDialog.setCancelable(false);
        pDialog.show();
    }


    //oculatomos el dialog
    private void hideDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }


    public void showAlertDialogLogin() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Por favor verifiqué la información...");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                hideDialog();

            }
        });

        alertDialog.show();
    }

    public void showAlertDialogRed(Context context, String texto) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error de red");
        alertDialog.setMessage("No se pudo loguear. Revise conexión a internet y/o que tenga Google play service activo. " + texto);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }

    @Override
    public void validandoConductorOK() {
        logueado();
    }

    @Override
    public void validandoConductorError() {

        mAuth.signOut();
        Toast.makeText(getApplicationContext(), "Datos Erróneos", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        i.putExtra("vistaPosicion", "dos");
        startActivity(i);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    public void logueado() {
        try {
            hideDialog();
        } catch (Exception e){}

        Intent i = new Intent(MainActivity.this, MapsActivity.class);
        i.putExtra("vistaPosicion", "dos");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);
        finish();
    }

    public void olvidoPassword(View v){
        Intent i = new Intent(getApplicationContext(), OlvidoSuPassword.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

    }
}
