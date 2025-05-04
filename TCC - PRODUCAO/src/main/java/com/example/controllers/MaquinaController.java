package com.example.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.database.Database;
import com.example.models.Maquina;
import com.example.utils.AlertType;
import com.example.utils.AlertUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MaquinaController {

    @FXML private TextField txtMaquinaNome;
    @FXML private ComboBox<String> cmbMaquinaSetor;
    @FXML private TextArea txtMaquinaDescricao;
    @FXML private TextField filtroNome;
    @FXML private TableView<Maquina> tableMaquinas;
    @FXML private TableColumn<Maquina, Integer> colMaquinaId;
    @FXML private TableColumn<Maquina, String> colMaquinaNome;
    @FXML private TableColumn<Maquina, String> colMaquinaSetor;
    @FXML private TableColumn<Maquina, String> colMaquinaDescricao;

    private ObservableList<Maquina> listaMaquinas = FXCollections.observableArrayList();
    
    private boolean emEdicao = false;
    private Maquina maquinaEmEdicao = null;

    @FXML
    public void initialize() {
        // Configurar combobox de setor
        cmbMaquinaSetor.setItems(FXCollections.observableArrayList(
            "Produção", 
            "Manutenção", 
            "Logística", 
            "Administrativo", 
            "Almoxarifado"
        ));

        // Configurar colunas da tabela
        colMaquinaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMaquinaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colMaquinaSetor.setCellValueFactory(new PropertyValueFactory<>("setor"));
        colMaquinaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        // Carregar máquinas
        carregarMaquinas();
    }

    @FXML
    public void salvarMaquina() {
        String nome = txtMaquinaNome.getText().trim();
        String setor = cmbMaquinaSetor.getValue();
        String descricao = txtMaquinaDescricao.getText().trim();

        if (nome.isEmpty() || setor == null) {
            AlertUtils.showAlert(AlertType.AVISO, "Campos Incompletos", "Preencha todos os campos obrigatórios.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if (emEdicao && maquinaEmEdicao != null) {
                // Lógica de ATUALIZAÇÃO
                sql = "UPDATE maquina SET nome = ?, setor = ?, descricao = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, setor);
                stmt.setString(3, descricao);
                stmt.setInt(4, maquinaEmEdicao.getId());

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    AlertUtils.showAlert(AlertType.SUCESSO, "Sucesso", "Máquina atualizada com sucesso!");
                }
            } else {
                // Lógica de INSERÇÃO
                sql = "INSERT INTO maquina (nome, setor, descricao) VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, nome);
                stmt.setString(2, setor);
                stmt.setString(3, descricao);

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    AlertUtils.showAlert(AlertType.SUCESSO, "Sucesso", "Máquina cadastrada com sucesso!");
                }
            }

            // Recarregar a lista de máquinas
            carregarMaquinas();
            
            // Limpar campos
            limparCampos();

        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Banco", "Não foi possível salvar/atualizar a máquina.");
            e.printStackTrace();
        }
    }

    @FXML
    public void editarMaquina() {
        Maquina maquinaSelecionada = tableMaquinas.getSelectionModel().getSelectedItem();
        
        if (maquinaSelecionada == null) {
            AlertUtils.showAlert(AlertType.AVISO, "Seleção Necessária", "Selecione uma máquina para editar.");
            return;
        }

        txtMaquinaNome.setText(maquinaSelecionada.getNome());
        cmbMaquinaSetor.setValue(maquinaSelecionada.getSetor());
        txtMaquinaDescricao.setText(maquinaSelecionada.getDescricao());

        this.emEdicao = true;
        this.maquinaEmEdicao = maquinaSelecionada;
    }

    @FXML
    public void excluirMaquina() {
        Maquina maquinaSelecionada = tableMaquinas.getSelectionModel().getSelectedItem();
        
        if (maquinaSelecionada == null) {
            AlertUtils.showAlert(AlertType.AVISO, "Seleção Necessária", "Selecione uma máquina para excluir.");
            return;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM maquina WHERE id = ?")) {

            stmt.setInt(1, maquinaSelecionada.getId());
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                AlertUtils.showAlert(AlertType.SUCESSO, "Máquina Excluída", "Máquina excluída com sucesso!");
                carregarMaquinas();
            }
        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Exclusão", "Não foi possível excluir a máquina.");
            e.printStackTrace();
        }
    }

    @FXML
    public void filtrarMaquinas() {
        FilteredList<Maquina> dadosFiltrados = new FilteredList<>(listaMaquinas, p -> true);

        dadosFiltrados.setPredicate(maquina -> {
            String filtroTexto = filtroNome.getText().toLowerCase();

            if (filtroTexto.isEmpty()) return true;

            return maquina.getNome().toLowerCase().contains(filtroTexto) ||
                   maquina.getSetor().toLowerCase().contains(filtroTexto) ||
                   maquina.getDescricao().toLowerCase().contains(filtroTexto);
        });

        tableMaquinas.setItems(dadosFiltrados);
    }

    @FXML
    public void limparFiltro() {
        filtroNome.clear();
        tableMaquinas.setItems(listaMaquinas);
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

            tableMaquinas.setItems(listaMaquinas);
        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Carregamento", "Não foi possível carregar as máquinas.");
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        txtMaquinaNome.clear();
        cmbMaquinaSetor.setValue(null);
        txtMaquinaDescricao.clear();
        emEdicao = false;
        maquinaEmEdicao = null;
    }
}
