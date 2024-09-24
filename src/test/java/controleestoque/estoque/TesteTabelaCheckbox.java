package controleestoque.estoque;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TesteTabelaCheckbox extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Dados de teste
        ObservableList<Produto> produtos = FXCollections.observableArrayList(
                new Produto("Produto 1", false),
                new Produto("Produto 2", false),
                new Produto("Produto 3", false)
        );

        TableView<Produto> tableView = new TableView<>(produtos);
        tableView.setEditable(true);

        TableColumn<Produto, Boolean> colunaCheckBox = new TableColumn<>("Selecionar");
        colunaCheckBox.setCellValueFactory(cellData -> cellData.getValue().selecionadoProperty());
        colunaCheckBox.setCellFactory(column -> new CheckBoxTableCell<Produto, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().unbind();
                    checkBox.setSelected(item);

                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        checkBox.selectedProperty().bindBidirectional(getTableRow().getItem().selecionadoProperty());
                    }

                    checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                        if (getTableRow() != null && getTableRow().getItem() != null) {
                            getTableRow().getItem().setSelecionado(isSelected);
                        }
                    });

                    setGraphic(checkBox);
                }
            }
        });

        TableColumn<Produto, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));

        tableView.getColumns().addAll(colunaCheckBox, colunaNome);

        // Layout e cena
        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Teste de Tabela com CheckBox Modificado");
        primaryStage.show();
    }

    public static class Produto {
        private SimpleStringProperty nome;
        private BooleanProperty selecionado;

        public Produto(String nome, boolean selecionado) {
            this.nome = new SimpleStringProperty(nome);
            this.selecionado = new SimpleBooleanProperty(selecionado);
        }

        public String getNome() {
            return nome.get();
        }

        public BooleanProperty selecionadoProperty() {
            return selecionado;
        }

        public boolean isSelecionado() {
            return selecionado.get();
        }

        public void setSelecionado(boolean selecionado) {
            this.selecionado.set(selecionado);
        }
    }
}
