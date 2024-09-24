package com.controleestoque.servico;

import com.controleestoque.estoque.Produto;
import com.controleestoque.util.AlertaUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;
/**
 * Classe responsável por gerenciar os filtros de pesquisa da tabela.
 */
public class GerenciadorFiltroProduto {

    private ServicoProduto servicoProduto;
    private GerenciadorDadosProduto gerenciadorDadosProduto;
    private TextField campoPesquisa;
    private TextField campoQuantidadeMinima;
    private TextField campoQuantidadeMaxima;
    private ComboBox<String> filtroCategoria;
    private ComboBox<String> filtroLocalizacao;
    private ComboBox<String> filtroCaixa;
    private ObservableList<Produto> produtosData;
    private TableView<Produto> tabela;

    /**
     * Construtor que inicializa o gerenciador de filtros e configura os eventos nos campos de entrada.
     *
     * @param servicoProduto           O serviço de produto para operações de CRUD.
     * @param gerenciadorDadosProduto  O gerenciador de dados para carregamento e manipulação de dados na tabela.
     * @param campoPesquisa            O campo de texto para pesquisa de produtos.
     * @param campoQuantidadeMinima    O campo de texto para a quantidade mínima.
     * @param campoQuantidadeMaxima    O campo de texto para a quantidade máxima.
     * @param filtroCategoria          O comboBox para seleção de categoria.
     * @param filtroLocalizacao        O comboBox para seleção de localização.
     * @param filtroCaixa              O comboBox para seleção de caixa.
     * @param produtosData             A Lista observável de produtos exibidos na tabela.
     * @param tabela                   A tabela.
     */
    public GerenciadorFiltroProduto(ServicoProduto servicoProduto, GerenciadorDadosProduto gerenciadorDadosProduto,
                                    TextField campoPesquisa, TextField campoQuantidadeMinima,
                                    TextField campoQuantidadeMaxima, ComboBox<String> filtroCategoria,
                                    ComboBox<String> filtroLocalizacao, ComboBox<String> filtroCaixa,
                                    ObservableList<Produto> produtosData, TableView<Produto> tabela) {
        this.servicoProduto = servicoProduto;
        this.gerenciadorDadosProduto = gerenciadorDadosProduto;
        this.campoPesquisa = campoPesquisa;
        this.campoQuantidadeMinima = campoQuantidadeMinima;
        this.campoQuantidadeMaxima = campoQuantidadeMaxima;
        this.filtroCategoria = filtroCategoria;
        this.filtroLocalizacao = filtroLocalizacao;
        this.filtroCaixa = filtroCaixa;
        this.produtosData = produtosData;
        this.tabela = tabela;

        configurarFiltros(); 
    }

    /**
     * Configura os eventos para os campos de filtro.
     */
    private void configurarFiltros() {

        campoPesquisa.textProperty().addListener((observavelTexto, textoAntigo, textoNovo) -> aplicarFiltros());
        campoQuantidadeMinima.setOnKeyReleased(evento -> aplicarFiltros());
        campoQuantidadeMaxima.setOnKeyReleased(evento -> aplicarFiltros());
        filtroCategoria.setOnAction(evento -> aplicarFiltros());
        filtroLocalizacao.setOnAction(evento -> aplicarFiltros());
        filtroCaixa.setOnAction(evento -> aplicarFiltros());
    }

    /**
     * Aplica os filtros de acordo com os valores fornecidos nos campos e ComboBoxes,
     */
    public void aplicarFiltros() {

        Platform.runLater(() -> {
            try {
                String termo = campoPesquisa.getText();
                String categoria = filtroCategoria.getSelectionModel().getSelectedItem();
                String localizacao = filtroLocalizacao.getSelectionModel().getSelectedItem();
                String caixa = filtroCaixa.getSelectionModel().getSelectedItem();
                Integer quantidadeMin = !campoQuantidadeMinima.getText().isEmpty()
                        ? Integer.parseInt(campoQuantidadeMinima.getText())
                        : null;
                Integer quantidadeMax = !campoQuantidadeMaxima.getText().isEmpty()
                        ? Integer.parseInt(campoQuantidadeMaxima.getText())
                        : null;

                List<Produto> produtosFiltrados = servicoProduto.buscarProdutosComFiltros(termo, categoria, localizacao,
                        caixa, quantidadeMin, quantidadeMax);

                produtosData.clear();
                produtosData.addAll(produtosFiltrados);
                tabela.refresh();

            } catch (NumberFormatException e) {
                AlertaUtils.mostrarAlertaErro("Erro de Validação!", "Quantidade mínima ou máxima inválida.");
            } catch (SQLException e) {
                AlertaUtils.mostrarAlertaErro("Erro!", "Erro ao aplicar filtros: " + e.getMessage());
            }
        });
    }

    /**
     * Atualiza as opções dos filtros de categoria, localização e caixa.
     *
     * @throws SQLException Se ocorrer um erro ao atualizar os filtros.
     */
    public void atualizarFiltros() throws SQLException {

        filtroCategoria.getItems().setAll(servicoProduto.getTodasCategorias());
        filtroLocalizacao.getItems().setAll(servicoProduto.getTodasLocalizacoes());
        filtroCaixa.getItems().setAll(servicoProduto.getTodasCaixas());
    }

    /**
     * Atualiza a tabela com todos os dados sem filtragem.
     *
     * @throws SQLException Se ocorrer um erro ao limpar os filtros.
     */
    public void limparFiltros() throws SQLException {

        filtroCategoria.getSelectionModel().clearSelection();
        filtroLocalizacao.getSelectionModel().clearSelection();
        filtroCaixa.getSelectionModel().clearSelection();
        campoPesquisa.clear();
        campoQuantidadeMinima.clear();
        campoQuantidadeMaxima.clear();

        atualizarFiltros();
        aplicarFiltros();
    }
}
