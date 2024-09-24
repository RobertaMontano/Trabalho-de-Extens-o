package com.controleestoque.util;

import com.controleestoque.estoque.Produto;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;

import javafx.util.Duration;
/**
 * Classe utilitária para aplicação de estilos visuais em componentes da interface gráfica.
 * Fornece métodos para estilizar botões, campos de texto, ComboBox e linhas da tabela.
 */
public class EstiloUI {

	// Estilo dos filtros
	public static void aplicarEstiloBotao(Button botao) {
		botao.setStyle("-fx-background-color: #6A5ACD	;" + "-fx-text-fill: white;" + "-fx-padding: 8px 16px;"
				+ "-fx-border-radius: 10px;" + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0, 0);"
				+ "-fx-cursor: hand;");

		botao.setOnMouseEntered(evento -> botao.setStyle("-fx-background-color: #836FFF;" + "-fx-text-fill: white;"
				+ "-fx-padding: 8px 16px;" + "-fx-border-radius: 10px;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0, 0);" + "-fx-cursor: hand;"));

		botao.setOnMouseExited(evento -> botao.setStyle("-fx-background-color: #6A5ACD;" + "-fx-text-fill: white;"
				+ "-fx-padding: 8px 16px;" + "-fx-border-radius: 10px;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0, 0);" + "-fx-cursor: hand;"));
	}

	public static void aplicarEstiloCampo(TextField campo) {
		campo.setStyle("-fx-background-color: #dfe6e9; -fx-text-fill: #2d3436; ");
	}

	public static void aplicarEstiloComboBox(ComboBox<String> comboBox) {
		comboBox.setStyle("-fx-background-color: #dfe6e9; -fx-text-fill: #2d3436; ");
	}

	// Estilo do Menu
	public static void aplicarEstiloBotaoMenu(Button button) {

		button.setPrefWidth(200);
		button.setPrefHeight(40);
		button.setStyle("-fx-background-color: #483D8B; " + "-fx-text-fill: white; " + "-fx-background-radius: 10px; "
				+ "-fx-font-weight: bold; " + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 2, 2);");

		button.setOnMousePressed(evento -> button.setStyle("-fx-background-color: #6959CD; " + "-fx-text-fill: white; "
				+ "-fx-background-radius: 10px; " + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 2, 2);"

		));

		button.setOnMouseReleased(evento -> {
			PauseTransition pause = new PauseTransition(Duration.millis(30));
			pause.setOnFinished(e -> button.setStyle(
					"-fx-background-color:#483D8B; " + "-fx-text-fill: white;" + " " + "-fx-background-radius: 10px; "
							+ "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 2, 2);"));
			pause.play();
		});
	}

	public static void aplicarEstiloBotaoSair(Button button) {

		button.setPrefWidth(200);
		button.setPrefHeight(40);
		button.setStyle("-fx-background-color: red; " + "-fx-text-fill: white; " + "-fx-background-radius: 10px; "
				+ "-fx-font-weight: bold; " + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 2, 2);");

		button.setOnMousePressed(evento -> button
				.setStyle("-fx-background-color: darkred; " + "-fx-text-fill: white; " + "-fx-background-radius: 10px; "
						+ "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 2, 2);"));

		button.setOnMouseReleased(evento -> {
			PauseTransition pause = new PauseTransition(Duration.millis(35));
			pause.setOnFinished(e -> button
					.setStyle("-fx-background-color: red; " + "-fx-text-fill: white; " + "-fx-background-radius: 10px; "
							+ "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 2, 2);"));
			pause.play();
		});
	}

	// Atualiza o estilo de uma linha
	public static void atualizarEstiloLinhaSelecionada(TableRow<Produto> row, Produto produto) {
		if (produto.isSelecionado()) {
			row.setStyle("-fx-background-color: lightblue;");
		} else {
			row.setStyle("");
		}
	}

}