package com.example.models;

public class Produto {
    private int id;
    private String nome;
    private double preco;
    private String lote;
    private String sku;

    // Construtor completo (com preço)
    public Produto(int id, String nome, double preco, String lote, String sku) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.lote = lote;
        this.sku = sku;
    }

    // Construtor reduzido (sem preço) — usado no ProdutoController
    public Produto(int id, String nome, String lote, String sku) {
        this.id = id;
        this.nome = nome;
        this.lote = lote;
        this.sku = sku;
        this.preco = 0.0; // valor padrão se não for informado
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return nome; // útil para ComboBox e debug
    }
}
