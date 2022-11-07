package com.example.casaportemporada.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.casaportemporada.R;
import com.example.casaportemporada.activity.autenticacao.LoginActivity;
import com.example.casaportemporada.helper.FirebaseHelper;

public class MainActivity extends AppCompatActivity {

    private ImageButton id_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciaComponentes();
        configCliques();
    }

    private void configCliques(){
        id_menu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, id_menu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_home, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if(menuItem.getItemId() == R.id.menu_filtrar){
                    startActivity(new Intent(this, FiltrarAnunciosActivity.class));
                }else if(menuItem.getItemId() == R.id.menus_meus_anundios){
                    if(FirebaseHelper.getAutenticado()){
                        startActivity(new Intent(this, MeusAnunciosActivity .class));
                    }else {
                        shownDialogLogin();
                    }
                }else{
                    if(FirebaseHelper.getAutenticado()){
                        startActivity(new Intent(this, MinhaContaActivity.class));
                    }else {
                        shownDialogLogin();
                    }
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void shownDialogLogin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Autenticaçao");
        builder.setCancelable(false);
        builder.setNegativeButton("Nao", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("Nao", (dialog, which) -> {startActivity(new Intent(this, LoginActivity.class));
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void iniciaComponentes(){
        id_menu = findViewById(R.id.id_menu);
    }
}