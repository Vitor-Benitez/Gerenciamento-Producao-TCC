package com.example.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.database.Database;
import com.example.models.Funcionario;
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

public class FuncionarioController {

    @FXML private TextField txtFuncionarioNome;
    @FXML private ComboBox<String> cmbFuncionarioSetor;
    @FXML private TextField filtroNome;
    @FXML private TableView<Funcionario> tableFuncionarios;
    @FXML private TableColumn<Funcionario, Integer> colFuncionarioId;
    @FXML private TableColumn<Funcionario, String> colFuncionarioNome;
    @FXML private TableColumn<Funcionario, String> colFuncionarioSetor;

    private ObservableList<Funcionario> listaFuncionarios = FXCollections.observableArrayList();
    
    // Variáveis de controle de edição
    private boolean emEdicao = false;
    private Funcionario funcionarioEmEdicao = null;

    @FXML
    public void initialize() {
        // Configurar combobox de setor
        cmbFuncionarioSetor.setItems(FXCollections.observableArrayList(
            "Produção", 
            "Manutenção", 
            "Logística", 
            "Administrativo", 
            "Recursos Humanos"
        ));

        // Configurar colunas da tabela
        configurarColunas();

        // Carregar funcionários
        carregarFuncionarios();
    }

    private void configurarColunas() {
        colFuncionarioId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFuncionarioNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colFuncionarioSetor.setCellValueFactory(new PropertyValueFactory<>("setor"));
    }

    @FXML
    public void salvarFuncionario() {
        String nome = txtFuncionarioNome.getText().trim();
        String setor = cmbFuncionarioSetor.getValue();

        if (nome.isEmpty() || setor == null) {
            AlertUtils.showAlert(AlertType.AVISO, "Campos Incompletos", "Preencha todos os campos.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if (emEdicao && funcionarioEmEdicao != null) {
                // Lógica de ATUALIZAÇÃO
                sql = "UPDATE funcionario SET nome = ?, setor = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, setor);
                stmt.setInt(3, funcionarioEmEdicao.getId());

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    AlertUtils.showAlert(AlertType.SUCESSO, "Sucesso", "Funcionário atualizado com sucesso!");
                }
            } else {
                // Lógica de INSERÇÃO
                sql = "INSERT INTO funcionario (nome, setor) VALUES (?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, nome);
                stmt.setString(2, setor);

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    AlertUtils.showAlert(AlertType.SUCESSO, "Sucesso", "Funcionário cadastrado com sucesso!");
                }
            }

            // Recarregar a lista de funcionários
            carregarFuncionarios();
            
            // Limpar campos
            limparCampos();

        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Banco", "Não foi possível salvar/atualizar o funcionário.");
            AlertUtils.logError(e);
        }
    }

    @FXML
    public void editarFuncionario() {
        // Recupera o funcionário selecionado na tabela
        Funcionario funcionarioSelecionado = tableFuncionarios.getSelectionModel().getSelectedItem();
        
        if (funcionarioSelecionado == null) {
            AlertUtils.showAlert(AlertType.AVISO, "Seleção Necessária", "Selecione um funcionário para editar.");
            return;
        }

        // Preenche os campos com os dados do funcionário selecionado
        txtFuncionarioNome.setText(funcionarioSelecionado.getNome());
        cmbFuncionarioSetor.setValue(funcionarioSelecionado.getSetor());

        // Configura modo de edição
        this.emEdicao = true;
        this.funcionarioEmEdicao = funcionarioSelecionado;
    }

    @FXML
    public void excluirFuncionario() {
        Funcionario funcionarioSelecionado = tableFuncionarios.getSelectionModel().getSelectedItem();
        
        if (funcionarioSelecionado == null) {
            AlertUtils.showAlert(AlertType.AVISO, "Seleção Necessária", "Selecione um funcionário para excluir.");
            return;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM funcionario WHERE id = ?")) {

            stmt.setInt(1, funcionarioSelecionado.getId());
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                AlertUtils.showAlert(AlertType.SUCESSO, "Funcionário Excluído", "Funcionário excluído com sucesso!");
                carregarFuncionarios();
            }
        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Exclusão", "Não foi possível excluir o funcionário.");
            AlertUtils.logError(e);
        }
    }

    @FXML
    public void filtrarFuncionarios() {
        FilteredList<Funcionario> dadosFiltrados = new FilteredList<>(listaFuncionarios, p -> true);

        dadosFiltrados.setPredicate(funcionario -> {
            String filtroTexto = filtroNome.getText().toLowerCase();

            if (filtroTexto.isEmpty()) return true;

            return funcionario.getNome().toLowerCase().contains(filtroTexto) ||
                   funcionario.getSetor().toLowerCase().contains(filtroTexto);
        });

        tableFuncionarios.setItems(dadosFiltrados);
    }

    @FXML
    public void limparFiltro() {
        filtroNome.clear();
        tableFuncionarios.setItems(listaFuncionarios);
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

            tableFuncionarios.setItems(listaFuncionarios);
        } catch (SQLException e) {
            AlertUtils.showAlert(AlertType.ERRO, "Erro de Carregamento", "Não foi possível carregar os funcionários.");
            AlertUtils.logError(e);
        }
    }

    private void limparCampos() {
        txtFuncionarioNome.clear();
        cmbFuncionarioSetor.setValue(null);
        emEdicao = false;
        funcionarioEmEdicao = null;
    }
}
