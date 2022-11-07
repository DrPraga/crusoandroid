package com.example.casaportemporada.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casaportemporada.R;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Produto;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class FormAnuncioActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
            }
    );

    private static final int REQUEST_GALERIA = 100;

    private String caminhoImagem;
    private Bitmap imagem;
    private ImageView img_anuncio;
    private ProgressBar progressBar;

    private EditText edit_titulo;
    private EditText edit_descricao;
    private EditText edit_quarto;
    private EditText edit_banheiro;
    private EditText edit_garagem;
    private CheckBox cb_status;

    private Produto produto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_anuncio);

        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            produto = (Produto) bundle.getSerializable("anuncio");
            configDados();
        }

        configClicks();
    }

    private void configDados(){
        Picasso.get().load(produto.getUrlImage()).into(img_anuncio);
    }

    public void verificaPermissaoGaleria(View view){
        PermissionListener permissionListener =  new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormAnuncioActivity.this, "Permissao Negada", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissaoGaleria(permissionListener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});

    }

    private void showDialogPermissaoGaleria(PermissionListener listener, String[] permissoes){
        TedPermission.with(this)
                .setPermissionListener(listener)
                .setDeniedTitle("Permissoes negadas")
                .setDeniedMessage("voce negou as permissoes para acessar a galeria do dispositivo, deseja permitir")
                .setDeniedCloseButtonText("Nao")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();

    }

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);

    }

    private void configClicks(){
        findViewById(R.id.id_salvar).setOnClickListener(view -> validaDados());
        findViewById(R.id.id_voltar).setOnClickListener(view -> finish());
    }

    private void validaDados() {
        String titulo = edit_titulo.getText().toString();
        String descricao = edit_descricao.getText().toString();
        String quarto = edit_quarto.getText().toString();
        String banheiro = edit_banheiro.getText().toString();
        String garagem = edit_garagem.getText().toString();

        if (!titulo.isEmpty()){
            if (!descricao.isEmpty()){
                if (!quarto.isEmpty()){
                    if (!banheiro.isEmpty()){
                        if (!garagem.isEmpty()){

                            if (produto == null) produto = new Produto();
                            produto.setTitulo(titulo);
                            produto.setDescricao(descricao);
                            produto.setQuarto(quarto);
                            produto.setBanheiro(banheiro);
                            produto.setGaragem(garagem);
                            produto.setStatus(cb_status.isChecked());

                            if(caminhoImagem != null){
                                salvarImagemProduto();
                            }else {
                                if (produto.getUrlImage() != null){
                                    produto.salvar();
                                }else {
                                    Toast.makeText(this, "Selecione uma imagem para o anuncio", Toast.LENGTH_SHORT);
                                }
                            }

                            if (caminhoImagem != null){
                                salvarImagemProduto();
                            }else{
                                Toast.makeText(this, "Selecione uma imagem para o anuncio", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            edit_garagem.requestFocus();
                            edit_garagem.setError("Informaçao obrigatoria");
                        }
                    }else {
                        edit_banheiro.requestFocus();
                        edit_banheiro.setError("Informaçao obrigatoria");
                    }
                }else {
                    edit_quarto.requestFocus();
                    edit_quarto.setError("Informaçao obrigatoria");
                }
            }else {
                edit_descricao.requestFocus();
                edit_descricao.setError("Informe uma descriçao ");
            }
        }else {
            edit_titulo.requestFocus();
            edit_titulo.setError("Informe um titulo ");
        }
    }

    private void iniciaComponentes(){
        TextView text_titulo = findViewById(R.id.text_titulo);
        text_titulo.setText("Form produto");

        edit_titulo = findViewById(R.id.edit_titulo);
        edit_descricao = findViewById(R.id.edit_descricao);
        edit_quarto = findViewById(R.id.edit_quarto);
        edit_banheiro = findViewById(R.id.edit_banheiro);
        edit_garagem = findViewById(R.id.edit_garagem);
        cb_status =  findViewById(R.id.cb_status);
        img_anuncio = findViewById(R.id.img_anuncio);
        progressBar = findViewById(R.id.progressBar);
        cb_status.setChecked(produto.isStatus());
    }

    private void salvarImagemProduto(){
        progressBar.setVisibility(View.VISIBLE);
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("Produto")
                .child(produto.getId() + "jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {

            String urlImage = task.getResult().toString();
            produto.setUrlImage(urlImage);

            produto.salvar();

            finish();

        })).addOnFailureListener(e -> {progressBar.setVisibility(View.GONE);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_GALERIA){

                Uri localImagemSelecionada = data.getData();
                caminhoImagem = localImagemSelecionada.toString();

                if(Build.VERSION.SDK_INT < 28){
                    try {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localImagemSelecionada);
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSelecionada);
                    try {
                        imagem = ImageDecoder.decodeBitmap(source);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

                img_anuncio.setImageBitmap(imagem);
            }
        }
    }
}