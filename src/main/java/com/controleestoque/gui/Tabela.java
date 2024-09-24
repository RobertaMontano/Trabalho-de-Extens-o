package com.controleestoque.gui;

import java.sql.SQLException;

import com.controleestoque.estoque.Produto;
import com.controleestoque.servico.GerenciadorDadosProduto;
import com.controleestoque.servico.GerenciadorEventoTabela;
import com.controleestoque.servico.GerenciadorFiltroProduto;
import com.controleestoque.servico.ServicoProduto;
import com.controleestoque.util.AlertaUtils;
import com.controleestoque.util.EstiloUI;
import com.controleestoque.util.ComboBoxPromptCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Classe responsável pela criação da tabela.
 */
public class Tabela {

    private ServicoProduto servicoProduto;
    private TableView<Produto> tabela;
    private VBox tabelaContainer;
    private ObservableList<Produto> produtosData;
    private ObservableList<Produto> produtosSelecionados;
    private GerenciadorDadosProduto gerenciadorDadosProduto;
    private GerenciadorFiltroProduto gerenciadorFiltroProduto;
    private GerenciadorEventoTabela gerenciadorEventoTabela;
    private CheckBox CheckBox;

    // Filtros
    private TextField campoPesquisa;
    private TextField campoQuantidadeMinima;
    private TextField campoQuantidadeMaxima;
    private ComboBox<String> filtroCategoria;
    private ComboBox<String> filtroLocalizacao;
    private ComboBox<String> filtroCaixa;
    private Button aplicarFiltrosButton;
    private Button limparFiltrosButton;

    /**
     * Construtor para inicializar a tabela e seus componentes.
     *
     * @param servicoProduto O serviço para operações relacionadas aos produtos.
     * @throws SQLException Em caso de erro ao carregar dados.
     */
    public Tabela(ServicoProduto servicoProduto) throws SQLException {

        this.servicoProduto = servicoProduto;
        this.produtosData = FXCollections.observableArrayList();
        this.produtosSelecionados = FXCollections.observableArrayList();
        this.tabela = new TableView<>();
        this.gerenciadorDadosProduto = new GerenciadorDadosProduto(servicoProduto);
        this.CheckBox = new CheckBox("");
        this.gerenciadorEventoTabela = new GerenciadorEventoTabela(tabela, CheckBox);

        campoPesquisa = new TextField();
        campoQuantidadeMinima = new TextField();
        campoQuantidadeMaxima = new TextField();
        filtroCategoria = new ComboBox<>();
        filtroLocalizacao = new ComboBox<>();
        filtroCaixa = new ComboBox<>();
        aplicarFiltrosButton = new Button("Aplicar Filtros");
        limparFiltrosButton = new Button("Limpar Filtros");

        filtroCategoria.getItems().addAll(servicoProduto.getTodasCategorias());
        filtroLocalizacao.getItems().addAll(servicoProduto.getTodasLocalizacoes());
        filtroCaixa.getItems().addAll(servicoProduto.getTodasCaixas());

        //Prompt dos comboBox
        filtroCategoria.setButtonCell(new ComboBoxPromptCell<>("Categoria"));
        filtroLocalizacao.setButtonCell(new ComboBoxPromptCell<>("Localização"));
        filtroCaixa.setButtonCell(new ComboBoxPromptCell<>("Caixa"));

        //Layout do container da tabela
        tabelaContainer = new VBox(10, configurarLayoutFiltros(), tabela);
        tabelaContainer.setAlignment(Pos.CENTER);
        tabelaContainer.setPadding(new Insets(10));
        tabelaContainer.setSpacing(10);

        gerenciadorFiltroProduto = new GerenciadorFiltroProduto(servicoProduto,
                gerenciadorDadosProduto, campoPesquisa, campoQuantidadeMinima, campoQuantidadeMaxima, filtroCategoria,
                filtroLocalizacao, filtroCaixa, produtosData, tabela);

        configurarTabela();
        carregarDadosIniciais();

        gerenciadorEventoTabela.configurarEventos();
    }

