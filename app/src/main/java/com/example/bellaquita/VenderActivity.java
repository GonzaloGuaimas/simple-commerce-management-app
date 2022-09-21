package com.example.bellaquita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bellaquita.Adaptadores.CustomAdapterProductos;
import com.example.bellaquita.Objetos.Egreso;
import com.example.bellaquita.Objetos.Estado;
import com.example.bellaquita.Objetos.Producto;
import com.example.bellaquita.Objetos.Venta;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VenderActivity extends AppCompatActivity {

    private ArrayList<Producto> listado;
    RecyclerView recyclerProductos;
    CustomAdapterProductos customAdapterProductos;

    private ArrayList<Producto> listadoAgregados;
    RecyclerView recyclerProductosAgregados;
    CustomAdapterProductos customAdapterProductosAgregados;

    FirebaseFirestore basedatos;
    ProgressBar progressBar;
    Button buttonBuscar;
    EditText textBuscar,textNombreCliente;
    TextView textTotal;
    private Animation ScaleUp;

    private CardView cardVender;

    private String NombreVendedora = "Giselle";


    private int total = 0;
    private int ganancia = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender);

        recyclerProductos = findViewById(R.id.recyclerProductos);
        recyclerProductosAgregados = findViewById(R.id.recyclerProductosAgregados);
        progressBar = findViewById(R.id.progressBar);
        buttonBuscar = findViewById(R.id.buttonBuscar);
        textBuscar = findViewById(R.id.textBuscar);

        cardVender = findViewById(R.id.cardVender);
        textNombreCliente = findViewById(R.id.textNombreCliente);
        textTotal = findViewById(R.id.textTotal);

        ScaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        CargarProductos();

        customAdapterProductos = new CustomAdapterProductos(listado,getApplicationContext(),"SinCosto");
        recyclerProductos.setAdapter(customAdapterProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        listadoAgregados = new ArrayList<>();
        customAdapterProductosAgregados = new CustomAdapterProductos(listadoAgregados,getApplicationContext(),"ConPrecio");
        recyclerProductosAgregados.setAdapter(customAdapterProductosAgregados);
        recyclerProductosAgregados.setLayoutManager(new LinearLayoutManager(this));


        cardVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardVender.startAnimation(ScaleUp);
                generarVenta();
            }
        });


        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonBuscar.startAnimation(ScaleUp);
                CargaYBusqueda(textBuscar.getText().toString());
            }
        });

        customAdapterProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VenderActivity.this);
                final String[] tipoPrecio = new String[]{"Precio Contado","Precio Lista"};
                builder.setTitle("Seleccioná Tipo Pago").setItems(tipoPrecio, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String precio = "";
                        final String codigo = listado.get(recyclerProductos.getChildAdapterPosition(view)).getCodigo();
                        final String descripcion = listado.get(recyclerProductos.getChildAdapterPosition(view)).getDescripcion();
                        final String marca = String.valueOf(listado.get(recyclerProductos.getChildAdapterPosition(view)).getMarca());
                        final String color = String.valueOf(listado.get(recyclerProductos.getChildAdapterPosition(view)).getColor());
                        final String talle = String.valueOf(listado.get(recyclerProductos.getChildAdapterPosition(view)).getTalle());
                        final String precioCosto = String.valueOf(listado.get(recyclerProductos.getChildAdapterPosition(view)).getPrecioCosto());
                        final String precioLista = String.valueOf(listado.get(recyclerProductos.getChildAdapterPosition(view)).getPrecioLista());
                        final String precioContado = String.valueOf(listado.get(recyclerProductos.getChildAdapterPosition(view)).getPrecioContado());

                        if(tipoPrecio[which]=="Precio Contado"){
                            precio = String.valueOf(listado.get(recyclerProductos.getChildAdapterPosition(view)).getPrecioContado());
                        }else if (tipoPrecio[which]=="Precio Lista"){
                            precio = String.valueOf(listado.get(recyclerProductos.getChildAdapterPosition(view)).getPrecioLista());
                        }
                        Producto producto = new Producto(codigo,descripcion,marca,color,talle,precioCosto,precioLista,precioContado,precio);
                        listadoAgregados.add(producto);
                        customAdapterProductosAgregados.notifyDataSetChanged();
                        calcularTotal();
                    }
                });
                builder.show();
            }
        });

        customAdapterProductosAgregados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Producto producto= listadoAgregados.get(recyclerProductosAgregados.getChildAdapterPosition(view));
                listadoAgregados.remove(producto);
                calcularTotal();
                customAdapterProductosAgregados.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),producto.getDescripcion()+" eliminado",Toast.LENGTH_LONG).show();
            }
        });
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void generarVenta(){
        try {
            if (listadoAgregados.size()>0){
                if (!TextUtils.isEmpty(textNombreCliente.getText().toString())){
                    basedatos = FirebaseFirestore.getInstance();
                    DateFormat datFormatID =new SimpleDateFormat("ddMMyyyyHHmmss");
                    DateFormat datFormatFecha =new SimpleDateFormat("dd/MM/yyyy");
                    DateFormat datFormatHora =new SimpleDateFormat("HH:mm");
                    Date date = new Date();

                    Venta venta = new Venta(datFormatID.format(date),datFormatFecha.format(date),datFormatHora.format(date),total,ganancia,NombreVendedora,listadoAgregados,textNombreCliente.getText().toString());

                    basedatos.collection("Ventas").document(venta.getID()).set(venta);

                    Egreso egreso = new Egreso(datFormatID.format(date),datFormatFecha.format(date),datFormatHora.format(date),"venta "+textNombreCliente.getText().toString(),
                            String.valueOf(ganancia),NombreVendedora,"Ingreso");
                    basedatos.collection("Ingresos").document(egreso.getID()).set(egreso);
                    Toast.makeText(getApplicationContext(),"Ingreso Agregado",Toast.LENGTH_LONG).show();

                    cartel();
                }else{
                    Toast.makeText(getApplicationContext(),"Agregue Nombre del Cliente",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Agregue Productos a la Venta",Toast.LENGTH_LONG).show();
            }


        }catch (Exception es){

        }
    }
    private void cartel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VenderActivity.this);
        View viewInflated = LayoutInflater.from(VenderActivity.this).inflate(R.layout.venta_generada, (ViewGroup) findViewById(android.R.id.content), false);

        final CardView cardGuardarProducto = (CardView) viewInflated.findViewById(R.id.cardGuardarProducto);
        builder.setView(viewInflated);

        cardGuardarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Venta Generada",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        builder.show();
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void ActualizarBD(String data){
        try {
            FileOutputStream fileout = openFileOutput("Productos.txt", Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(data.toString());
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void CargarProductos(){
        listado = new ArrayList<>();
        basedatos = FirebaseFirestore.getInstance();
        basedatos.collection("Usuarios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    if(documentSnapshot.getId().equalsIgnoreCase(NombreVendedora)){
                        final Estado estado = documentSnapshot.toObject(Estado.class);
                        if(estado.isActualizado()==false){
                            ActualizarBD("");
                            basedatos.collection("Productos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    String data = "";
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Producto producto = documentSnapshot.toObject(Producto.class);
                                        data += producto.getCodigo()+";"+producto.getDescripcion()+";"+producto.getMarca()+";"
                                                +producto.getColor()+";"+producto.getTalle()+";"
                                                +producto.getPrecioCosto()+";"+producto.getPrecioLista()+";"
                                                +producto.getPrecioContado()+"\n";
                                    }
                                    estado.setActualizado(true);
                                    basedatos.collection("Usuarios").document(NombreVendedora).set(estado);
                                    ActualizarBD(data);
                                }
                            });
                            Toast.makeText(getApplicationContext(),"Se Actualizaron Productos",Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        }
                        break;
                    }
                }
            }
        });
        CargaYBusqueda("");

    }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void CargaYBusqueda(String catego){

        try{
            listado.clear();
            customAdapterProductos.notifyDataSetChanged();
        }catch (Exception e){
        }
        try{
            String[]data =  ObtenerProductos().split("\n");
            for(int i=0;i<data.length;i++){
                String prod []= data[i].split(";");
                Producto producto = new Producto();
                producto.setCodigo(prod[0]);
                producto.setDescripcion(prod[1]);
                producto.setMarca(prod[2]);
                producto.setColor(prod[3]);
                producto.setTalle(prod[4]);
                producto.setPrecioCosto(prod[5]);
                producto.setPrecioLista(prod[6]);
                producto.setPrecioContado(prod[7]);
                //lo

                if(textBuscar.getText().toString().equalsIgnoreCase("")){
                    listado.add(producto);
                }else{
                    String B = textBuscar.getText().toString().substring(0,3);
                    String A = "";
                    String C = "";
                    String D = producto.getDescripcion();
                    try{
                        A = producto.getDescripcion().substring(0,3);
                        try{
                            C = D.split(" ")[1].substring(0,3);
                        }catch (Exception e){
                        }

                    }catch (Exception e){
                    }
                    if(A.equalsIgnoreCase(B) || C.equalsIgnoreCase(B)){
                        listado.add(producto);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                customAdapterProductos.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }.start();
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private String ObtenerProductos(){
        FileInputStream fis = null;
        String text = "";
        try {
            fis = openFileInput("Productos.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            while ((text = br.readLine()) != null) {
                sb.append(text+"\n");
            }
            text = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  text;
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------

    private void calcularTotal(){
        try{

            for(int i = 0; i<listadoAgregados.size();i++){
                total += Integer.parseInt(listadoAgregados.get(i).getPrecio());
                ganancia += Integer.parseInt(listadoAgregados.get(i).getPrecio()) - Integer.parseInt(listadoAgregados.get(i).getPrecioCosto());
            }
            textTotal.setText("TOTAL: $"+String.valueOf(total));

        }catch (Exception es){

        }

    }
}