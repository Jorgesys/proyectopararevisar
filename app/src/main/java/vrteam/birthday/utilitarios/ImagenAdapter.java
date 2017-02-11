package vrteam.birthday.utilitarios;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import vrteam.birthday.R;

public class ImagenAdapter extends SimpleCursorAdapter {
    // Objetos de clase.
    private Cursor cursor;
    private Context contexto;
    private LayoutInflater mInflater;

    static class ViewHolder{
        TextView textViewNombre;
        TextView textViewFecha;
        TextView textViewZodiaco;
        ImageView thumb_persona;
    }

    /**
     * Constructor con 4 parametros.
     * @param contexto
     * @param cursor
     * @param from
     * @param to
     */
    public ImagenAdapter(Context contexto, Cursor cursor, String[] from,
                         int[] to) {
        super(contexto, R.layout.fila_persona, cursor, from, to);
        this.contexto = contexto;
        this.cursor = cursor;
        this.mInflater = LayoutInflater.from(contexto);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = this.mInflater.inflate(R.layout.fila_persona, null);
            viewHolder = new ViewHolder();

            viewHolder.textViewNombre = (TextView)convertView.findViewById(R.id.persona_nombre);
            viewHolder.textViewFecha = (TextView)convertView.findViewById(R.id.persona_fecha);
            viewHolder.textViewZodiaco = (TextView)convertView.findViewById(R.id.persona_zodiaco);
            viewHolder.thumb_persona = (ImageView)convertView.findViewById(R.id.foto_gallery);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        this.cursor.moveToPosition(position);

        viewHolder.textViewNombre.setText(this.cursor.getString(this.cursor.getColumnIndex("nombre")));
        viewHolder.textViewFecha.setText(this.cursor.getString(this.cursor.getColumnIndex("fecha")));
        viewHolder.textViewZodiaco.setText(this.cursor.getString(this.cursor.getColumnIndex("zodiaco")));

        // Se obtiene la ruta de la imagen.
        String ruta_imagen = cursor.getString(cursor.getColumnIndex("ruta_imagen"));

        File imagenArchivo = new  File(ruta_imagen);
        if(imagenArchivo.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imagenArchivo.getAbsolutePath());
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
            viewHolder.thumb_persona.setImageBitmap(bitmap);
        }
        return convertView;
    }
}