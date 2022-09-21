package com.example.bellaquita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bellaquita.Objetos.Egreso;
import com.example.bellaquita.Objetos.Venta;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore basedatos;
    CardView cardProductos,cardVender,cardEgresos,cardEstadisticas;
    Animation ScaleUp;
    private ArrayList<Venta> listadoVentas;

    TextView textCantVentas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTheme(R.style.SplashTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);
        cardProductos = findViewById(R.id.cardProductos);
        cardVender = findViewById(R.id.cardVender);
        cardEgresos = findViewById(R.id.cardEgresos);
        cardEstadisticas = findViewById(R.id.cardEstadisticas);
        textCantVentas = findViewById(R.id.textCantVentas);

        cargarVentas();

        cardProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardProductos.startAnimation(ScaleUp);
                Intent intent = new Intent(getApplicationContext(), ProductosActivity.class);
                startActivity(intent);

            }
        });
        cardVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardVender.startAnimation(ScaleUp);
                Intent intent = new Intent(getApplicationContext(), VenderActivity.class);
                startActivity(intent);

            }
        });
        cardEgresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardEgresos.startAnimation(ScaleUp);
                Intent intent = new Intent(getApplicationContext(), EgresosActivity.class);
                startActivity(intent);

            }
        });
        cardEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardEstadisticas.startAnimation(ScaleUp);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final String[] mes = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
                builder.setTitle("Seleccionar Mes").setItems(mes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), EstadisticasActivity.class);
                        intent.putExtra("mes", Integer.parseInt(mes[which]));
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });
    }
    private void cargarVentas(){
        try{
            listadoVentas = new ArrayList<>();
            basedatos = FirebaseFirestore.getInstance();
            basedatos.collection("Ventas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Venta venta = documentSnapshot.toObject(Venta.class);
                        listadoVentas.add(documentSnapshot.toObject(Venta.class));
                    }
                    textCantVentas.setText(String.valueOf(listadoVentas.size()));
                }
            });

        }catch (Exception es){

        }
    }
}