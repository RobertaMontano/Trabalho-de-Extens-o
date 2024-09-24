package com.controleestoque.servico;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.controleestoque.estoque.Produto;

import com.controleestoque.gui.Tabela;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
/**
 * Classe responsável pela gestão e exibição do gráfico.
 */
public class GerenciadorGrafico {

	private VBox grafico;
	private BorderPane layoutPrincipal;
	private ObservableList<Produto> produtosData;
	private Tabela tabela;

	/**
	 * Construtor que inicializa o gerenciador de gráficos com os dados e layout fornecidos.
	 *
	 * @param layoutPrincipal O layout principal da aplicação.
	 * @param produtosData A lista observável de produtos a serem exibidos no gráfico.
	 * @param tabela O componente de tabela para alternar entre tabela e gráfico.
	 */
	public GerenciadorGrafico(BorderPane layoutPrincipal, ObservableList<Produto> produtosData, Tabela tabela) {
		this.layoutPrincipal = layoutPrincipal;
		this.produtosData = produtosData;
		this.tabela = tabela;
	}

	/**
	 * Alterna a visualização entre a tabela ou gráfico.
	 *
	 * @param mostrarGrafico Sendo true para exibir o gráfico e false para exibir a tabela.
	 */
	public void alternaTabelaOuGrafico(boolean mostrarGrafico) {
		if (mostrarGrafico) {
			tabela.getTabelaContainer().setVisible(false);
			atualizarGrafico();
		} else {
			layoutPrincipal.setCenter(tabela.getTabelaContainer());
			tabela.getTabelaContainer().setVisible(true);
			tabela.atualizarTabela();
		}
		layoutPrincipal.requestLayout();
	}

	/**
	 * Atualiza o gráfico com os dados atuais disponíveis na lista de produtos.
	 */
	public void atualizarGrafico() {
		Platform.runLater(() -> {
			if (grafico != null) {
				layoutPrincipal.getChildren().remove(grafico);
			}

			if (produtosData == null || produtosData.isEmpty()) {
				return;
			}

			grafico = GerenciadorGrafico.criaGraficoPorCategoria(produtosData);
			layoutPrincipal.setCenter(grafico);

			grafico.setVisible(true);
			grafico.setManaged(true);
			grafico.requestLayout();
		});
	}

	/**
	 * Cria um gráfico de barras para exibir a quantidade de produtos por categoria.
	 *
	 * @param produtosData A lista observável de produtos para geração do gráfico.
	 * @return VBox contendo o gráfico e a legenda correspondente.
	 */

	public static VBox criaGraficoPorCategoria(ObservableList<Produto> produtosData) {
		if (produtosData == null || produtosData.isEmpty()) {
			System.out.println("Nenhum dado disponível para criar o gráfico.");
			return new VBox(new Label("Sem dados disponíveis para o gráfico."));
		}
		//Eixos do gráfico
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Categoria");
		xAxis.setTickLabelsVisible(false);

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Quantidade");
		yAxis.setTickUnit(10);
		yAxis.lookup(".axis-label").setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

		//Gráfico de barras
		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle("Quantidade de Produtos por Categoria");

		barChart.setCategoryGap(10);
		barChart.setBarGap(5);
		barChart.setLegendVisible(false);
		barChart.setVerticalGridLinesVisible(false);
		barChart.setHorizontalGridLinesVisible(false);
		barChart.setPrefWidth(1000);
		barChart.setPrefHeight(700);

		barChart.getStylesheets().add("css/grafico.css"); // arquivo css do gráfico
		barChart.getStyleClass().add("custom-bar-chart");

		XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
		dataSeries.setName("Estoque");

		Map<String, Integer> totalPorCategoria = GerenciadorTotalizacaoProduto.calcularTotalPorCategoria(produtosData);
		if (totalPorCategoria.isEmpty()) {
			System.out.println("Nenhuma categoria encontrada para o gráfico.");
			return new VBox(new Label("Sem categorias para o gráfico."));
		}

		//Cores das barras
		List<Color> colors = List.of(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE);
		Map<String, Color> categoriaCores = new HashMap<>();
		final int[] colorIndex = { 0 };

		final int maxCategorias = 10;
		int categoriaCount = 0;
		for (Map.Entry<String, Integer> entry : totalPorCategoria.entrySet()) {
			if (categoriaCount >= maxCategorias) {
				break;
			}

			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
			dataSeries.getData().add(data);

			Color color = colors.get(colorIndex[0] % colors.size());
			categoriaCores.put(entry.getKey(), color);
			data.nodeProperty().addListener((obs, oldNode, newNode) -> {
				if (newNode != null) {
					newNode.setStyle("-fx-bar-fill: " + obterCodigoHexadecimal(color) + ";");
				}
			});
			colorIndex[0]++;
			categoriaCount++;
		}

		barChart.getData().clear();
		barChart.getData().add(dataSeries);

		//legenda
		HBox legendaContainer = new HBox(10);
		legendaContainer.setAlignment(Pos.CENTER);
		legendaContainer.setPadding(new Insets(10));

		for (Map.Entry<String, Color> entry : categoriaCores.entrySet()) {
			Rectangle colorBox = new Rectangle(15, 15, entry.getValue());
			Text legendaItem = new Text(entry.getKey());
			legendaItem.setStyle("-fx-font-size: 18px; -fx-fill: white; -fx-font-weight: bold;");
			HBox legendaItemContainer = new HBox(5, colorBox, legendaItem);
			legendaItemContainer.setAlignment(Pos.CENTER_LEFT);
			legendaContainer.getChildren().add(legendaItemContainer);
		}

		VBox vbox = new VBox(barChart, legendaContainer);
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(20);

		return vbox;
	}

	/**
	 * Converte uma cor para o formato hexadecimal.
	 *
	 * @param color A cor a ser convertida.
	 * @return Código hexadecimal correspondente à cor.
	 */
	private static String obterCodigoHexadecimal(Color color) {
		return String.format("#%02X%02X%02X",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	/**
	 * Verifica se o gráfico está atualmente ativo e visível no layout.
	 *
	 * @return Sendo true se o gráfico está ativo e false se não estiver.
	 */
	public boolean isGraficoAtivo() {
		return layoutPrincipal.getCenter() == grafico;
	}

}
