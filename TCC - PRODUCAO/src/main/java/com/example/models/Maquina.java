package com.example.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Maquina {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final StringProperty setor = new SimpleStringProperty();
    private final StringProperty descricao = new SimpleStringProperty();

    // Construtor vazio
    public Maquina() {}

    // Construtor completo
    public Maquina(int id, String nome, String setor, String descricao) {
        this.id.set(id);
        this.nome.set(nome);
        this.setor.set(setor);
        this.descricao.set(descricao);
    }

    // Métodos Property
    @SuppressWarnings("exports")
    public IntegerProperty idProperty() { return id; }
    @SuppressWarnings("exports")
    public StringProperty nomeProperty() { return nome; }
    @SuppressWarnings("exports")
    public StringProperty setorProperty() { return setor; }
    @SuppressWarnings("exports")
    public StringProperty descricaoProperty() { return descricao; }

    // Getters e Setters tradicionais
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    
    public String getNome() { return nome.get(); }
    public void setNome(String nome) { this.nome.set(nome); }
    
    public String getSetor() { return setor.get(); }
    public void setSetor(String setor) { this.setor.set(setor); }
    
    public String getDescricao() { return descricao.get(); }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    @Override
    public String toString() {
        return nome.get();
    }
}
