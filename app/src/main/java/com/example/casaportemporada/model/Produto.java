package com.example.casaportemporada.model;

import com.example.casaportemporada.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class Produto implements Serializable {

    private String id;
    private String titulo;
    private String descricao;
    private String quarto;
    private String banheiro;
    private String garagem;
    private boolean status;
    private String urlImage;

    public Produto(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference();

    }

    public void salvar(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("Produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(this.getId());
        reference.setValue(this);
    }

    public void deletar(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("Produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(this.getId());
        reference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                StorageReference storageReference = FirebaseHelper.getStorageReference()
                        .child("imagens")
                        .child("Produto")
                        .child(this.getId() + "jpeg");
                storageReference.delete();
            }
        });

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getQuarto() {
        return quarto;
    }

    public void setQuarto(String quarto) {
        this.quarto = quarto;
    }

    public String getBanheiro() {
        return banheiro;
    }

    public void setBanheiro(String banheiro) {
        this.banheiro = banheiro;
    }

    public String getGaragem() {
        return garagem;
    }

    public void setGaragem(String garagem) {
        this.garagem = garagem;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
