package com.enfermeraya.enfermeraya.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.Splash;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Usuario;
import com.enfermeraya.enfermeraya.comandos.ComandoPerfil;
import com.enfermeraya.enfermeraya.comandos.ComandoValidarCorreoFirebase;
import com.enfermeraya.enfermeraya.models.utility.Utility;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;
import static android.Manifest.permission_group.STORAGE;


import com.enfermeraya.enfermeraya.R;

public class Perfil extends Activity implements ComandoPerfil.OnPerfilChangeListener {

    Usuario usuario;

    EditText txt_nombre, txt_apellido, txt_celular, txt_correo;
    Modelo modelo = Modelo.getInstance();
    String token = "";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ComandoPerfil comandoPerfil;

    //foto
    String userChoosenTask="";
    boolean img_cam1 = false;
    private int REQUEST_CAMERA = 0;
    int SELECT_FILE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;
    ImageView camara1;
    TextView txt_camara1;

    private ProgressDialog progressDialog;
    private static final String TAG ="AndroidBash";
    String foto ="";
    Date date;
    DateFormat hourFormat;
    int setHora = 0;


    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";
    private final int MY_PERMISSIONS = 123;
    private final int PHOTO_CODE = 3;
    private final int SELECT_PICTURE = 300;
    private String mPath = "";
    private LinearLayout mRlView;
    Uri photoURI = null;
    String ruta1 = null;
    static final int REQUEST_TAKE_PHOTO = 3;
    String mCurrentPhotoPath = "";
    Utility utility;
    SweetAlertDialog pDialog;
    LinearLayout layut1,layut2;
    Button btnupdate;
    Space space1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_perfil);


        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), Splash.class);
            startActivity(i);
            finish();
            return;
        }

        //instanciamos la clase utility
        utility = new Utility();

        usuario =  null;
        boolean result = utility.checkPermission(Perfil.this);

        txt_nombre = (EditText)findViewById(R.id.txt_nombre);
        txt_apellido = (EditText)findViewById(R.id.txt_apellido);
        txt_celular = (EditText)findViewById(R.id.txt_celular);
        txt_correo = (EditText)findViewById(R.id.txt_correo);

        camara1 = (ImageView)findViewById(R.id.camara1);
        txt_camara1 = (TextView) findViewById(R.id.txt_camara1);
        mRlView = (LinearLayout) findViewById(R.id.mRlView);
        layut1 = (LinearLayout) findViewById(R.id.layut1);
        layut2 = (LinearLayout) findViewById(R.id.layut2);
        btnupdate =(Button) findViewById(R.id.btnupdate);
        space1 =(Space) findViewById(R.id.space1);


        comandoPerfil =  new ComandoPerfil(this);

            if (utility.estado(getApplicationContext())) {


                Log.v("User" ,  ""+user.getDisplayName());

                if(modelo.tipoLogin.equals("normal")){
                    loadswet("Cargando la información...");
                    comandoPerfil.getUsuario();
                }else{
                    layut1.setVisibility(View.GONE);
                    btnupdate.setVisibility(View.GONE);
                    layut2.setVisibility(View.GONE);
                    space1.setVisibility(View.GONE);
                    txt_nombre.setFocusable(false);
                    txt_correo.setFocusable(false);
                    txt_nombre.setText(user.getDisplayName());
                    txt_correo.setText(user.getEmail());
                    camara1.setClickable(false);
                    txt_camara1.setClickable(false);

                    //Bitmap bitmap = BitmapFactory.decodeFile(user.getPhotoUrl());

                    try {
                       // Uri url = user.getPhotoUrl();

                        /*Glide.with(getApplicationContext())
                                .load(user.getPhotoUrl())
                                .centerCrop()
                                .into(camara1);*/

                        Glide.with(getApplicationContext())
                                .load(user.getPhotoUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(camara1);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //camara1.setImageBitmap(getCircularBitmap(bitmap));

                    //camara1.setBackgroundResource();
                }

            }else {
                alerta("Sin Internet","Valide la conexión a internet");
            }

        }


    //posgres dialos sweetalert

    public void loadswet(String text){

        try {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText(text);
            pDialog.setCancelable(false);
            pDialog.show();

        }catch (Exception e){

        }

    }


    //oculatomos el dialog
    private void hideDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }



    public void validar(View v){

        if(txt_nombre.getText().toString().length() < 3){
            txt_nombre.setError("Información muy corta");
        }
        else if(txt_apellido.getText().toString().length() < 3){
            txt_apellido.setError("Información muy corta");
        }

        else if(txt_celular.getText().toString().length() < 10){
            txt_celular.setError("Información muy corta");
        }

        else if(txt_correo.getText().toString().length() < 5){
            txt_correo.setError("Email muy corta");
        }

        else if (!validarEmail(txt_correo.getText().toString())) {
            txt_correo.setError("Email no válido");
        }
        else{

            usuario = new Usuario();
            usuario.setNombre(txt_nombre.getText().toString());
            usuario.setApellido(txt_nombre.getText().toString());
            usuario.setCelular(txt_celular.getText().toString());
            usuario.setFoto(foto);



            if (utility.estado(getApplicationContext())) {
                loadswet("Validando la información...");
                comandoPerfil.actualizarUsuario(usuario);
            }else{
                alerta("Sin Internet","Valide la conexión a internet");
            }

        }


    }


    //validar email
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    ///imagen
    public void camara1(View v){
        if(Utility.checkPermission(getApplicationContext())){
            camara1.setEnabled(true);
            showOptions();
        }

        else{
            camara1.setEnabled(false);

        }

    }

    //manejo camara y galeria
    private boolean mayRequestStoragePermission() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(mRlView, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galería", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(Perfil.this);
        builder.setTitle("Seleccione una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean result = Utility.checkPermission(getApplicationContext());
                if(option[which] == "Tomar foto"){

                    if (result) {
                        openCamera();
                    }
                }else if(option[which] == "Elegir de galería"){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void openCamera() {

        dispatchTakePictureIntent();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                ruta1 = photoFile.getAbsolutePath();


                photoURI = FileProvider.getUriForFile(this,
                        //"com.tactoapps.android.fileprovider",
                        //otener ruta fileprovidder   getApplicationContext().getPackageName()
                        getString(R.string.authorities),
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix //
                ".jpg",         // suffix //
                storageDir      // directory //
        );

        // Save a file: path for use with ACTION_VIEW intents
        mPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });


                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    camara1.setImageBitmap(getCircularBitmap(bitmap));

                    txt_camara1.setText("Cambiar Foto");
                    foto = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);

                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();

                    try {
                        Bitmap imagen = getBitmapFromUri (path);
                        camara1.setImageBitmap(getCircularBitmap(imagen));
                        foto = encodeToBase64(imagen, Bitmap.CompressFormat.JPEG, 100);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // camara1.setImageURI(path);
                    txt_camara1.setText("Cambiar Foto");

                    break;

            }
        }
        else {
            Log.v("Code",""+requestCode);
        }
    }


    //convertir uri en bitmap
    private Bitmap getBitmapFromUri ( Uri uri ) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver (). openFileDescriptor ( uri , "r" );
        FileDescriptor fileDescriptor = parcelFileDescriptor . getFileDescriptor ();
        Bitmap image = BitmapFactory . decodeFileDescriptor ( fileDescriptor );
        parcelFileDescriptor . close ();
        return image ;
    }

    //convertir bitmap a base64
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permisos aceptados", Toast.LENGTH_SHORT).show();
                camara1.setEnabled(true);

            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Perfil.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }
    //fin manejo de galeria y camara




    //imagen circular
    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
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





    @Override
    public void cargoUSuario() {

        hideDialog();
        txt_nombre.setText("" + modelo.usuario.getNombre());
        txt_apellido.setText("" + modelo.usuario.getApellido());
        txt_celular.setText("" + modelo.usuario.getCelular());
        txt_correo.setText("" + modelo.usuario.getCorreo());
        if (!modelo.usuario.getFoto().equals("")) {
            camara1.setImageBitmap(getCircularBitmap(decodeBase64(modelo.usuario.getFoto())));
        }


    }

    @Override
    public void setUsuarioListener() {
        hideDialog();

        alerta("Actualización","Datos Actualizados");
    }

    @Override
    public void errorSetUsuario() {
        hideDialog();

        alerta("Eror","Error con el regitro");
    }


}
