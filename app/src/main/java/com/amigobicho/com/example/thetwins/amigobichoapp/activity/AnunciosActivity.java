package com.amigobicho.com.example.thetwins.amigobichoapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.StringPrepParseException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.amigobicho.com.example.thetwins.amigobichoapp.R;
import com.amigobicho.com.example.thetwins.amigobichoapp.adapter.AdapterAnuncios;
import com.amigobicho.com.example.thetwins.amigobichoapp.helper.ConfiguracaoFirebase;
import com.amigobicho.com.example.thetwins.amigobichoapp.helper.RecyclerItemClickListener;
import com.amigobicho.com.example.thetwins.amigobichoapp.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private String filtroRegiao = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorRegiao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();

        //Configuracoes Iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");

        //Configurar RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();

        //Aplicar evento de clique
        recyclerAnunciosPublicos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAnunciosPublicos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Anuncio anuncioSelecionado = listaAnuncios.get( position );
                                Intent i = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                                i.putExtra("anuncioSelecionado", anuncioSelecionado);
                                startActivity(i );
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }

                )
        );


    }

    public void filtrarPorRegiao(View view){

        AlertDialog.Builder dialogRegiao = new AlertDialog.Builder(this );
        dialogRegiao.setTitle("Selecione a região desejada");

        // Configurar Spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        //Configura spinner de estados
        final Spinner spinnerRegiao = viewSpinner.findViewById(R.id.spinnerFiltro);
        String[] regiao = getResources().getStringArray(R.array.regioes);
        ArrayAdapter<String> adapter = new ArrayAdapter <String>(
                this, android.R.layout.simple_spinner_item,
                regiao
        );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerRegiao.setAdapter( adapter );
        dialogRegiao.setView(viewSpinner);


        dialogRegiao.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtroRegiao = spinnerRegiao.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                filtrandoPorRegiao = true;
            }
        });

        dialogRegiao.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogRegiao.create();
        dialog.show();

    }

    public void filtrarPorCategoria(View view){

        if (filtrandoPorRegiao == true){

            AlertDialog.Builder dialogRegiao = new AlertDialog.Builder(this );
            dialogRegiao.setTitle("Selecione a categoria desejada");

            // Configurar Spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //Configura spinner de categorias
            final Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
            String[] regioes = getResources().getStringArray(R.array.categorias);
            ArrayAdapter<String> adapter = new ArrayAdapter <String>(
                    this, android.R.layout.simple_spinner_item,
                    regioes
            );
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spinnerCategoria.setAdapter( adapter );
            dialogRegiao.setView(viewSpinner);


            dialogRegiao.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();
                }
            });

            dialogRegiao.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogRegiao.create();
            dialog.show();

        }else{
            Toast.makeText(this, "Escolha primeiro a região!",
                    Toast.LENGTH_SHORT).show();
        }


    }

    public void recuperarAnunciosPorCategoria() {


        // configura nó por categoria
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroRegiao)
                .child(filtroCategoria);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaAnuncios.clear();
                for (DataSnapshot anuncios : dataSnapshot.getChildren()) {

                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    listaAnuncios.add(anuncio);


                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }


    public void recuperarAnunciosPorEstado(){

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage("Recuperando anúncios")
                .setCancelable( false )
                .build();
        dialog.show();

        // configura nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroRegiao);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaAnuncios.clear();

                for (DataSnapshot categorias: dataSnapshot.getChildren()){
                    for (DataSnapshot anuncios: categorias.getChildren()){

                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add(anuncio);





                    }

                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    public void recuperarAnunciosPublicos(){

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage("Recuperando anúncios")
                .setCancelable( false )
                .build();
        dialog.show();

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot regioes: dataSnapshot.getChildren()){
                    for (DataSnapshot categorias: regioes.getChildren()){
                        for (DataSnapshot anuncios: categorias.getChildren()){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);





                        }

                    }


                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(autenticacao.getCurrentUser() == null ){ //usuario deslogado
            menu.setGroupVisible(R.id.group_deslogado,true );
        }else{ //usuario logado
            menu.setGroupVisible(R.id.group_logado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
                break;
            case R.id.menu_sair :
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_anuncios :
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void inicializarComponentes(){

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);

    }
}
