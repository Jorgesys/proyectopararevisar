package vrteam.birthday.modelos;

import java.io.Serializable;

public class Persona implements Serializable{
    // Atributos de clase
    private int id;
    private String nombre;
    private String fecha;
    private String zodiaco;
    private String ruta_imagen;

    /**
     * Constructor por defecto.
     */
    public Persona(){
    }

    /**
     * Constructor que se le pasa 4 parametros.
     * @param p_nombre
     * @param p_fecha
     * @param p_zodiaco
     * @param p_ruta_imagen
     */
    public Persona(String p_nombre, String p_fecha, String p_zodiaco, String p_ruta_imagen){
        setNombre(p_nombre);
        setFecha(p_fecha);
        setZodiaco(p_zodiaco);
        setRutaImagen(p_ruta_imagen);
    }

    /**
     * Constructor que se le pasa 5 parametros.
     * @param p_id
     * @param p_nombre
     * @param p_fecha
     * @param p_zodiaco
     * @param p_ruta_imagen
     */
    public Persona(int p_id, String p_nombre, String p_fecha, String p_zodiaco, String p_ruta_imagen){
        setId(p_id);
        setNombre(p_nombre);
        setFecha(p_fecha);
        setZodiaco(p_zodiaco);
        setRutaImagen(p_ruta_imagen);
    }

	/*
	 * Getters y Setters
	 */
    /**
     *
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     *
     * @return
     */
    public String getFecha() {
        return this.fecha;
    }

    /**
     *
     * @param fecha
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     *
     * @return
     */
    public String getZodiaco() {
        return zodiaco;
    }

    /**
     *
     * @param zodiaco
     */
    public void setZodiaco(String zodiaco) {
        this.zodiaco = zodiaco;
    }

    /**
     *
     * @return
     */
    public int getId(){
        return this.id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getRutaImagen(){
        return this.ruta_imagen;
    }

    /**
     *
     * @param String
     */
    public void setRutaImagen(String ruta_imagen){
        // Si ruta_imagen es null entonces se pone un valor predeterminado.
        if(ruta_imagen == null){
            this.ruta_imagen = "No tiene imagen.";
        }else{
            this.ruta_imagen = ruta_imagen;
        }
    }
} // Fin de la clase Persona.