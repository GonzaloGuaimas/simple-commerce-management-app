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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bellaquita.Adaptadores.CustomAdapterProductos;
import com.example.bellaquita.Objetos.Estado;
import com.example.bellaquita.Objetos.Producto;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
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
import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity {

    private ArrayList<Producto> listado;
    RecyclerView recyclerProductos;
    CustomAdapterProductos customAdapterProductos;

    FirebaseFirestore basedatos;
    ProgressBar progressBar;
    Button buttonBuscar;
    EditText textBuscar;
    private Animation ScaleUp;
    private CardView cardAgregarProducto;



    private String NombreVendedora = "Giselle";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        recyclerProductos = findViewById(R.id.recyclerProductos);
        progressBar = findViewById(R.id.progressBar3);
        buttonBuscar = findViewById(R.id.buttonBuscar);
        textBuscar = findViewById(R.id.textBuscar);
        cardAgregarProducto = findViewById(R.id.cardAgregarProducto);

        ScaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);


        CargarProductos();

        customAdapterProductos = new CustomAdapterProductos(listado,getApplicationContext(),"Simple");
        recyclerProductos.setAdapter(customAdapterProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        cardAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardAgregarProducto.startAnimation(ScaleUp);
                dialogInputProducto("","","","","","","","");
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

                String codigo = listado.get(recyclerProductos.getChildAdapterPosition(view)).getCodigo();
                String descripcion = listado.get(recyclerProductos.getChildAdapterPosition(view)).getDescripcion();
                String marca = listado.get(recyclerProductos.getChildAdapterPosition(view)).getMarca();
                String color = listado.get(recyclerProductos.getChildAdapterPosition(view)).getColor();
                String talle = listado.get(recyclerProductos.getChildAdapterPosition(view)).getTalle();
                String pCosto = listado.get(recyclerProductos.getChildAdapterPosition(view)).getPrecioCosto();
                String pLista = listado.get(recyclerProductos.getChildAdapterPosition(view)).getPrecioLista();
                String pContado = listado.get(recyclerProductos.getChildAdapterPosition(view)).getPrecioContado();

                dialogInputProducto(codigo,descripcion,marca,color,talle,pCosto,pLista,pContado);
            }
        });
        customAdapterProductos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProductosActivity.this);
                builder.setMessage("Desea Eliminar Producto? "+listado.get(recyclerProductos.getChildAdapterPosition(view)).getDescripcion());
                builder.setCancelable(true);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        basedatos = FirebaseFirestore.getInstance();
                        basedatos.collection("Egresos").document(listado.get(recyclerProductos.getChildAdapterPosition(view)).getCodigo()).delete();

                        Estado estado = new Estado();
                        estado.setActualizado(false);
                        basedatos.collection("Usuarios").document(NombreVendedora).set(estado);
                        Toast.makeText(getApplicationContext(),"Producto Agregado/Actualizado",Toast.LENGTH_LONG).show();
                        finish();

                    }
                });
                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });
    }


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
    private void dialogInputProducto(String codigo,String descripcion,String marca,String color,String talle,String pCosto,String pLista,String pContado){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductosActivity.this);
        View viewInflated = LayoutInflater.from(ProductosActivity.this).inflate(R.layout.input_producto, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText textCodigo = (EditText) viewInflated.findViewById(R.id.textCodigo);
        final EditText textDescripcion = (EditText) viewInflated.findViewById(R.id.textDescripcion);
        final EditText textMarca = (EditText) viewInflated.findViewById(R.id.textMarca);
        final EditText textColor = (EditText) viewInflated.findViewById(R.id.textColor);
        final EditText textTalle = (EditText) viewInflated.findViewById(R.id.textTalle);
        final EditText textPrecioCosto = (EditText) viewInflated.findViewById(R.id.textPrecioCosto);
        final EditText textPrecioLista = (EditText) viewInflated.findViewById(R.id.textPrecioLista);
        final EditText textPrecioContado = (EditText) viewInflated.findViewById(R.id.textPrecioContado);
        final CardView cardGuardarProducto = (CardView) viewInflated.findViewById(R.id.cardGuardarProducto);

        textCodigo.setText(codigo);
        textDescripcion.setText(descripcion);
        textMarca.setText(marca);
        textColor.setText(color);
        textTalle.setText(talle);
        textPrecioCosto.setText(pCosto);
        textPrecioLista.setText(pLista);
        textPrecioContado.setText(pContado);

        builder.setView(viewInflated);

        textPrecioCosto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Double precioContado = Double.parseDouble(textPrecioCosto.getText().toString())*2.0;
                Double precioLista = Double.parseDouble(textPrecioCosto.getText().toString())*2.3;

                textPrecioContado.setText(String.valueOf(Math.round(precioContado)));
                textPrecioLista.setText(String.valueOf(Math.round(precioLista)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cardGuardarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(textCodigo.getText().toString()) &&
                        !TextUtils.isEmpty(textDescripcion.getText().toString())&&
                        !TextUtils.isEmpty(textPrecioCosto.getText().toString())&&
                        !TextUtils.isEmpty(textPrecioLista.getText().toString())&&
                        !TextUtils.isEmpty(textPrecioContado.getText().toString())
                        ){
                    basedatos = FirebaseFirestore.getInstance();
                    Producto producto = new Producto(textCodigo.getText().toString(),textDescripcion.getText().toString(),textMarca.getText().toString(),
                            textColor.getText().toString(),textTalle.getText().toString(),textPrecioCosto.getText().toString(),textPrecioLista.getText().toString(),
                            textPrecioContado.getText().toString(),"");
                    basedatos.collection("Productos").document(producto.getCodigo()).set(producto);

                    Estado estado = new Estado();
                    estado.setActualizado(false);
                    basedatos.collection("Usuarios").document(NombreVendedora).set(estado);
                    Toast.makeText(getApplicationContext(),"Producto Agregado/Actualizado",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Complete Campos",Toast.LENGTH_LONG).show();
                }

            }
        });

        builder.show();
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


}