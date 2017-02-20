package vrteam.birthday;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import vrteam.birthday.basedatos.DatabaseHandler;
import vrteam.birthday.modelos.Persona;
import vrteam.birthday.utilitarios.ImagenAdapter;

public class MainActivity extends AppCompatActivity {
    // Storage Permissions variables

    private static final String TAG = "MainActivity";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Objetos.
    private DatabaseHandler baseDatos;
    private ImagenAdapter cursorAdapter;
    private ListView listViewPersonas;
    FloatingActionMenu btn_flo_menu;
    FloatingActionButton btn_flo_1, btn_flo_2, btn_flo_3;


    // Constantes privadas.
    private int CODIGO_RESULT_EDITAR_PERSONA = 0;
    private static final String SAMPLE_DB_NAME = "BDBIRTHDAY_INFO";
    private static final String SAMPLE_TABLE_NAME = "Personas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyStoragePermissions(this);

        setContentView(R.layout.activity_main);

        // Hace referencia a la parte xml (activity_main5555.xml).
        listViewPersonas = (ListView) findViewById(R.id.listViewPersonas);
        btn_flo_menu = (FloatingActionMenu) findViewById(R.id.icon_btn_flo_menu);
        btn_flo_1 = (FloatingActionButton) findViewById(R.id.icon_btn_flo_1);
        btn_flo_2 = (FloatingActionButton) findViewById(R.id.icon_btn_flo_2);
        btn_flo_3 = (FloatingActionButton) findViewById(R.id.icon_btn_flo_3);

/**
 * Al hacer click en el boton agregar Persona se abre una ventana para la edicion de una
 * nueva persona..
 */

