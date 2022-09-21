package com.example.bellaquita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.bellaquita.Adaptadores.CustomAdapterEgresos;
import com.example.bellaquita.Objetos.Egreso;
import com.example.bellaquita.Objetos.Producto;
import com.example.bellaquita.Objetos.Venta;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EstadisticasActivity extends AppCompatActivity {

    private ArrayList<Egreso> listadoEgresos;
    RecyclerView recyclerEgresos;
    CustomAdapterEgresos customAdapterEgresos;

    private ArrayList<Egreso> listadoIngresos;
    RecyclerView recyclerIngresos;
    CustomAdapterEgresos customAdapterIngresos;

    private ArrayList<Venta> listadoVentas;
    private ArrayList<Producto> listadoProductos;

    FirebaseFirestore basedatos;
    private Animation ScaleUp;
    private CardView cardNuevoEgreso;


    private String NombreVendedora = "Giselle";
    private int mes = 1;

    private TextView textCantVentas,textBrutoVentas,textGastos,textGanancias,textCantVentasDeb,
            textCantVentasGis,textMontoVendDeb,textMontoVendGis,textCantProdYBruto;

    //------------------------------------------------
    //------------------------------------------------
    //----------------datos utiles--------------------

    private int cantVentas = 0;
    private int cantVenDeb = 0;
    private int montoVenDeb = 0;
    private int cantVenGis = 0;
    private int montoVenGis = 0;

    private int bruto = 0;
    private int ganancia = 0;
    private int gastos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        cardNuevoEgreso = findViewById(R.id.cardNuevoEgreso);
        recyclerEgresos = findViewById(R.id.recyclerEgresos);
        recyclerIngresos = findViewById(R.id.recyclerIngresos);

        textCantVentas = findViewById(R.id.textCantVentas);
        textBrutoVentas = findViewById(R.id.textBrutoVentas);
        textGastos = findViewById(R.id.textGastos);
        textGanancias = findViewById(R.id.textGanancias);
        textCantVentasDeb = findViewById(R.id.textCantVentasDeb);
        textCantVentasGis = findViewById(R.id.textCantVentasGis);
        textMontoVendDeb = findViewById(R.id.textMontoVendDeb);
        textMontoVendGis = findViewById(R.id.textMontoVendGis);

        ScaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        cargarEgresos();
        cargarIngresos();
        cargarVentas();

        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                calcularNumeros();
            }
        }.start();

        customAdapterEgresos = new CustomAdapterEgresos(listadoEgresos,getApplicationContext(),"Egreso");
        recyclerEgresos.setAdapter(customAdapterEgresos);
        recyclerEgresos.setLayoutManager(new LinearLayoutManager(this));

        customAdapterIngresos = new CustomAdapterEgresos(listadoIngresos,getApplicationContext(),"Ingreso");
        recyclerIngresos.setAdapter(customAdapterIngresos);
        recyclerIngresos.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        mes = bundle.getInt("mes");
    }

    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------

    private void cargarEgresos(){
        try{
            listadoEgresos = new ArrayList<>();
            basedatos = FirebaseFirestore.getInstance();
            basedatos.collection("Egresos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Egreso egreso = documentSnapshot.toObject(Egreso.class);
                        if (diferenciaMes(egreso.getFecha().substring(3,5))==0){
                            listadoEgresos.add(documentSnapshot.toObject(Egreso.class));
                        }
                    }
                    customAdapterEgresos.notifyDataSetChanged();
                }
            });
        }catch (Exception es){

        }
    }
    private void cargarIngresos(){
        try{
            listadoIngresos = new ArrayList<>();
            basedatos = FirebaseFirestore.getInstance();
            basedatos.collection("Ingresos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Egreso egreso = documentSnapshot.toObject(Egreso.class);
                        if (diferenciaMes(egreso.getFecha().substring(3,5))==0){
                            listadoIngresos.add(documentSnapshot.toObject(Egreso.class));
                        }
                    }
                    customAdapterIngresos.notifyDataSetChanged();
                }
            });
        }catch (Exception es){

        }
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
                        if (diferenciaMes(venta.getFecha().substring(3,5))==0){
                            listadoVentas.add(documentSnapshot.toObject(Venta.class));
                        }
                    }
                }
            });
        }catch (Exception es){

        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------

    private int diferenciaMes(String mesItem){
        Calendar fechaComparar = new GregorianCalendar(2021, mes, 1);
        Calendar fechaItem = new GregorianCalendar(2021,Integer.parseInt(mesItem),1);

        return fechaComparar.get(Calendar.MONTH) - fechaItem.get(Calendar.MONTH);
    }
    private void calcularNumeros(){
        try{
            for (int i=0;i<listadoEgresos.size();i++) {
                gastos += Integer.parseInt(listadoEgresos.get(i).getMonto());
            }
            cantVentas = listadoVentas.size();
            for (int i=0;i<listadoVentas.size();i++) {
                ganancia += listadoVentas.get(i).getGanancia();
                bruto += listadoVentas.get(i).getTotal();
                if (listadoVentas.get(i).getVendedora().equals("Debora")){
                    cantVenDeb++;
                    montoVenDeb += listadoVentas.get(i).getTotal();
                }else if(listadoVentas.get(i).getVendedora().equals("Giselle")){
                    cantVenGis++;
                    montoVenGis += listadoVentas.get(i).getTotal();
                }
            }
            ganancia = ganancia - gastos;

            textCantVentas.setText("Cantidad Ventas: "+cantVentas);
            textBrutoVentas.setText("Bruto: $"+bruto);
            textGastos.setText("Gastos: $"+gastos);
            textGanancias.setText("Ganancias: $"+ganancia);
            textCantVentasDeb.setText("Cant: "+cantVenDeb);
            textCantVentasGis.setText("Cant: "+cantVenGis);
            textMontoVendDeb.setText("Monto: $"+montoVenDeb);
            textMontoVendGis.setText("Monto: $"+montoVenGis);

        }catch (Exception es){

        }
    }

}