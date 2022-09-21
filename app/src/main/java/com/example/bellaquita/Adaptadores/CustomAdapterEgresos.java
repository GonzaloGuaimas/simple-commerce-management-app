package com.example.bellaquita.Adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bellaquita.Objetos.Egreso;
import com.example.bellaquita.R;

import java.util.ArrayList;

public class CustomAdapterEgresos extends RecyclerView.Adapter<CustomAdapterEgresos.MyViewHolder> implements  View.OnClickListener,View.OnLongClickListener{

    private View.OnClickListener listener;
    private View.OnLongClickListener listener2;
    private ArrayList<Egreso> listado;
    Context context;
    private String tipo = "";

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }
    @Override
    public boolean onLongClick(View view) {
        if(listener2!=null){
            listener2.onLongClick(view);
        }
        return true;
    }
    public  void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }
    public  void setOnLongClickListener(View.OnLongClickListener listener2){
        this.listener2=listener2;
    }
    public CustomAdapterEgresos(ArrayList<Egreso> listado, Context context,String tipo) {
        this.listado = listado;
        this.context = context;
        this.tipo = tipo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_egresos,parent,false);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textMotivo.setText(listado.get(position).getMotivo());
        holder.textFecha.setText(listado.get(position).getFecha());
        holder.textHora.setText(listado.get(position).getHora());
        holder.textVendedora.setText(listado.get(position).getVendedora());
        holder.textMonto.setText("Monto: $"+String.valueOf(listado.get(position).getMonto()));
    }

    @Override
    public int getItemCount() {
        return listado.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textMotivo,textFecha,textHora,textVendedora,textMonto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textMotivo = itemView.findViewById(R.id.textMotivo);
            textFecha = itemView.findViewById(R.id.textFecha);
            textHora = itemView.findViewById(R.id.textHora);
            textVendedora = itemView.findViewById(R.id.textVendedora);
            textMonto = itemView.findViewById(R.id.textMonto);


            switch (tipo){
                case "Egreso":
                    textMotivo.setTextColor(Color.RED);
                    break;
                case "Ingreso":
                    textMotivo.setTextColor(Color.GREEN);
                    break;

            }
        }
    }
}