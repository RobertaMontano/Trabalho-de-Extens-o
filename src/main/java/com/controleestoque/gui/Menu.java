package com.controleestoque.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import com.controleestoque.util.EstiloUI;
/**
 * Classe responsável pela criação do menu lateral da aplicação.
 */
public class Menu {

	private VBox menuLateral;
	private Button btnAddProduto;
	private Button btnRemProduto;
	private Button btnAlterarProduto;
	private Button btnSair;
	private Button btnTotalPorCategoria;
	private Button btnAlternarGrafico;
	private Button btnExibirTabela;

	/**
	 * Construtor responsável por configurar o layout do menu lateral e seus botões.
	 */
	public Menu() {

		//Configurações container menu
		menuLateral = new VBox();
		menuLateral.setPadding(new Insets(20));
		menuLateral.setSpacing(15);
		menuLateral.setPrefWidth(250);

		menuLateral.setStyle("-fx-background-color: rgba(44, 62, 80, 0.5);");

		// Título
		HBox tituloContainer = new HBox();
		tituloContainer.setAlignment(Pos.CENTER);
		Label lblTitulo = new Label("Menu");
		lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
		tituloContainer.getChildren().add(lblTitulo);

		// Botões
		btnAddProduto = new Button("Adicionar");
		btnRemProduto = new Button("Remover");
		btnAlterarProduto = new Button("Alterar");
		btnTotalPorCategoria = new Button("Totalizar por Categoria");
		btnAlternarGrafico = new Button("Exibir Gráfico");
		btnExibirTabela = new Button("Exibir Tabela");
		btnSair = new Button("Sair");

		//Estilos dos botões
		EstiloUI.aplicarEstiloBotaoMenu(btnAddProduto);
		EstiloUI.aplicarEstiloBotaoMenu(btnRemProduto);
		EstiloUI.aplicarEstiloBotaoMenu(btnAlterarProduto);
		EstiloUI.aplicarEstiloBotaoMenu(btnTotalPorCategoria);
		EstiloUI.aplicarEstiloBotaoMenu(btnAlternarGrafico);
		EstiloUI.aplicarEstiloBotaoMenu(btnExibirTabela);
		EstiloUI.aplicarEstiloBotaoSair(btnSair);

		//Evento botão sair
		btnSair.setOnAction(evento -> {
			Platform.exit();
			System.exit(0);
		});
		//Coloca os botões no menu
		menuLateral.getChildren().addAll(tituloContainer, btnAddProduto, btnRemProduto,
				btnAlterarProduto, btnTotalPorCategoria,
				btnAlternarGrafico, btnExibirTabela);

		//Container do botão de sair
		VBox sairContainer = new VBox();
		sairContainer.getChildren().add(btnSair);
		sairContainer.setAlignment(Pos.BOTTOM_CENTER);
		VBox.setVgrow(sairContainer, Priority.ALWAYS);
		menuLateral.getChildren().add(sairContainer);
		VBox.setMargin(btnSair, new Insets(0, 10, 20, 0));
	}


	// Getters dos botões
	public VBox getMenuLateral() {
		return menuLateral;
	}

	public Button getBtnAddProduto() {
		return btnAddProduto;
	}

	public Button getBtnRemProduto() {
		return btnRemProduto;
	}

	public Button getBtnAlterarProduto() {
		return btnAlterarProduto;
	}

	public Button getBtnTotalPorCategoria() {
		return btnTotalPorCategoria;
	}

	public Button getBtnAlternarGrafico() {
		return btnAlternarGrafico;
	}

	public Button getBtnExibirTabela() {
		return btnExibirTabela;
	}
}
