package vrteam.birthday;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import vrteam.birthday.basedatos.DatabaseHandler;
import vrteam.birthday.modelos.Persona;
import vrteam.birthday.utilitarios.Mensaje;



public class EditarPersonaActivity extends Activity {
    // Objetos.
    private Button butonLimpiar;
    private Button butonGuardar;
    private EditText editTextNombre;
    private EditText editTextFecha;
    private EditText editTextZodiaco;
    private DatabaseHandler baseDatos;
    private Bundle extras;
    private ImageView imagenPersona;
    private Mensaje mensaje;
    private int dia;
    private int mes;
    private int año;
    private static final int TIPO_DIALOGO = 0;
    private static DatePickerDialog.OnDateSetListener SelectorFecha;
    Uri uriAlarm;

    // Variables.
    private String ruta_imagen; // La ruta de la imagen que el usuario eligio
    // para la imagen de su persona.
    private int SELECCIONAR_IMAGEN = 237487;
    private int notification_id;

    // Constantes privadas.
    private static final int FECHA_DIALOGO_ID = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_persona);

        // Hace referencia a los objetos xml.
        butonGuardar = (Button) findViewById(R.id.botonGuardar);
        butonLimpiar = (Button) findViewById(R.id.botonLimpiar);
        editTextNombre = (EditText) findViewById(R.id.editTextNombre);
        editTextFecha = (EditText) findViewById(R.id.editTextFecha);
        editTextZodiaco = (EditText) findViewById(R.id.editTextZodiaco);
        imagenPersona = (ImageView) findViewById(R.id.imagenPersona);
        uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Calendar calendario = Calendar.getInstance();
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        año = calendario.get(Calendar.YEAR);
        mostrarFecha();
        SelectorFecha = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dia = dayOfMonth;
                mes = month;
                año = year;
                mostrarFecha();
            }
        };

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                return new DatePickerDialog(this, SelectorFecha, dia, mes, año);
        }
        return null;
    }

    public void mostrarCalendario(View control) {
        showDialog(TIPO_DIALOGO);
    }


    public void mostrarFecha() {
        editTextFecha.setText(dia + "/" + (mes + 1) + "/" + año);

        // Se crea el objeto mensaje.
        mensaje = new Mensaje(getApplicationContext());

        /**
         * Al hacer click en el boton imagen se abre una ventana.
         */
        imagenPersona.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ventanaImagen();
            }
        });
        // Recupera en un Objeto Bundle si tiene valores que fueron pasados como
        // parametro de una actividad.
        extras = getIntent().getExtras();


        if (estadoEditarPersona()) {
            editTextNombre.setText(extras.getString("nombre"));
            editTextFecha.setText(extras.getString("fecha"));
            editTextZodiaco.setText(extras.getString("zodiaco"));
            ruta_imagen = extras.getString("ruta_imagen");
            imagenPersona.setImageBitmap(crearThumb());
        }

        // Agrega nuevo registro de una persona.
        butonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (verificarCampoNombre() && verificarCampoFecha()
                        && verificarCampoZodiaco()) {
                    if (estadoEditarPersona()) {
                        editarPersona();
                    } else {
                        try {
                            notification_id = (int)insertarNuevoPersona();
                            setAlarm(uriAlarm, notification_id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    // Finaliza la actividad EditarPersonaActivity.
                    finish();
                } else {
                    if (editTextNombre.getText().toString().equals("")) {
                        mensaje.mostrarMensajeCorto("Introduzca un Nombre");
                    }
                    if (editTextFecha.getText().toString().equals("")) {
                        mensaje.mostrarMensajeCorto(getResources().getString(R.string.intro_fecha));
                    }
                    if (editTextZodiaco.getText().toString().equals("")) {
                        mensaje.mostrarMensajeCorto("Introduzca su Zodiaco");
                    }
                }
            }
        });


        // Limpia los campos.
        butonLimpiar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                limpiarCampos();
            }
        });
    }

    /**
     * Metodo privado que limpia los campos.
     */
    private void limpiarCampos() {
        editTextNombre.setText("");
        editTextFecha.setText("");
        editTextZodiaco.setText("");
    }

    /**
     * Metodo privado que verifica que se cambio el valor de Nombre o no está en
     * blanco (vacio).
     */
    private boolean verificarCampoNombre() {
        if (editTextNombre.getText().toString().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * Metodo privado que verifica que se cambio el valor de Fecha o no
     * está en blanco (vacio).
     */
    private boolean verificarCampoFecha() {
        if (editTextFecha.getText().toString().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * Metodo privado que verifica que se cambio el valor de la Zodiaco o no está
     * en blanco (vacio).
     */
    private boolean verificarCampoZodiaco() {
        if (editTextZodiaco.getText().toString().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * Metodo privado que insertar una nueva Persona.
     */
    private long insertarNuevoPersona() {
        long personaId = -1;
        baseDatos = new DatabaseHandler(EditarPersonaActivity.this);

        try {
            // Crear objeto Persona.
            Persona persona = new Persona(editTextNombre.getText().toString(),
                    editTextFecha.getText().toString(), editTextZodiaco
                    .getText().toString(), ruta_imagen);

            // Se inserta una nueva persona.
            personaId = baseDatos.insertarPersona(persona);
        } catch (Exception e) {
            mensaje.mostrarMensajeCorto("Error, por favor empieza de nuevo");
            e.printStackTrace();
        } finally {
            // Se cierra la base de datos.
            baseDatos.cerrar();
        }
        return personaId;
    }

    /**
     * Metodo privado que edita una persona existente.
     */
    private void editarPersona() {
        baseDatos = new DatabaseHandler(EditarPersonaActivity.this);

        try {
            // Crear objeto persona.
            int id = extras.getInt("id");

            Persona persona = new Persona(id, editTextNombre.getText()
                    .toString(), editTextFecha.getText().toString(),
                    editTextZodiaco.getText().toString(), ruta_imagen);

            baseDatos.actualizarRegistros(id, persona.getNombre(),
                    persona.getFecha(), persona.getZodiaco(),
                    persona.getRutaImagen());
            setAlarm(uriAlarm, id);
            mensaje.mostrarMensajeCorto("Se edito correctamente");
        } catch (Exception e) {
            mensaje.mostrarMensajeCorto("Error al querer editarlo, por favor intentelo de nuevo");
            e.printStackTrace();
        } finally {
            baseDatos.cerrar();
        }
    }

    /**
     *
     */
    public boolean estadoEditarPersona() {
        // Si extras es diferente a null es porque tiene valores. En este caso
        // es porque se quiere editar una persona.
        if (extras != null) {
            return true;
        } else {
            return false;
        }
    }


    private void setAlarm(Uri passuri, int notification_id) throws ParseException {

        System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(sdf.parse(editTextFecha.getText().toString()));
        Intent intent = new Intent(getBaseContext(), vrteam.birthday.notif.AlarmReceiver.class);
        //los extras
        intent.putExtra("titulo", editTextNombre.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(),
                notification_id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

    }











    /**
     * Metodo privado que abre la ventana.
     */
    private void ventanaImagen() {
        try {
            final CharSequence[] items = {"Seleccionar de la galería"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Seleccionar una foto");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            Intent intentSeleccionarImagen = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            intentSeleccionarImagen.setType("image/*");
                            startActivityForResult(intentSeleccionarImagen, SELECCIONAR_IMAGEN);
                            break;
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            mensaje.mostrarMensajeCorto("El error es: " + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == SELECCIONAR_IMAGEN) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    ruta_imagen = obtieneRuta(selectedImage);
                    imagenPersona.setImageBitmap(crearThumb());
                }
            }
        } catch (Exception e) {
        }

    }

    private Bitmap getBitmap(String ruta_imagen) {
        // Objetos.
        File imagenArchivo = new File(ruta_imagen);
        Bitmap bitmap = null;

        if (imagenArchivo.exists()) {
            bitmap = BitmapFactory.decodeFile(imagenArchivo.getAbsolutePath());
        }
        return bitmap;
    }

    /**
     * Metodo privado
     *
     * @param uri
     * @return
     */
    private String obtieneRuta(Uri uri) {
        String[] projection = {android.provider.MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // Cerrar EditarPersonaActivity.
                EditarPersonaActivity.this.finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        // Coloco todos los objetos en null.
        imagenPersona = null;

        // LLama al recolector de basura.
        System.gc();
    }

    /**
     * Metodo privado que crea un Bitmap (thumb).
     */
    private Bitmap crearThumb(){
        Bitmap bitmap = getBitmap(ruta_imagen);
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(ruta_imagen, opciones);

        int scaleW = opciones.outWidth / 854 + 1;
        int scaleH = opciones.outHeight / 480 + 1;
        int scale = Math.max(scaleW, scaleH);

        opciones.inJustDecodeBounds = false;
        opciones.inSampleSize = scale;
        opciones.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(ruta_imagen, opciones);
        return bitmap;
    }
        }
