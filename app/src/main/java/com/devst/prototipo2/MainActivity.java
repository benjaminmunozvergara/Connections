package com.devst.prototipo2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Prototipo 2 - Intents
 *
 * 5 intents IMPLÍCITOS: mapa, web, marcador, SMS, ajustes Wi-Fi
 * 3 intents EXPLÍCITOS: Detalle (extras), Ayuda, Confirmar (resultado)
 */
public class MainActivity extends AppCompatActivity {

    // Campos de texto
    EditText edtLugar, edtUrl, edtTelefono, edtTelefonoSms, edtMensajeSms, edtNombre;

    // Botones implícitos
    Button btnMapa, btnWeb, btnLlamar, btnSms, btnWifi;

    // Botones explícitos
    Button btnDetalle, btnAyuda, btnConfirmar;

    // Texto donde se muestra el resultado de ConfirmActivity
    TextView txtResultado;

    // Lanzador para recibir el resultado de ConfirmActivity
    ActivityResultLauncher<Intent> launcherConfirmar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // =========================================
        // CONECTAR COMPONENTES DEL XML
        // =========================================

        edtLugar = findViewById(R.id.edtLugar);
        edtUrl = findViewById(R.id.edtUrl);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtTelefonoSms = findViewById(R.id.edtTelefonoSms);
        edtMensajeSms = findViewById(R.id.edtMensajeSms);
        edtNombre = findViewById(R.id.edtNombre);

        btnMapa = findViewById(R.id.btnMapa);
        btnWeb = findViewById(R.id.btnWeb);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnSms = findViewById(R.id.btnSms);
        btnWifi = findViewById(R.id.btnWifi);

        btnDetalle = findViewById(R.id.btnDetalle);
        btnAyuda = findViewById(R.id.btnAyuda);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        txtResultado = findViewById(R.id.txtResultado);


        // =========================================
        // RECIBIR RESULTADO DE ConfirmActivity
        // (se debe registrar en onCreate)
        // =========================================

