package controleestoque.estoque;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TabelaTeste extends Application {

    private TableView<Produto> tableView;
    private ObservableList<Produto> produtosData;
    private CheckBox selectAllCheckBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Teste de CheckBox na Tabela");

        produtosData = FXCollections.observableArrayList(
                new Produto("Produto 1"),
                new Produto("Produto 2"),
                new Produto("Produto 3")
        );

        tableView = new TableView<>();
        configurarTabela();

        Button btnRemover = new Button("Remover Selecionados");
        btnRemover.setOnAction(event -> removerSelecionados());

        HBox hboxBotoes = new HBox(10, btnRemover);
        hboxBotoes.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, tableView, hboxBotoes);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void configurarTabela() {
        TableColumn<Produto, Boolean> colunaSelecionar = new TableColumn<>();
        selectAllCheckBox = new CheckBox();
        colunaSelecionar.setGraphic(selectAllCheckBox);

        colunaSelecionar.setCellValueFactory(cellData -> cellData.getValue().selecionadoProperty());
        colunaSelecionar.setCellFactory(CheckBoxTableCell.forTableColumn(colunaSelecionar));
        colunaSelecionar.setPrefWidth(50);

        selectAllCheckBox.setOnAction(event -> {
            boolean selecionarTodos = selectAllCheckBox.isSelected();
            for (Produto produto : produtosData) {
                produto.setSelecionado(selecionarTodos);
            }
            tableView.refresh();
        });

        TableColumn<Produto, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colunaNome.setPrefWidth(200);

        tableView.getColumns().addAll(colunaSelecionar, colunaNome);
        tableView.setItems(produtosData);

        tableView.setRowFactory(tv -> {
            TableRow<Produto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Produto produto = row.getItem();
                    produto.setSelecionado(!produto.isSelecionado());
                    tableView.refresh();
                }
            });

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && newItem.isSelecionado()) {
                    row.setStyle("-fx-background-color: lightblue;");
                } else {
                    row.setStyle("");
                }
            });

            return row;
        });
    }

    private void removerSelecionados() {
        ObservableList<Produto> produtosSelecionados = FXCollections.observableArrayList();
        for (Produto produto : produtosData) {
            if (produto.isSelecionado()) {
                produtosSelecionados.add(produto);
            }
        }
        produtosData.removeAll(produtosSelecionados);
    }

    public static class Produto {
        private final SimpleStringProperty nome;
        private final BooleanProperty selecionado;

        public Produto(String nome) {
            this.nome = new SimpleStringProperty(nome);
            this.selecionado = new SimpleBooleanProperty(false);
        }

        public String getNome() {
            return nome.get();
        }

        public boolean isSelecionado() {
            return selecionado.get();
        }

        public void setSelecionado(boolean selecionado) {
            this.selecionado.set(selecionado);
        }

        public BooleanProperty selecionadoProperty() {
            return selecionado;
        }
    }
}