    /**
     * Configura as colunas e o layout da tabela.
     */
    private void configurarTabela() {

        tabela.setPrefSize(1000, 750);
        tabela.setMaxWidth(1000);
        tabela.setMinWidth(600);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tabela.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        tabela.getStylesheets().add(getClass().getResource("/css/estilo.css").toExternalForm()); // css da tabela

        // Check Box
        TableColumn<Produto, Boolean> colunaCheckBox = new TableColumn<>();
        colunaCheckBox.setGraphic(CheckBox);
        colunaCheckBox.setCellValueFactory(cellData -> {
            Produto produto = cellData.getValue();
            return produto.selecionadoProperty();
        });
        colunaCheckBox.setSortable(false);

        gerenciadorEventoTabela.configurarCelulaCheckBox(colunaCheckBox);
        colunaCheckBox.setPrefWidth(50);

        //Definição das colunas da tabela
        TableColumn<Produto, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(nomeData -> new javafx.beans.property.SimpleStringProperty
                (nomeData.getValue().getNome()));
        colunaNome.setPrefWidth(150);

        TableColumn<Produto, String> colunaQuantidade = new TableColumn<>("Quantidade");
        colunaQuantidade.setCellValueFactory(quantidadeData -> new javafx.beans.property.SimpleStringProperty
                (String.valueOf(quantidadeData.getValue().getQuantidade())));
        colunaQuantidade.setPrefWidth(150);
        colunaQuantidade.setStyle("-fx-alignment: CENTER-RIGHT;");


        TableColumn<Produto, String> colunaCategoria = new TableColumn<>("Categoria");
        colunaCategoria.setCellValueFactory(categoriaData -> {
            String categoria = categoriaData.getValue().getCategoria();
            return new javafx.beans.property.SimpleStringProperty(
                    (categoria != null && !categoria.trim().isEmpty()) ? categoria : "Sem Categoria"
            );
        });
        colunaCategoria.setPrefWidth(150);
        colunaCategoria.setStyle("-fx-alignment: CENTER;");

        TableColumn<Produto, String> colunaCaixa = new TableColumn<>("Caixa");
        colunaCaixa.setCellValueFactory(caixaData -> {
            String caixa = caixaData.getValue().getCaixaNome();
            return new javafx.beans.property.SimpleStringProperty(
                    (caixa != null && !caixa.trim().isEmpty()) ? caixa : "Sem Caixa"
            );
        });
        colunaCaixa.setPrefWidth(150);
        colunaCaixa.setStyle("-fx-alignment: CENTER;");

        TableColumn<Produto, String> colunaLocalizacao = new TableColumn<>("Localização");
        colunaLocalizacao.setCellValueFactory(caixaData -> {
            String localizacao = caixaData.getValue().getLocalizacao();
            return new javafx.beans.property.SimpleStringProperty(
                    (localizacao != null && !localizacao.trim().isEmpty()) ? localizacao : "Sem Localização"
            );
        });
        colunaLocalizacao.setPrefWidth(150);
        colunaLocalizacao.setStyle("-fx-alignment: CENTER;");

        //Ajuste da posição das colunas
        tabela.getColumns().addAll(colunaCheckBox, colunaNome, colunaQuantidade, colunaCategoria, colunaCaixa,
                colunaLocalizacao);
    }

    /**
     * Carrega os dados iniciais na tabela.
     */
    private void carregarDadosIniciais() {

        try {
            gerenciadorDadosProduto.carregarDados(produtosData, tabela);
        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtils.mostrarAlertaErro("Erro ao carregar dados iniciais", e.getMessage());
        }
    }

