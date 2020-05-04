package com.enfermeraya.enfermeraya.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.annotations.Nullable;

import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends Activity implements ComandoValidarUsuario.OnValidarUsuarioChangeListener {

    CallbackManager callbackManager;
    LoginButton loginButton;
    AccessToken accessToken;



    //google
    private SignInButton signInButton;
    private GoogleSignInClient googleSignInClient;
    private int RESULT_CODE_SINGIN=999;

    //auth
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText email, password;
    Modelo modelo = Modelo.getInstance();



    Utility utility;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        signInButton = (SignInButton) findViewById(R.id.login_gmail);
        //facebook

        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email"));

        // Callback registration facebook
       // loginButton
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancelar Login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error Login", Toast.LENGTH_SHORT).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goMainScreen();
                }
            }
        };


        //fin facebook

        //gmail


        //initialization
        signInButton = findViewById(R.id.login_gmail);
        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this,gso);

        //Attach a onClickListener
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInM();
            }
        });


        //fin gmail


        //auth

        FirebaseApp.initializeApp(this);


        if (savedInstanceState != null) {
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(i);
            finish();
            return;
        }


        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        //instanciamos la clase utility
        utility =  new Utility();

        preferences();

    }

    //facebook

    private void handleFacebookAccessToken(AccessToken accessToken) {
        loginButton.setVisibility(View.GONE);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error al registarse", Toast.LENGTH_LONG).show();
                }

                loginButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }





    // fin facebook




    //google


    //when the signIn Button is clicked then start the signIn Intent
    private void signInM() {
        Intent singInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent,RESULT_CODE_SINGIN);
    }

    // onActivityResult (Here we handle the result of the Activity )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_SINGIN) {        //just to verify the code
            //create a Task object and use GoogleSignInAccount from Intent and write a separate method to handle singIn Result.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        //facebook

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        //we use try catch block because of Exception.
        try {
            // signInButton.setVisibility(View.INVISIBLE);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Signed In successfully",Toast.LENGTH_LONG).show();
            //SignIn successful now show authentication
            FirebaseGoogleAuth(account);

        } catch (ApiException e) {
            String error = e.getMessage();
            e.printStackTrace();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(MainActivity.this,"SignIn Failed!!!",Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {


        if (account != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            //here we are checking the Authentication Credential and checking the task is successful or not and display the message
            //based on that.
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(MainActivity.this,"successful",Toast.LENGTH_LONG).show();
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        UpdateUI(firebaseUser);
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Failed!",Toast.LENGTH_LONG).show();
                        UpdateUI(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "acc failed", Toast.LENGTH_SHORT).show();
        }

    }

    //Inside UpdateUI we can get the user information and display it when required
    private void UpdateUI(FirebaseUser fUser) {


        //getLastSignedInAccount returned the account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account !=null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personEmail = account.getEmail();
            String personId = account.getId();

            Toast.makeText(MainActivity.this,personName + "  " + personEmail,Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(i);
            finish();
        }
    }
    //fin google


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

                                } catch (Exception e){}
                                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
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
}
