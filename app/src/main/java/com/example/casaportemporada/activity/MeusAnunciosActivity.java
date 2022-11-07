package com.example.casaportemporada.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casaportemporada.R;
import com.example.casaportemporada.adapter.AdapterAnuncios;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity implements AdapterAnuncios.OnClick {

    private List<Produto> produtoList = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView text_info;
    private SwipeableRecyclerView rv_anuncio;
    private AdapterAnuncios adapterAnuncios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        iniciaComponentes();
        configRv();
        confiCliques();

    }
    @Override
    protected void onStart(){
        super.onStart();
        recuperarAnuncios();
    }

    private void confiCliques(){
        findViewById(R.id.id_add).setOnClickListener(view -> startActivity(new Intent(this, FormAnuncioActivity.class)));
    }

    private void configRv(){
        rv_anuncio.setLayoutManager(new LinearLayoutManager(this));
        rv_anuncio.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(produtoList, this);
        rv_anuncio.setAdapter(adapterAnuncios);

        rv_anuncio.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
            }

            @Override
            public void onSwipedRight(int position) {
                showDialogDelete(position);
            }
        });
    }

    private void showDialogDelete(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete anuncio");
        builder.setMessage("Apert em sim para confirmar ou nao para cancelar");
        builder.setNegativeButton("nao", ((dialogInterface, i) -> {
            dialogInterface.dismiss();
            adapterAnuncios.notifyDataSetChanged();
        }));
        builder.setPositiveButton("sim",(((dialogInterface, i) -> {
            Produto produto = produtoList.get(pos);
            produto.deletar();

            produtoList.get(pos).deletar();
        })));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarAnuncios(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios")
                .child(FirebaseHelper.getIdFirebase());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtoList.clear();
                if (snapshot.exists()){
                    produtoList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Produto produto = snap.getValue(Produto.class);
                        produtoList.add(produto);
                    }
                    text_info.setText("");
                }else {
                    text_info.setText("Nenhum anuncio cadastrado");
                }
                progressBar.setVisibility(View.GONE);
                Collections.reverse(produtoList);
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void iniciaComponentes(){
        TextView text_titulo = findViewById(R.id.text_titulo);
        text_titulo.setText("meus anuncios");

        progressBar = findViewById(R.id.progressBar);
        text_info = findViewById(R.id.text_info);
        rv_anuncio = findViewById(R.id.rv_anuncio);
    }

    @Override
    public void OnClikeListner(Produto produto) {
        Intent intent = new Intent(this, FormAnuncioActivity.class);
        intent.putExtra("produto", produto);
        startActivity(intent);
    }
}