        launcherConfirmar = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {

                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            // Leemos el mensaje que devolvió la otra pantalla
                            String mensaje =
                                    result.getData().getStringExtra(Claves.EXTRA_MENSAJE);

                            txtResultado.setText("Resultado: " + mensaje);

                        } else {

                            txtResultado.setText("Resultado: operación cancelada");

                        }
                    }
                });


        // =========================================
        // INTENTS IMPLÍCITOS
        // =========================================

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirMapa();
            }
        });

        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirWeb();
            }
        });

        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirMarcador();
            }
        });

        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarSms();
            }
        });

        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirAjustesWifi();
            }
        });


        // =========================================
        // INTENTS EXPLÍCITOS
        // =========================================

        btnDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irADetalle();
            }
        });

        btnAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAAyuda();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAConfirmar();
            }
        });

    }


    // =============================================
    // INTENT IMPLÍCITO 1: ABRIR UBICACIÓN EN MAPA
    // =============================================

    private void abrirMapa() {

        String lugar = edtLugar.getText().toString().trim();

        // VALIDACIÓN: el campo no puede estar vacío
        if (lugar.isEmpty()) {
            edtLugar.setError("Ingresa un lugar o dirección");
            return;
        }

        /*
         * geo:0,0?q=texto  -> busca el texto en el mapa
         * Uri.encode() cambia espacios y tildes a un formato válido.
         */
        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(lugar));

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        lanzarIntent(intent, "No hay una app de mapas instalada");
    }


    // =============================================
    // INTENT IMPLÍCITO 2: ABRIR PÁGINA WEB
    // =============================================

    private void abrirWeb() {

        String url = edtUrl.getText().toString().trim();

        // VALIDACIÓN: no vacío
        if (url.isEmpty()) {
            edtUrl.setError("Ingresa una página web");
            return;
        }

        // Si el usuario no escribió http:// o https://, lo agregamos
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        // VALIDACIÓN: formato de URL correcto
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            edtUrl.setError("La dirección web no es válida");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        lanzarIntent(intent, "No hay un navegador instalado");
    }


    // =============================================
    // INTENT IMPLÍCITO 3: ABRIR MARCADOR TELEFÓNICO
    // =============================================

    private void abrirMarcador() {

        // Quitamos espacios del número
        String telefono = edtTelefono.getText().toString()
                .trim()
                .replace(" ", "");

        // VALIDACIÓN: no vacío
        if (telefono.isEmpty()) {
            edtTelefono.setError("Ingresa un número de teléfono");
            return;
        }

        // VALIDACIÓN: formato de teléfono y largo mínimo
        if (!Patterns.PHONE.matcher(telefono).matches() || telefono.length() < 8) {
            edtTelefono.setError("Número de teléfono no válido");
            return;
        }

        /*
         * ACTION_DIAL solo muestra el marcador con el número.
         * NO llama automáticamente, por eso no necesita permiso CALL_PHONE.
         */
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefono));

        lanzarIntent(intent, "No hay una app de teléfono instalada");
    }


    // =============================================
    // INTENT IMPLÍCITO 4: ENVIAR SMS (INTERFAZ DEL SISTEMA)
    // =============================================

    private void enviarSms() {

        // Quitamos espacios del número
        String telefono = edtTelefonoSms.getText().toString()
                .trim()
                .replace(" ", "");

        String mensaje = edtMensajeSms.getText().toString().trim();

        // VALIDACIÓN: teléfono no vacío
        if (telefono.isEmpty()) {
            edtTelefonoSms.setError("Ingresa un número de teléfono");
            return;
        }

        // VALIDACIÓN: formato de teléfono y largo mínimo
        if (!Patterns.PHONE.matcher(telefono).matches() || telefono.length() < 8) {
            edtTelefonoSms.setError("Número de teléfono no válido");
            return;
        }

        // VALIDACIÓN: mensaje no vacío
        if (mensaje.isEmpty()) {
            edtMensajeSms.setError("Escribe un mensaje");
            return;
        }

        /*
         * ACTION_SENDTO con "smsto:" abre la app de mensajes del sistema
         * con el número y el texto ya escritos ("sms_body").
         * NO envía el SMS automáticamente: el usuario debe presionar enviar,
         * por eso no necesita el permiso SEND_SMS.
         */
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + telefono));
        intent.putExtra("sms_body", mensaje);

        lanzarIntent(intent, "No hay una app de mensajes instalada");
    }


    // =============================================
    // INTENT IMPLÍCITO 5: AJUSTES DE WI-FI
    // =============================================

    private void abrirAjustesWifi() {

        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);

        lanzarIntent(intent, "No se pudo abrir los ajustes de Wi-Fi");
    }


    // =============================================
    // MÉTODO AUXILIAR PARA INTENTS IMPLÍCITOS
    // =============================================

    /*
     * Intenta abrir el intent. Si no existe ninguna app
     * que pueda manejarlo, Android lanza ActivityNotFoundException
     * y mostramos un mensaje en lugar de cerrar la app.
     */
    private void lanzarIntent(Intent intent, String mensajeError) {

        try {

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    MainActivity.this,
                    mensajeError,
                    Toast.LENGTH_SHORT
            ).show();

        }
    }


    // =============================================
    // INTENT EXPLÍCITO 1: IR A DETALLE (CON EXTRAS)
    // =============================================

    private void irADetalle() {

        /*
         * INTENT EXPLÍCITO
         * Indicamos exactamente qué Activity abrir: DetalleActivity.
         * Con putExtra enviamos datos a la otra pantalla.
         */
        Intent intent = new Intent(MainActivity.this, DetalleActivity.class);

        intent.putExtra(Claves.EXTRA_TITULO, "Teclado mecánico");
        intent.putExtra(Claves.EXTRA_DESCRIPCION, "Teclado RGB con switches azules");
        intent.putExtra(Claves.EXTRA_PRECIO, 29990);

        startActivity(intent);
    }


    // =============================================
    // INTENT EXPLÍCITO 2: IR A AYUDA
    // =============================================

    private void irAAyuda() {

        Intent intent = new Intent(MainActivity.this, AyudaActivity.class);

        startActivity(intent);
    }


    // =============================================
    // INTENT EXPLÍCITO 3: IR A CONFIRMAR (CON RESULTADO)
    // =============================================

    private void irAConfirmar() {

        String nombre = edtNombre.getText().toString().trim();

        // VALIDACIÓN: el nombre no puede estar vacío
        if (nombre.isEmpty()) {
            edtNombre.setError("Ingresa tu nombre");
            return;
        }

        Intent intent = new Intent(MainActivity.this, ConfirmActivity.class);

        intent.putExtra(Claves.EXTRA_NOMBRE, nombre);

        // launch() abre la pantalla y espera una respuesta
        launcherConfirmar.launch(intent);
    }

}
