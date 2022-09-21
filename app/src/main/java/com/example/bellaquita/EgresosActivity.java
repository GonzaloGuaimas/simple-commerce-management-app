package com.example.bellaquita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bellaquita.Adaptadores.CustomAdapterEgresos;
import com.example.bellaquita.Adaptadores.CustomAdapterProductos;
import com.example.bellaquita.Objetos.Egreso;
import com.example.bellaquita.Objetos.Estado;
import com.example.bellaquita.Objetos.Producto;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EgresosActivity extends AppCompatActivity {
    
    private ArrayList<Egreso> listado;
    RecyclerView recyclerEgresos;
    CustomAdapterEgresos customAdapterEgresos;

    FirebaseFirestore basedatos;
    private Animation ScaleUp;
    private CardView cardNuevoEgreso;

    private String NombreVendedora = "Giselle";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egresos);
        
        cardNuevoEgreso = findViewById(R.id.cardNuevoEgreso);
        recyclerEgresos = findViewById(R.id.recyclerEgresos);

        ScaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        cargarEgresos();

        customAdapterEgresos = new CustomAdapterEgresos(listado,getApplicationContext(),"Egreso");
        recyclerEgresos.setAdapter(customAdapterEgresos);
        recyclerEgresos.setLayoutManager(new LinearLayoutManager(this));



        cardNuevoEgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardNuevoEgreso.startAnimation(ScaleUp);
                dialogInputProducto();
            }
        });
        customAdapterEgresos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EgresosActivity.this);
                builder.setMessage("Desea Eliminar Egreso? "+listado.get(recyclerEgresos.getChildAdapterPosition(view)).getFecha()+" | "+listado.get(recyclerEgresos.getChildAdapterPosition(view)).getMotivo());
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
                        basedatos.collection("Egresos").document(listado.get(recyclerEgresos.getChildAdapterPosition(view)).getID()).delete();
                        finish();
                    }
                });
                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();


                    return false;
            }
        });
    }

    private void dialogInputProducto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EgresosActivity.this);
        View viewInflated = LayoutInflater.from(EgresosActivity.this).inflate(R.layout.input_egreso, (ViewGroup) findViewById(android.R.id.content), false);
            final EditText textFecha = (EditText) viewInflated.findViewById(R.id.textFecha);
        final EditText textMotivo = (EditText) viewInflated.findViewById(R.id.textMotivo);
        final EditText textMonto = (EditText) viewInflated.findViewById(R.id.textMonto);
        final CardView cardGuardarEgreso = (CardView) viewInflated.findViewById(R.id.cardGuardarEgreso);

        textFecha.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));
                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    textFecha.setText(current);
                    textFecha.setSelection(sel < current.length() ? sel : current.length());
                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        builder.setView(viewInflated);

        cardGuardarEgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(textFecha.getText().toString());
                    if (!TextUtils.isEmpty(textMonto.getText().toString()) &&
                            !TextUtils.isEmpty(textMotivo.getText().toString())){
                        basedatos = FirebaseFirestore.getInstance();
                        DateFormat datFormatID =new SimpleDateFormat("ddMMyyyyHHmmss");
                        DateFormat datFormatHora =new SimpleDateFormat("HH:mm");
                        Date date = new Date();
                        Egreso egreso = new Egreso(datFormatID.format(date),textFecha.getText().toString(),datFormatHora.format(date),textMotivo.getText().toString(),
                                textMonto.getText().toString(),NombreVendedora,"Egreso");
                        basedatos.collection("Egresos").document(egreso.getID()).set(egreso);
                        Toast.makeText(getApplicationContext(),"Egreso Agregado",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Complete Campos",Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(),"Inserte una Fecha Correcta",Toast.LENGTH_LONG).show();
                }


            }
        });

        builder.show();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------

    private void cargarEgresos(){
        try{
            listado = new ArrayList<>();
            basedatos = FirebaseFirestore.getInstance();
            basedatos.collection("Egresos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        listado.add(documentSnapshot.toObject(Egreso.class));
                    }
                    customAdapterEgresos.notifyDataSetChanged();
                }
            });
        }catch (Exception es){

        }
    }

}