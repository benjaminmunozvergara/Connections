package com.devst.prototipo2;

/**
 * Constantes con las claves de los extras de los Intents.
 * Se usan en vez de escribir el texto a mano en cada Activity,
 * así evitamos errores de tipeo y se cambian en un solo lugar.
 */
public final class Claves {

    // Datos enviados a DetalleActivity
    public static final String EXTRA_TITULO = "titulo";
    public static final String EXTRA_DESCRIPCION = "descripcion";
    public static final String EXTRA_PRECIO = "precio";

    // Datos enviados a ConfirmActivity
    public static final String EXTRA_NOMBRE = "nombre";

    // Dato devuelto por ConfirmActivity a MainActivity
    public static final String EXTRA_MENSAJE = "mensaje";

    // Constructor privado: esta clase solo guarda constantes, no se instancia
    private Claves() {
    }
}
