package com.devst.prototipo2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Intent explícito con RESULTADO + uso de Thread.
 *
 * Recibe un nombre, pregunta si se confirma y devuelve la respuesta
 * a MainActivity usando setResult().
 *
 * Al aceptar, un Thread en segundo plano simula un proceso que demora
 * 2 segundos (ej: guardar en un servidor) sin congelar la pantalla.
 */
public class ConfirmActivity extends AppCompatActivity {

    TextView txtPregunta;
    Button btnAceptar, btnCancelar;
    ProgressBar progreso;

    // Tiempo que dura el proceso simulado (en milisegundos)
    final int TIEMPO_PROCESO = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm);

        txtPregunta = findViewById(R.id.txtPregunta);
        btnAceptar = findViewById(R.id.btnAceptar);
        btnCancelar = findViewById(R.id.btnCancelar);
        progreso = findViewById(R.id.progreso);


        // Leemos el nombre enviado desde MainActivity
        String nombre = getIntent().getStringExtra(Claves.EXTRA_NOMBRE);

        // VALIDACIÓN: si viene vacío, usamos un texto por defecto
        if (nombre == null || nombre.isEmpty()) {
            nombre = "usuario";
        }

        final String nombreFinal = nombre;

        txtPregunta.setText("¿Confirmas el registro de " + nombreFinal + "?");


        // ACEPTAR -> ejecuta el proceso en un Thread y luego devuelve RESULT_OK
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procesarConfirmacion(nombreFinal);
            }
        });

        // CANCELAR -> devuelve RESULT_CANCELED
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }


    // =============================================
    // PROCESO EN SEGUNDO PLANO (THREAD)
    // =============================================

    private void procesarConfirmacion(final String nombre) {

        // Evitamos que el usuario presione los botones otra vez
        btnAceptar.setEnabled(false);
        btnCancelar.setEnabled(false);

        // Mostramos "Procesando..." y la rueda de carga
        txtPregunta.setText("Procesando...");
        progreso.setVisibility(View.VISIBLE);

        /*
         * THREAD (hilo secundario)
         *
         * Las tareas lentas NO deben correr en el hilo principal,
         * porque la pantalla se congelaría. Por eso creamos un Thread
         * que hace la espera por separado.
         */
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    // Simulamos un proceso que demora 2 segundos
                    Thread.sleep(TIEMPO_PROCESO);

                } catch (InterruptedException e) {

                    // Si el hilo se interrumpe, salimos sin devolver nada
                    Thread.currentThread().interrupt();
                    return;
                }

                /*
                 * Solo el hilo principal puede modificar la pantalla.
                 * runOnUiThread() vuelve al hilo principal para
                 * terminar la tarea.
                 */
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // VALIDACIÓN: si la pantalla ya se cerró, no hacemos nada
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        // Preparamos la respuesta para MainActivity
                        Intent resultado = new Intent();
                        resultado.putExtra(
                                Claves.EXTRA_MENSAJE,
                                nombre + " fue confirmado ✅"
                        );

                        setResult(RESULT_OK, resultado);
                        finish();
                    }
                });
            }
        }).start();
    }
}
