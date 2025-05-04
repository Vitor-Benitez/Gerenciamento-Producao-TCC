package com.example.models;

import java.time.LocalDateTime;

public class Producao {
    private int id;
    private int funcionarioId;
    private int maquinaId;
    private int produtoId;
    private int quantidade;
    private LocalDateTime dataProducao;

    // Campos auxiliares (para exibição)
    private String nomeFuncionario;
    private String nomeMaquina;
    private String nomeProduto;

    // Construtor vazio (necessário para certas operações)
    public Producao() {}

    // Construtor completo
    public Producao(int id, int funcionarioId, int maquinaId, int produtoId,
                    int quantidade, LocalDateTime dataProducao,
                    String nomeFuncionario, String nomeMaquina, String nomeProduto) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.maquinaId = maquinaId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.dataProducao = dataProducao;
        this.nomeFuncionario = nomeFuncionario;
        this.nomeMaquina = nomeMaquina;
        this.nomeProduto = nomeProduto;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(int funcionarioId) { this.funcionarioId = funcionarioId; }

    public int getMaquinaId() { return maquinaId; }
    public void setMaquinaId(int maquinaId) { this.maquinaId = maquinaId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataProducao() { return dataProducao; }
    public void setDataProducao(LocalDateTime dataProducao) { this.dataProducao = dataProducao; }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

    public String getNomeMaquina() { return nomeMaquina; }
    public void setNomeMaquina(String nomeMaquina) { this.nomeMaquina = nomeMaquina; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    // toString para facilitar exibição no TableView ou ComboBox
    @Override
    public String toString() {
        return String.format("Funcionário: %s | Máquina: %s | Produto: %s",
                nomeFuncionario != null ? nomeFuncionario : "N/A",
                nomeMaquina != null ? nomeMaquina : "N/A",
                nomeProduto != null ? nomeProduto : "N/A");
    }
}
