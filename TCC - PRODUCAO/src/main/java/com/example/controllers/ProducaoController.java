package com.example.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.example.database.Database;
import com.example.models.Funcionario;
import com.example.models.Maquina;
import com.example.models.Producao;
import com.example.models.Produto;
import com.example.utils.AlertType;
import com.example.utils.AlertUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProducaoController {
    @FXML private ComboBox<Funcionario> cmbFuncionario;
    @FXML private ComboBox<Maquina> cmbMaquina;
    @FXML private ComboBox<Produto> cmbProduto;
    @FXML private TextField txtQuantidade;
    @FXML private TextField filtroNome;
    @FXML private TableView<Producao> tableProducao;
    @FXML private TableColumn<Producao, Integer> colId;
    @FXML private TableColumn<Producao, String> colFuncionario;
    @FXML private TableColumn<Producao, String> colMaquina;
    @FXML private TableColumn<Producao, String> colProduto;
    @FXML private TableColumn<Producao, Integer> colQuantidade;
    @FXML private TableColumn<Producao, LocalDateTime> colDataProducao;

    private ObservableList<Producao> listaProducao = FXCollections.observableArrayList();
    private ObservableList<Funcionario> listaFuncionarios = FXCollections.observableArrayList();
    private ObservableList<Maquina> listaMaquinas = FXCollections.observableArrayList();
    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    private boolean emEdicao = false;
    private Producao producaoEmEdicao = null;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarComboBoxEditaveis(); // Novo método para autocomplete
        carregarFuncionarios();
        carregarMaquinas();
        carregarProdutos();
        carregarProducoes();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFuncionario.setCellValueFactory(new PropertyValueFactory<>("nomeFuncionario"));
        colMaquina.setCellValueFactory(new PropertyValueFactory<>("nomeMaquina"));
        colProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colDataProducao.setCellValueFactory(new PropertyValueFactory<>("dataProducao"));
    }

    private void configurarComboBoxEditaveis() {
        // Funcionário
        cmbFuncionario.setEditable(true);
        cmbFuncionario.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarComboBox(cmbFuncionario, listaFuncionarios, newVal);
        });

        // Máquina
        cmbMaquina.setEditable(true);
        cmbMaquina.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarComboBox(cmbMaquina, listaMaquinas, newVal);
        });

        // Produto
        cmbProduto.setEditable(true);
        cmbProduto.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarComboBox(cmbProduto, listaProdutos, newVal);
        });
    }

    private <T> void filtrarComboBox(ComboBox<T> comboBox, ObservableList<T> listaOriginal, String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            comboBox.setItems(listaOriginal);
            return;
        }

        ObservableList<T> listaFiltrada = listaOriginal.filtered(item -> {
            String textoItem = item.toString().toLowerCase();
            return textoItem.contains(filtro.toLowerCase());
        });

        comboBox.setItems(listaFiltrada);
        comboBox.show();
    }

    private void carregarFuncionarios() {
        listaFuncionarios.clear();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM funcionario")) {
            while (rs.next()) {
                listaFuncionarios.add(new Funcionario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("setor")
                ));
            }
            cmbFuncionario.setItems(listaFuncionarios);
        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Carregamento", "Não foi possível carregar os funcionários.");
            AlertUtils.logError(e);
        }
    }

    private void carregarMaquinas() {
        listaMaquinas.clear();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM maquina")) {
            while (rs.next()) {
                listaMaquinas.add(new Maquina(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("setor"),
                    rs.getString("descricao")
                ));
            }
            cmbMaquina.setItems(listaMaquinas);
        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Carregamento", "Não foi possível carregar as máquinas.");
            AlertUtils.logError(e);
        }
    }

    private void carregarProdutos() {
        listaProdutos.clear();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produto")) {
            while (rs.next()) {
                listaProdutos.add(new Produto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getString("lote"),
                    rs.getString("sku")
                ));
            }
            cmbProduto.setItems(listaProdutos);
        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Carregamento", "Não foi possível carregar os produtos.");
            AlertUtils.logError(e);
        }
    }

    @FXML
    public void registrarProducao() {
        Funcionario funcionario = cmbFuncionario.getValue();
        Maquina maquina = cmbMaquina.getValue();
        Produto produto = cmbProduto.getValue();
        String quantidadeTexto = txtQuantidade.getText().trim();

        if (funcionario == null || maquina == null || produto == null || quantidadeTexto.isEmpty()) {
            AlertUtils.showAlert(AlertType.AVISO, "Campos Incompletos", "Preencha todos os campos obrigatórios.");
            return;
        }

        try {
            int quantidade = Integer.parseInt(quantidadeTexto);

            try (Connection conn = Database.getConnection()) {
                String sql;
                PreparedStatement stmt;

                if (emEdicao && producaoEmEdicao != null) {
                    sql = "UPDATE producao SET funcionario_id = ?, maquina_id = ?, produto_id = ?, quantidade = ? WHERE id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, funcionario.getId());
                    stmt.setInt(2, maquina.getId());
                    stmt.setInt(3, produto.getId());
                    stmt.setInt(4, quantidade);
                    stmt.setInt(5, producaoEmEdicao.getId());

                    int linhasAfetadas = stmt.executeUpdate();
                    if (linhasAfetadas > 0) {
                        AlertUtils.showAlert(AlertType.SUCESSO, "Produção Atualizada", "Produção atualizada com sucesso!");
                    }
                } else {
                    sql = "INSERT INTO producao (funcionario_id, maquina_id, produto_id, quantidade, data_producao) VALUES (?, ?, ?, ?, ?)";
                    stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, funcionario.getId());
                    stmt.setInt(2, maquina.getId());
                    stmt.setInt(3, produto.getId());
                    stmt.setInt(4, quantidade);
                    stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                    int linhasAfetadas = stmt.executeUpdate();
                    if (linhasAfetadas > 0) {
                        AlertUtils.showAlert(AlertType.SUCESSO, "Produção Registrada", "Produção registrada com sucesso!");
                    }
                }

                carregarProducoes();
                limparCampos();
                emEdicao = false;
                producaoEmEdicao = null;

            } catch (SQLException e) {
                AlertUtils.showAlert(AlertType.ERRO, "Erro de Banco", "Não foi possível registrar/atualizar a produção.");
                AlertUtils.logError(e);
            }
        } catch (NumberFormatException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Quantidade Inválida", "Digite uma quantidade válida.");
        }
    }

    @FXML
    public void editarProducao() {
        Producao producaoSelecionada = tableProducao.getSelectionModel().getSelectedItem();

        if (producaoSelecionada == null) {
            AlertUtils.showAlert(AlertType.AVISO, "Seleção Necessária", "Selecione uma produção para editar.");
            return;
        }

        Funcionario funcionarioSelecionado = listaFuncionarios.stream()
            .filter(f -> f.getId() == producaoSelecionada.getFuncionarioId())
            .findFirst()
            .orElse(null);

        Maquina maquinaSelecionada = listaMaquinas.stream()
            .filter(m -> m.getId() == producaoSelecionada.getMaquinaId())
            .findFirst()
            .orElse(null);

        Produto produtoSelecionado = listaProdutos.stream()
            .filter(p -> p.getId() == producaoSelecionada.getProdutoId())
            .findFirst()
            .orElse(null);

        cmbFuncionario.setValue(funcionarioSelecionado);
        cmbMaquina.setValue(maquinaSelecionada);
        cmbProduto.setValue(produtoSelecionado);
        txtQuantidade.setText(String.valueOf(producaoSelecionada.getQuantidade()));

        this.emEdicao = true;
        this.producaoEmEdicao = producaoSelecionada;
    }

    @FXML
    public void excluirProducao() {
        Producao producaoSelecionada = tableProducao.getSelectionModel().getSelectedItem();

        if (producaoSelecionada == null) {
            AlertUtils.showAlert(AlertType.AVISO, "Seleção Necessária", "Selecione uma produção para excluir.");
            return;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM producao WHERE id = ?")) {

            stmt.setInt(1, producaoSelecionada.getId());
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                AlertUtils.showAlert(AlertType.SUCESSO, "Produção Excluída", "Produção excluída com sucesso!");
                carregarProducoes();
            }
        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Exclusão", "Não foi possível excluir a produção.");
            AlertUtils.logError(e);
        }
    }

    @FXML
    public void filtrarProducoes() {
        FilteredList<Producao> dadosFiltrados = new FilteredList<>(listaProducao, p -> true);

        dadosFiltrados.setPredicate(producao -> {
            String filtroTexto = filtroNome.getText().toLowerCase();

            if (filtroTexto.isEmpty()) return true;

            return producao.getNomeFuncionario().toLowerCase().contains(filtroTexto) ||
                   producao.getNomeMaquina().toLowerCase().contains(filtroTexto) ||
                   producao.getNomeProduto().toLowerCase().contains(filtroTexto);
        });

        tableProducao.setItems(dadosFiltrados);
    }

    private void carregarProducoes() {
        listaProducao.clear();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT p.*, " +
                 "f.nome AS funcionario_nome, " +
                 "m.nome AS maquina_nome, " +
                 "pr.nome AS produto_nome " +
                 "FROM producao p " +
                 "JOIN funcionario f ON p.funcionario_id = f.id " +
                 "JOIN maquina m ON p.maquina_id = m.id " +
                 "JOIN produto pr ON p.produto_id = pr.id"
             )) {
            while (rs.next()) {
                listaProducao.add(new Producao(
                    rs.getInt("id"),
                    rs.getInt("funcionario_id"),
                    rs.getInt("maquina_id"),
                    rs.getInt("produto_id"),
                    rs.getInt("quantidade"),
                    rs.getTimestamp("data_producao").toLocalDateTime(),
                    rs.getString("funcionario_nome"),
                    rs.getString("maquina_nome"),
                    rs.getString("produto_nome")
                ));
            }
            tableProducao.setItems(listaProducao);
        } catch (SQLException e) {
            AlertUtils.logError(e);
        }
    }

    private void limparCampos() {
        cmbFuncionario.setValue(null);
        cmbMaquina.setValue(null);
        cmbProduto.setValue(null);
        txtQuantidade.clear();
        emEdicao = false;
        producaoEmEdicao = null;
    }
}