        btn_flo_1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Se crea una nueva persona.
                editarPersona(0);
                btn_flo_menu.close(true);
            }
        });


        btn_flo_2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.icon_btn_flo_2:
                        exportDB();
                        break;
                }
            }
            private void exportDB(){

                Log.i(TAG, "inicia exportDB()");
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                FileChannel source=null;
                FileChannel destination=null;
                String currentDBPath = "/data/"+ "vrteam.birthday" +"/databases/"+SAMPLE_DB_NAME;
                File f = new File(Environment.getExternalStorageDirectory() + "/BirthdayBackup");
                if(!f.isDirectory()) {
                    Log.i(TAG, "NO es directorio, se procede a crear: " + f.getAbsolutePath());
                    String newFolder = "/BirthdayBackup";
                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    File myNewFolder = new File(extStorageDirectory + newFolder);
                    myNewFolder.mkdir(); //creamos la carpeta
                }else{
                    Log.i(TAG, "es directorio: " + f.getAbsolutePath());
                }
                String backupDBPath = "BirthdayBackup/"+SAMPLE_DB_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                try {
                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                    Toast.makeText(MainActivity.this, "¡Copia de Seguridad creada correctamente!", Toast.LENGTH_LONG).show();
                } catch(IOException e) {
                    Toast.makeText(getApplicationContext(), "Error al crear Copia de Seguridad", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                btn_flo_menu.close(true);

                Log.i(TAG, "termina exportDB()...");
            }
        });
        btn_flo_3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.icon_btn_flo_3:
                        ImportDB();
                        break;
                }
            }
            private void ImportDB(){
                Log.e(TAG, "inicia importDB()");
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                FileChannel source=null;
                FileChannel destination=null;
                String currentDBPath = "/data/"+ "vrteam.birthday" +"/databases/"+SAMPLE_DB_NAME;
                String backupDBPath = "BirthdayBackup/"+SAMPLE_DB_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                try {
                    source = new FileInputStream(backupDB).getChannel();
                    destination = new FileOutputStream(currentDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                    Toast.makeText(MainActivity.this, "Se restauró correctamente", Toast.LENGTH_LONG).show();
                } catch(IOException e) {
                    Toast.makeText(getApplicationContext(), "Error al restaurar", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                btn_flo_menu.close(true);
                Log.e(TAG, "Termina importDB() procede a activar alertas.");
                activaAlertas();
            }
        });

        // Se recuperan todas las personas de la base de datos.
        recuperarTodasPersonas();

        // Asociamos los menús contextuales al listViewPersonas.
        registerForContextMenu(listViewPersonas);
    }


    private void activaAlertas(){
        try{
            baseDatos = new DatabaseHandler(this);
            // Devuelve todas las personas en el objeto Cursor.
            Cursor cursor = baseDatos.obtenerTodasPersonas();
            if (cursor.moveToFirst()) {
                // EditarPersonaActivity epa = new EditarPersonaActivity();
                while (!cursor.isAfterLast()) {
                    Log.i(TAG, "Activando alarma " + cursor.getString(0) + " para usuario " + cursor.getString(1));

                    Log.i(TAG, " alarma " + cursor.getString(2) );
                    // setAlarm(Integer.parseInt(cursor.getString(0)));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }catch(Exception e){
            Log.d("Error", "El mensaje de error es: " + e.getMessage());
        }finally{
            baseDatos.cerrar();
        }

    }


    public void setAlarm(int notification_id, String nombre, String fecha) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(sdf.parse(fecha));
        Intent intent = new Intent(getBaseContext(), vrteam.birthday.notif.AlarmReceiver.class);
        //los extras
        intent.putExtra("titulo", nombre);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(),
                notification_id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        Log.i(TAG, "Se configura satisfactoriamente alarma con id " + notification_id);
    }



    /**
     * Metodo publico que se sobreescribe. En este metodo crearmos el menu contextual
     * para el ListView de personas.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        android.view.MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        inflater.inflate(R.menu.opciones_personas, menu);
    }

    /**
     * Metodo publico que se sobreescribe. En este metodo colocamos las acciones de las opciones del menu contextual
     * para el ListView de personas.
     */
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.menu_contextual_editar_persona:
                editarPersona((int)info.id);
                return true;
            case R.id.menu_contextual_eliminar_persona:
                eliminarPersona((int)info.id);
                recuperarTodasPersonas();
                return true;
            default:
                return super.onContextItemSelected((android.view.MenuItem) item);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();;
    }

    /**
     * Metodo privado que recupera todos las personas existentes de la base de datos.
     */
    private void recuperarTodasPersonas() {
        try{
            baseDatos = new DatabaseHandler(this);

            // Devuelve todas las personas en el objeto Cursor.
            Cursor cursor = baseDatos.obtenerTodasPersonas();

            String[] from = new String[]{
                    "nombre",
                    "fecha",
                    "zodiaco",
                    "ruta_imagen"
            };

            int[] to = new int[]{
                    R.id.persona_nombre,
                    R.id.persona_fecha,
                    R.id.persona_zodiaco,
                    R.id.foto_gallery,
            };
            cursorAdapter = new ImagenAdapter(this, cursor, from, to);
            listViewPersonas.setAdapter(cursorAdapter);
        }catch(Exception e){
            Log.d("Error", "El mensaje de error es: " + e.getMessage());
        }finally{
            // Se cierra la base de datos.
            baseDatos.cerrar();
        }
    }

    /**
     * Metodo publico que edita una persona.
     * @param p_id
     */
    public void editarPersona(int p_id){
        // Si el p_id es 0, entonces se crea una nueva persona.
        if(p_id == 0){
            // Se dirige a la actividad EditarPersonaActivity.
            Intent actividad_editarPersona = new Intent(MainActivity.this, EditarPersonaActivity.class);
            startActivityForResult(actividad_editarPersona, CODIGO_RESULT_EDITAR_PERSONA);
        }else{
            // Recupera una persona especifica.
            Persona persona;

            try{
                persona = baseDatos.getPersona(p_id);

                // Se dirige a la actividad EditarPersonaActivity.
                Intent actividad_editarPersona = new Intent(this, EditarPersonaActivity.class);

                // Se le coloca parametros para enviar a la actividad EditarPersonaActivity.
                actividad_editarPersona.putExtra("id", p_id);
                actividad_editarPersona.putExtra("nombre", persona.getNombre());
                actividad_editarPersona.putExtra("fecha", persona.getFecha());
                actividad_editarPersona.putExtra("zodiaco", persona.getZodiaco());
                actividad_editarPersona.putExtra("ruta_imagen", persona.getRutaImagen());

                startActivityForResult(actividad_editarPersona, CODIGO_RESULT_EDITAR_PERSONA);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error al editar", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }finally{
                baseDatos.cerrar();
            }
        }
    }

    /**
     * Metodo privado que elimina una persona.
     * @param id_persona
     */
    private void eliminarPersona(int id_persona){
        // Objetos.
        AlertDialog.Builder mensaje_dialogo = new AlertDialog.Builder(this);

        // Variables.
        final int v_id_persona = id_persona;

        mensaje_dialogo.setTitle("Importante");
        mensaje_dialogo.setMessage("¿Está seguro de eliminar esta persona?");
        mensaje_dialogo.setCancelable(false);
        mensaje_dialogo.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                try{
                    baseDatos.eliminaPersona(v_id_persona);

                    recuperarTodasPersonas();
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Error al eliminar!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }finally{
                    baseDatos.cerrar();
                }
            }
        });
        mensaje_dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                recuperarTodasPersonas();
            }
        });
        mensaje_dialogo.show();
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * El metodo protegido se sobreescribe. Se llama con el resultado de otra actividad
     * requestCode es el codigo original que se manda a la actividad
     * resultCode es el codigo de retorno, 0 significa que todo salió bien
     * intent es usado para obtener alguna información del caller.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        recuperarTodasPersonas();
    }
} // Fin de la actividad MainActivity_noti.