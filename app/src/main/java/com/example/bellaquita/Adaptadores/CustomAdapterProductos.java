package com.example.bellaquita.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bellaquita.Objetos.Producto;
import com.example.bellaquita.R;

import java.util.ArrayList;

public class CustomAdapterProductos extends RecyclerView.Adapter<CustomAdapterProductos.MyViewHolder> implements  View.OnClickListener,View.OnLongClickListener{

    private View.OnClickListener listener;
    private View.OnLongClickListener listener2;
    private ArrayList<Producto> listado;
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
    public  void setOnLongClickListener(View.OnLongClickListener listener2){
        this.listener2=listener2;
    }
    public  void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    public CustomAdapterProductos(ArrayList<Producto> listado, Context context,String tipo) {
        this.listado = listado;
        this.context = context;
        this.tipo = tipo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_productos,parent,false);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textDescripcion.setText(listado.get(position).getDescripcion());
        holder.textCodigo.setText(listado.get(position).getCodigo());
        holder.textMarca.setText(listado.get(position).getMarca());
        holder.textColor.setText(listado.get(position).getColor());
        holder.textTalle.setText(listado.get(position).getTalle());
        holder.textPrecioCosto.setText("$"+String.valueOf(listado.get(position).getPrecioCosto()));
        holder.textPrecioLista.setText("$"+String.valueOf(listado.get(position).getPrecioLista()));
        holder.textPrecioContado.setText("$"+String.valueOf(listado.get(position).getPrecioContado()));
        holder.textPrecio.setText("$"+String.valueOf(listado.get(position).getPrecio()));
    }

    @Override
    public int getItemCount() {
        return listado.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textCodigo,textDescripcion,textMarca,textColor,textTalle,textPrecioCosto,textPrecioLista,textPrecioContado,textPrecio;
        LinearLayout linearPrecios;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textCodigo = itemView.findViewById(R.id.textCodigo);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            textMarca = itemView.findViewById(R.id.textMarca);
            textColor = itemView.findViewById(R.id.textColor);
            textTalle = itemView.findViewById(R.id.textTalle);
            textPrecioCosto = itemView.findViewById(R.id.textPrecioCosto);
            textPrecioLista = itemView.findViewById(R.id.textPrecioLista);
            textPrecioContado = itemView.findViewById(R.id.textPrecioContado);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            linearPrecios = itemView.findViewById(R.id.linearPrecios);

            switch (tipo){
                case "Simple":
                    textPrecio.setVisibility(View.INVISIBLE);
                    break;
                case "SinCosto":
                    textPrecioCosto.setVisibility(View.INVISIBLE);
                    textPrecio.setVisibility(View.INVISIBLE);
                    break;
                case "ConPrecio":
                    textPrecio.setVisibility(View.VISIBLE);
                    linearPrecios.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }
}
