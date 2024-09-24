package com.controleestoque.servico;

import com.controleestoque.estoque.Produto;
import com.controleestoque.util.EstiloUI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Classe responsável por gerenciar eventos e interações na tabela de produtos.
 */
public class GerenciadorEventoTabela {

    private TableView<Produto> tabela;
    private CheckBox CheckBox;

    /**
     * Construtor que inicializa o gerenciador com a tabela e o checkbox de seleção.
     *
     * @param tabela  A tabela de produtos.
     * @param CheckBox  O CheckBox para seleção global.
     */
    public GerenciadorEventoTabela(TableView<Produto> tabela, CheckBox CheckBox) {
        this.tabela = tabela;
        this.CheckBox = CheckBox;
    }

    /**
     * Configura todos os eventos da tabela.
     */
    public void configurarEventos() {
        configurarCliqueDuplo();
        configurarSelecaoTodos();
        configurarCheckBox();
        configurarCliqueLinha();
    }

    /**
     * Configura evento de clique duplo nas linhas da tabela para mostrar os detalhes do produto.
     */
    private void configurarCliqueDuplo() {

        tabela.setRowFactory(tb -> {
            TableRow<Produto> row = new TableRow<>();
            row.setOnMouseClicked(evento -> {
                if (evento.getClickCount() == 2 && (!row.isEmpty())) {
                    Produto produtoSelecionado = row.getItem();
                    mostrarDetalhesProduto(produtoSelecionado);
                }
            });
            return row;
        });
    }

    /**
     * Exibe uma janela com os detalhes do produto clicado.
     *
     * @param produtoSelecionado O produto a qual os detalhes serão exibidos.
     */
    private void mostrarDetalhesProduto(Produto produtoSelecionado) {

        Stage detalhesStage = new Stage();
        detalhesStage.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label nomeLabel = new Label("Nome: " + produtoSelecionado.getNome());
        Label quantidadeLabel = new Label("Quantidade: " + produtoSelecionado.getQuantidade());
        Label categoriaLabel = new Label("Categoria: " + produtoSelecionado.getCategoria());
        Label caixaLabel = new Label("Caixa: " + (produtoSelecionado.getCaixaNome() != null ? produtoSelecionado.getCaixaNome() : "Sem Caixa"));
        Label localizacaoLabel = new Label("Localização: " + (produtoSelecionado.getLocalizacao() != null ? produtoSelecionado.getLocalizacao() : "Sem Localização"));

        Button fecharButton = new Button("Fechar");
        fecharButton.setOnAction(evento -> detalhesStage.close());

        vbox.getChildren().addAll(nomeLabel, quantidadeLabel, categoriaLabel, caixaLabel, localizacaoLabel, fecharButton);

        Scene scene = new Scene(vbox, 400, 300);
        detalhesStage.setScene(scene);
        detalhesStage.setTitle("Detalhes do Produto");
        detalhesStage.show();
    }

    /**
     * Configura a lógica para selecionar todos os produtos ou desmarcá-los quando o CheckBox global é alterado.
     */
    private void configurarSelecaoTodos() {
        ObservableList<Produto> produtosData = tabela.getItems();
        ObservableList<Produto> produtosSelecionados = tabela.getSelectionModel().getSelectedItems();

        produtosSelecionados.addListener((ListChangeListener<Produto>) eventoLista -> {
            while (eventoLista.next()) {
                if (eventoLista.wasAdded()) {
                    for (Produto produto : eventoLista.getAddedSubList()) {
                        produto.setSelecionado(true);
                    }
                } else if (eventoLista.wasRemoved()) {
                    for (Produto produto : eventoLista.getRemoved()) {
                        produto.setSelecionado(false);
                    }
                }
            }

            tabela.refresh();
            atualizarCheckBoxGlobal();
        });
    }

    /**
     * Configura o comportamento do CheckBox global.
     */
    private void configurarCheckBox() {
        CheckBox.setOnAction(event -> {
            boolean selecionarTodos = CheckBox.isSelected();
            ObservableList<Produto> produtosData = tabela.getItems();

            for (Produto produto : produtosData) {
                produto.setSelecionado(selecionarTodos);
            }

            tabela.refresh();
        });

        tabela.getItems().addListener((ListChangeListener<Produto>) eventoLista -> {
            while (eventoLista.next()) {
                atualizarCheckBoxGlobal();
            }
        });
    }

    /**
     * Atualiza o estado do CheckBox global com base na seleção atual dos produtos.
     */
    private void atualizarCheckBoxGlobal() {
        ObservableList<Produto> produtosData = tabela.getItems();
        boolean todosSelecionados = produtosData.stream().allMatch(Produto::isSelecionado);

        CheckBox.setSelected(todosSelecionados);
    }

    /**
     * Configura a renderização para a coluna de CheckBox na tabela.
     *
     * @param colunaCheckBox A coluna de CheckBox da tabela.
     */
    public void configurarCelulaCheckBox(TableColumn<Produto, Boolean> colunaCheckBox) {

        colunaCheckBox.setCellFactory(coluna -> new TableCell<Produto, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            private final HBox container = new HBox(checkBox);

            {
                container.setStyle("-fx-alignment: center;");
                container.setPadding(new Insets(5));
                container.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    checkBox.selectedProperty().unbind();
                    Produto produto = (Produto) getTableRow().getItem();
                    checkBox.setSelected(produto.isSelecionado());
                    checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        produto.setSelecionado(isNowSelected);
                        EstiloUI.atualizarEstiloLinhaSelecionada(getTableRow(), produto);
                    });
                    setGraphic(container);
                    EstiloUI.atualizarEstiloLinhaSelecionada(getTableRow(), produto);
                }
            }
        });
    }

    /**
     * Configura evento de clique simples para alterar a seleção do produto na tabela.
     */
    private void configurarCliqueLinha() {
        tabela.setRowFactory(tv -> {
            TableRow<Produto> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    Produto produto = row.getItem();
                    produto.setSelecionado(!produto.isSelecionado());

                    EstiloUI.atualizarEstiloLinhaSelecionada(row, produto);

                    if (!produto.isSelecionado()) {
                        tabela.getSelectionModel().clearSelection(tabela.getItems().indexOf(produto));
                    }
                    tabela.refresh();
                }
            });

            return row;
        });
    }
}
