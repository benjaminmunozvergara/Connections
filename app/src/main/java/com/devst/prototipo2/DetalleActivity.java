package com.devst.prototipo2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Intent explícito con datos extra.
 * Recibe título, descripción y precio desde MainActivity.
 */
public class DetalleActivity extends AppCompatActivity {

    TextView txtTitulo, txtDescripcion, txtPrecio;
    Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle);

        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtPrecio = findViewById(R.id.txtPrecio);
        btnVolver = findViewById(R.id.btnVolver);


        // Obtenemos el Intent que abrió esta pantalla
        Intent intent = getIntent();

        String titulo = intent.getStringExtra(Claves.EXTRA_TITULO);
        String descripcion = intent.getStringExtra(Claves.EXTRA_DESCRIPCION);
        int precio = intent.getIntExtra(Claves.EXTRA_PRECIO, -1);

        // VALIDACIÓN: si no llegaron datos, avisamos y cerramos
        if (titulo == null || descripcion == null || precio == -1) {

            Toast.makeText(
                    DetalleActivity.this,
                    "No se recibieron datos",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        txtTitulo.setText(titulo);
        txtDescripcion.setText(descripcion);
        txtPrecio.setText("Precio: $" + precio);


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish() cierra esta pantalla y vuelve a la anterior
                finish();
            }
        });
    }
}
