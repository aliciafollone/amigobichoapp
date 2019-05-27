package com.amigobicho.com.example.thetwins.amigobichoapp.model;

import com.amigobicho.com.example.thetwins.amigobichoapp.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Anuncio implements Serializable {

    private String idAnuncio;
    private String regiao;
    private String categoria;
    private String nome;
    private String recompensa;
    private String telefone;
    private String descricao;
    private List<String> fotos;

    public Anuncio() {
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios");
        setIdAnuncio( anuncioRef.push().getKey());
    }

    public void salvar(){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios");

        anuncioRef.child(idUsuario)
                .child(getIdAnuncio())
                .setValue(this);

        salvarAnuncioPublico();
    }

    public void salvarAnuncioPublico(){


        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");

        anuncioRef.child(getRegiao())
                .child( getCategoria())
                .child(getIdAnuncio())
                .setValue(this);
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void remover (){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios")
                .child(idUsuario)
                .child(getIdAnuncio());

        anuncioRef.removeValue();
        removerAnuncioPublico();
    }

    public void removerAnuncioPublico (){

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(getRegiao())
                .child( getCategoria())
                .child(getIdAnuncio());

        anuncioRef.removeValue();
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(String recompensa) {
        this.recompensa = recompensa;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