    /**
     * Configura o layout dos filtros.
     *
     * @return Container com os componentes de filtro.
     */
    private HBox configurarLayoutFiltros() {

        int alturaComponente = 30;
        //Definição dos campos
        campoPesquisa.setPromptText("Buscar...");
        campoPesquisa.setPrefWidth(135);
        campoPesquisa.setPrefHeight(alturaComponente);

        campoQuantidadeMinima.setPromptText("Qtd Min");
        campoQuantidadeMinima.setPrefWidth(70);
        campoQuantidadeMinima.setPrefHeight(alturaComponente);

        campoQuantidadeMaxima.setPromptText("Qtd Max");
        campoQuantidadeMaxima.setPrefWidth(70);
        campoQuantidadeMaxima.setPrefHeight(alturaComponente);

        filtroCategoria.setPromptText("Categoria");
        filtroCategoria.setPrefWidth(130);
        filtroCategoria.setVisibleRowCount(4);
        filtroCategoria.setPrefHeight(alturaComponente);

        filtroLocalizacao.setPromptText("Localização");
        filtroLocalizacao.setPrefWidth(130);
        filtroLocalizacao.setVisibleRowCount(4);
        filtroLocalizacao.setPrefHeight(alturaComponente);

        filtroCaixa.setPromptText("Caixa");
        filtroCaixa.setPrefWidth(130);
        filtroCaixa.setVisibleRowCount(4);
        filtroCaixa.setPrefHeight(alturaComponente);

        aplicarFiltrosButton.setPrefWidth(105);
        aplicarFiltrosButton.setPrefHeight(alturaComponente);

        limparFiltrosButton.setPrefWidth(105);
        limparFiltrosButton.setPrefHeight(alturaComponente);

        //Aplicação dos estilos dos filtros
        EstiloUI.aplicarEstiloBotao(aplicarFiltrosButton);
        EstiloUI.aplicarEstiloBotao(limparFiltrosButton);
        EstiloUI.aplicarEstiloCampo(campoPesquisa);
        EstiloUI.aplicarEstiloCampo(campoQuantidadeMinima);
        EstiloUI.aplicarEstiloCampo(campoQuantidadeMaxima);
        EstiloUI.aplicarEstiloComboBox(filtroCategoria);
        EstiloUI.aplicarEstiloComboBox(filtroLocalizacao);
        EstiloUI.aplicarEstiloComboBox(filtroCaixa);

        //Container dos filtros
        HBox filtrosContainer = new HBox(10, campoPesquisa, campoQuantidadeMinima,
                campoQuantidadeMaxima, filtroCategoria,filtroCaixa, filtroLocalizacao,
                aplicarFiltrosButton, limparFiltrosButton);

        filtrosContainer.setAlignment(Pos.CENTER);
        filtrosContainer.setPadding(new Insets(10));

        return filtrosContainer;
    }


    //Getters da tabela

    public VBox getTabelaContainer() {
        return tabelaContainer;
    }

    public TableView<Produto> getTabela() {
        return tabela;
    }

    public ObservableList<Produto> getProdutosData() {
        return produtosData;
    }

    public Button getBtnAplicarFiltros() {
        return aplicarFiltrosButton;
    }

    public Button getBtnLimparFiltros() {
        return limparFiltrosButton;
    }

    public TextField getCampoPesquisa() {
        return campoPesquisa;
    }

    public TextField getCampoQuantidadeMinima() {
        return campoQuantidadeMinima;
    }

    public TextField getCampoQuantidadeMaxima() {
        return campoQuantidadeMaxima;
    }

    public ComboBox<String> getFiltroCategoria() {
        return filtroCategoria;
    }

    public ComboBox<String> getFiltroLocalizacao() {
        return filtroLocalizacao;
    }

    public ComboBox<String> getFiltroCaixa() {
        return filtroCaixa;
    }

    public void atualizarTabela() {
        gerenciadorDadosProduto.carregarDados(produtosData, tabela);
    }

    public void atualizarFiltros() throws SQLException {
        gerenciadorFiltroProduto.atualizarFiltros();
    }

    public GerenciadorFiltroProduto getFiltroProdutoHelper() {
        return gerenciadorFiltroProduto;
    }
}