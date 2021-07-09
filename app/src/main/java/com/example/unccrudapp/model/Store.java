package com.example.unccrudapp.model;

public class Store {
    String id;
    String nome;
    String site;
    String tipo;
    String cidade;
    String estado;

    public Store(){

    }

    public Store(String id, String nome, String site, String tipo, String cidade, String estado) {
        this.id = id;
        this.nome = nome;
        this.site = site;
        this.tipo = tipo;
        this.cidade = cidade;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
