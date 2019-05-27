package com.amigobicho.com.example.thetwins.amigobichoapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amigobicho.com.example.thetwins.amigobichoapp.R;
import com.amigobicho.com.example.thetwins.amigobichoapp.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.w3c.dom.Text;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo;
    private TextView descricao;
    private TextView regiao;
    private TextView recompensa;
    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        //Configurar toolbar
        getSupportActionBar().setTitle("Detalhe Produto");


        //Inicializar componentes de interface
        inicializarComponentes();

        //Recupera anúncio para exibicao
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");

        if (anuncioSelecionado != null){

            titulo.setText(anuncioSelecionado.getNome());
            descricao.setText(anuncioSelecionado.getDescricao());
            regiao.setText(anuncioSelecionado.getRegiao());
            recompensa.setText(anuncioSelecionado.getRecompensa());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);

                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);

        }


    }

    public void visualizarTelefone(View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity( i );

    }

    private void inicializarComponentes(){
        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textTituloDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);
        regiao = findViewById(R.id.textRegiaoDetalhes);
        recompensa = findViewById(R.id.textPrecoDetalhe);
    }
}
