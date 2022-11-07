package com.example.casaportemporada.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casaportemporada.R;
import com.example.casaportemporada.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Produto> produtoList;
    private OnClick onClick;

    public AdapterAnuncios(List<Produto> produtoList, OnClick onClick) {
        this.produtoList = produtoList;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position);

        Picasso.get().load(produto.getUrlImage()).into(holder.img_anuncio);
        holder.text_titulo.setText(produto.getTitulo());
        holder.text_descricao.setText(produto.getDescricao());
        holder.text_data.setText("");

        holder.itemView.setOnClickListener(view -> onClick.OnClikeListner(produto));

    }

    @Override
    public int getItemCount() {return produtoList.size();}

    public interface OnClick{
        public void OnClikeListner(Produto produto);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_anuncio;
        TextView text_titulo, text_descricao, text_data;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_anuncio = itemView.findViewById(R.id.img_anuncio);
            text_titulo = itemView.findViewById(R.id.test_titulo);
            text_descricao = itemView.findViewById(R.id.test_descricao);
            text_data = itemView.findViewById(R.id.test_data);

        }
    }
}
