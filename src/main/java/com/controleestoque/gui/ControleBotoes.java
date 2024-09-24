package com.controleestoque.gui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.controleestoque.estoque.Caixa;
import com.controleestoque.estoque.CaixaDAO;
import com.controleestoque.estoque.ConexaoBanco;
import com.controleestoque.estoque.Produto;
import com.controleestoque.servico.GerenciadorGrafico;
import com.controleestoque.servico.ServicoProduto;
import com.controleestoque.servico.GerenciadorTotalizacaoProduto;
import com.controleestoque.util.AlertaUtils;
import com.controleestoque.util.LimpezaBancoDados;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * Classe que controla as funcionalidades e eventos dos botões da interface.
 */
public class ControleBotoes {

	private ServicoProduto servicoProduto;
	private Tabela tabela;
	private BorderPane layoutPrincipal;
	private ObservableList<Produto> produtosData;
	private GerenciadorGrafico gerenciadorGrafico;


	/**
	 * Construtor que inicializa o controle dos botões com os serviços e layout fornecidos.
	 *
	 * @param servicoProduto O serviço responsável pelo gerenciamento de produtos.
	 * @param tabela A tabela de produtos na interface.
	 * @param layoutPrincipal O layout principal da interface.
	 */
	public ControleBotoes(ServicoProduto servicoProduto, Tabela tabela, BorderPane layoutPrincipal) {
		this.servicoProduto = servicoProduto;
		this.tabela = tabela;
		this.layoutPrincipal = layoutPrincipal;
		this.produtosData = FXCollections.observableArrayList();
		this.gerenciadorGrafico = new GerenciadorGrafico(layoutPrincipal, produtosData, tabela);

		carregarDados();

		tabela.getTabela().setOnKeyPressed(evento -> {
			if (evento.getCode() == KeyCode.DELETE) {
				removerProdutoSelecionado();
			}
		});
	}

	/**
	 * Configura os eventos para os botões fornecidos.
	 *
	 * @param btnAddProduto Botão para adicionar produtos.
	 * @param btnRemProduto Botão para remover produtos.
	 * @param btnAlterarProduto Botão para alterar produtos.
	 * @param btnAplicarFiltros Botão para aplicar filtros.
	 * @param btnLimparFiltros Botão para limpar filtros.
	 * @param btnTotalPorCategoria Botão para exibir total por categoria.
	 * @param btnAlternarGrafico Botão para alternar entre tabela e gráfico.
	 * @param btnExibirTabela Botão para exibir a tabela.
	 */
	public void configurarBotoes(Button btnAddProduto, Button btnRemProduto, Button btnAlterarProduto,
			Button btnAplicarFiltros, Button btnLimparFiltros, Button btnTotalPorCategoria, Button btnAlternarGrafico,
			Button btnExibirTabela) {

		configurarAdicionarProduto(btnAddProduto);
		configurarRemoverProduto(btnRemProduto);
		configurarAlteracaoProduto(btnAlterarProduto);
		configurarFiltros(btnAplicarFiltros, btnLimparFiltros);

		btnTotalPorCategoria.setOnAction(evento -> exibirTotalPorCategoria());
		btnAlternarGrafico.setOnAction(evento -> alternaTabelaOuGrafico(true));
		btnExibirTabela.setOnAction(evento -> alternaTabelaOuGrafico(false));
	}

	// Atualiza a tabela após filtros ou mudanças de dados.
	private void atualizarTabela() {

		Platform.runLater(() -> {
			try {
				tabela.getFiltroProdutoHelper().aplicarFiltros();
			} catch (Exception e) {
				AlertaUtils.mostrarAlertaErro("Erro ao atualizar tabela!", e.getMessage());
			}
		});
	}

	// Define o eventos dos botões de limpar e aplicar filtro
	private void configurarFiltros(Button btnAplicarFiltros, Button btnLimparFiltros) {

		btnAplicarFiltros.setOnAction(evento -> {
            tabela.getFiltroProdutoHelper().aplicarFiltros();
        });

		btnLimparFiltros.setOnAction(evento -> {
			try {
				tabela.getFiltroProdutoHelper().limparFiltros();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	// Carrega os dados dos produtos para a tabela
	private void carregarDados() {

		Platform.runLater(() -> {
			try {
				List<Produto> produtos = servicoProduto.listarProdutos();
				if (produtos.isEmpty()) {
					System.out.println("Nenhum produto encontrado no banco de dados.");
					produtosData.clear();
				} else {
					produtosData.clear();
					produtosData.addAll(produtos);
				}
				tabela.getFiltroProdutoHelper().aplicarFiltros();
				atualizarTabela();
			} catch (SQLException e) {
				e.printStackTrace();
				AlertaUtils.mostrarAlertaErro("Erro!", "Erro ao carregar dados do banco de dados: " + e.getMessage());
			}
		});
	}

	// Exibe uma janela com a contagem da quantidade por categoria
	public void exibirTotalPorCategoria() {

		ObservableList<Produto> produtosData = tabela.getTabela().getItems();
		Map<String, Integer> totalPorCategoria = GerenciadorTotalizacaoProduto.calcularTotalPorCategoria(produtosData);
		StringBuilder resultado = new StringBuilder("Total de Produtos por Categoria:\n");

		for (Map.Entry<String, Integer> entry : totalPorCategoria.entrySet()) {
			String categoria = entry.getKey() != null ? entry.getKey() : "Sem Categoria";
			resultado.append("Categoria: ").append(categoria).append(" - Quantidade: ").append(entry.getValue())
					.append("\n");
		}

		AlertaUtils.mostrarAlertaInformacao("Totalização por Categoria", resultado.toString());
	}

	// Alterna entre a visualização de tabela e gráfico.
	public void alternaTabelaOuGrafico(boolean mostrarGrafico) {
		gerenciadorGrafico.alternaTabelaOuGrafico(mostrarGrafico);
	}

	//Configura o botão de adicionar
	private void configurarAdicionarProduto(Button btnAddProduto) {

		btnAddProduto.setOnAction(evento -> {
			Stage addProdutoStage = new Stage();
			addProdutoStage.getIcons().add(new Image("/imagens/adicionar.png"));
			addProdutoStage.initModality(Modality.APPLICATION_MODAL);

			VBox vbox = new VBox(10);
			vbox.setPadding(new Insets(20));

			TextField nomeField = new TextField();
			TextField quantidadeField = new TextField();
			TextField categoriaField = new TextField();
			TextField caixaField = new TextField();
			TextField localizacaoField = new TextField();

			Button salvarButton = new Button("Salvar");
			Button salvarFecharButton = new Button("Salvar e Fechar");

			salvarButton.setOnAction(e -> {
				adicionarProduto(nomeField, quantidadeField, categoriaField, caixaField, localizacaoField, false,
						addProdutoStage);
			});

			salvarFecharButton.setOnAction(e -> {
				adicionarProduto(nomeField, quantidadeField, categoriaField, caixaField, localizacaoField, true,
						addProdutoStage);
			});

			vbox.getChildren().addAll(new Label("Nome do Produto"), nomeField, new Label("Quantidade"), quantidadeField,
					new Label("Categoria"), categoriaField, new Label("Caixa"), caixaField, new Label("Localização"),
					localizacaoField, salvarButton, salvarFecharButton);

			Scene scene = new Scene(vbox, 400, 400);
			addProdutoStage.setScene(scene);
			addProdutoStage.setResizable(false);
			addProdutoStage.setTitle("Adicionar Produto");
			addProdutoStage.show();
		});
	}

	//Adiciona um produto
	private void adicionarProduto(TextField nomeField, TextField quantidadeField, TextField categoriaField,
			TextField caixaField, TextField localizacaoField, boolean fechar, Stage stage) {
		try {
			String nome = nomeField.getText();
			if (nome.isEmpty()) {
				throw new IllegalArgumentException("O nome do produto não pode estar vazio.");
			}
			int quantidade = Integer.parseInt(quantidadeField.getText());
			String categoria = categoriaField.getText();
			String caixaNome = caixaField.getText();
			String localizacao = localizacaoField.getText();

			Connection conn = servicoProduto.getConexao();
			if (conn == null || conn.isClosed()) {
				throw new SQLException("Conexão com o banco de dados não disponível.");
			}

			CaixaDAO caixaDAO = new CaixaDAO(conn);
			Integer caixaId = null;

			if (!caixaNome.isEmpty()) {
				caixaId = caixaDAO.buscarIdCaixaPorNome(caixaNome);
				if (caixaId == 0) {
					Optional<ButtonType> result = AlertaUtils.mostrarConfirmacao("Caixa não encontrada!",
							"A caixa informada não existe. Deseja criá-la?");
					if (result.isPresent() && result.get() == ButtonType.OK) {
						Caixa novaCaixa = new Caixa();
						novaCaixa.setNomeCaixa(caixaNome);
						novaCaixa.setLocalizacao(localizacao);
						caixaDAO.adicionar(novaCaixa);
						caixaId = caixaDAO.buscarIdCaixaPorNome(caixaNome);
					} else {
						return;
					}
				}
			}

			Produto produto = new Produto();
			produto.setNome(nome);
			produto.setQuantidade(quantidade);
			produto.setCategoria(categoria);
			produto.setCaixaID(caixaId);
			produto.setLocalizacao(localizacao);
			produto.setSelecionado(false);

			Task<Void> adicionarProdutoTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						servicoProduto.adicionar(produto);
						LimpezaBancoDados.limparDadosInvalidos(conn);
						ConexaoBanco.commit(conn);
						carregarDados();
						Platform.runLater(() -> {
                            try {
                                tabela.atualizarFiltros();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
							if (gerenciadorGrafico.isGraficoAtivo()) {
								gerenciadorGrafico.atualizarGrafico();
							}
                            AlertaUtils.mostrarAlertaInformacao("Sucesso", "Produto adicionado com sucesso!");
							if (fechar) {
								stage.close();
							} else {
								nomeField.clear();
								quantidadeField.clear();
								categoriaField.clear();
								caixaField.clear();
								localizacaoField.clear();
							}
						});
					} catch (SQLException ex) {
						ConexaoBanco.rollback(conn);
						Platform.runLater(() -> AlertaUtils.mostrarAlertaErro("Erro de Banco de Dados!",
								"Erro ao adicionar produto: " + ex.getMessage()));
					}
					return null;
				}
			};

			Thread thread = new Thread(adicionarProdutoTask);
			thread.setDaemon(true);
			thread.start();

		} catch (NumberFormatException ex) {
			AlertaUtils.mostrarAlertaErro("Erro de Validação!", "A quantidade deve ser um número válido.");
		} catch (IllegalArgumentException ex) {
			AlertaUtils.mostrarAlertaErro("Erro de Validação!", ex.getMessage());
		} catch (SQLException ex) {
			AlertaUtils.mostrarAlertaErro("Erro de Banco de Dados!", "Erro ao adicionar produto: " + ex.getMessage());
		}
	}

	//Configura o  botão de remover
	private void configurarRemoverProduto(Button btnRemProduto) {
		btnRemProduto.setOnAction(evento -> removerProdutoSelecionado());
	}

	//Remove um produto
	private void removerProdutoSelecionado() {

		ObservableList<Produto> produtosSelecionados = FXCollections.observableArrayList();
		for (Produto produto : tabela.getTabela().getItems()) {
			if (produto.isSelecionado()) {
				produtosSelecionados.add(produto);
			}
		}
		if (produtosSelecionados != null && !produtosSelecionados.isEmpty()) {
			Optional<ButtonType> result = AlertaUtils.mostrarConfirmacao("Confirmação de Remoção",
					"Deseja remover " + produtosSelecionados.size() + " produtos selecionados?");

			if (result.isPresent() && result.get() == ButtonType.OK) {
				Task<Void> removerMultiplosTask = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Connection conn = null;
						try {
							conn = ConexaoBanco.getConexao();
							for (int i = produtosSelecionados.size() - 1; i >= 0; i--) {
								Produto produto = produtosSelecionados.get(i);
								servicoProduto.remover(produto);
								Platform.runLater(() -> produtosData.remove(produto));
							}
							LimpezaBancoDados.limparDadosInvalidos(conn);
							ConexaoBanco.commit(conn);

							Platform.runLater(() -> {
								carregarDados();
								try {
									tabela.atualizarFiltros();
								} catch (SQLException e) {
									e.printStackTrace();
								}
								AlertaUtils.mostrarAlertaInformacao("Sucesso",
										produtosSelecionados.size() + " produtos removidos com sucesso!");
							});
						} catch (SQLException e) {
							ConexaoBanco.rollback(conn);
							Platform.runLater(() -> AlertaUtils.mostrarAlertaErro("Erro de Banco de Dados",
									"Erro ao remover produtos: " + e.getMessage()));
						} finally {
							ConexaoBanco.fecharConexao(conn);
						}
						return null;
					}
				};
				Thread thread = new Thread(removerMultiplosTask);
				thread.setDaemon(true);
				thread.start();
			}
		} else {
			AlertaUtils.mostrarAlertaErro("Aviso", "Nenhum produto selecionado.");
		}
	}

	//Configura o botão de alterar
	private void configurarAlteracaoProduto(Button btnAlterarProduto) {

		btnAlterarProduto.setOnAction(evento -> {
			ObservableList<Produto> produtosSelecionados = FXCollections.observableArrayList();
			for (Produto produto : tabela.getTabela().getItems()) {
				if (produto.isSelecionado()) {
					produtosSelecionados.add(produto);
				}
			}
			if (produtosSelecionados == null || produtosSelecionados.isEmpty()) {
				AlertaUtils.mostrarAlertaErro("Aviso", "Nenhum produto selecionado.");
				return;
			}
			if (produtosSelecionados.size() == 1) {
				Produto produtoSelecionado = produtosSelecionados.get(0);
				mostrarFormularioAlteracaoUnico(produtoSelecionado);
			} else {
				mostrarFormularioAlteracaoEmMassa(produtosSelecionados);
			}
		});
	}

	//Formulário usado para alterar um produto
	private void mostrarFormularioAlteracaoUnico(Produto produto) {

		Stage alterarProdutoStage = new Stage();
		alterarProdutoStage.getIcons().add(new Image("/imagens/alterar.png"));
		alterarProdutoStage.initModality(Modality.APPLICATION_MODAL);

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(20));

		TextField nomeField = new TextField(produto.getNome() != null ? produto.getNome() : "");
		TextField quantidadeField = new TextField(String.valueOf(produto.getQuantidade()));
		TextField categoriaField = new TextField(produto.getCategoria() != null ? produto.getCategoria() : "");
		TextField caixaField = new TextField(produto.getCaixaNome() != null ? produto.getCaixaNome() : "");
		TextField localizacaoField = new TextField(produto.getLocalizacao() != null ? produto.getLocalizacao() : "");

		Button salvarButton = new Button("Salvar Alterações");
		salvarButton.setOnAction(evento -> {
			try {
				if (nomeField.getText().isEmpty()) {
					AlertaUtils.mostrarAlertaErro("Erro de Validação!", "O nome do produto é obrigatório.");
					return;
				}

				produto.setNome(nomeField.getText());
				produto.setQuantidade(Integer.parseInt(quantidadeField.getText()));
				produto.setCategoria(categoriaField.getText().isEmpty() ? null : categoriaField.getText());
				produto.setLocalizacao(localizacaoField.getText().isEmpty() ? null : localizacaoField.getText());

				String nomeCaixa = caixaField.getText().trim();
				CaixaDAO caixaDAO = new CaixaDAO(servicoProduto.getConexao());
				Integer caixaId = null;

				if (!nomeCaixa.isEmpty()) {
					caixaId = caixaDAO.buscarIdCaixaPorNome(nomeCaixa);
					if (caixaId == 0) {
						Optional<ButtonType> result = AlertaUtils.mostrarConfirmacao("Caixa não encontrada!",
								"A caixa informada não existe. Deseja criá-la?");
						if (result.isPresent() && result.get() == ButtonType.OK) {
							Caixa novaCaixa = new Caixa();
							novaCaixa.setNomeCaixa(nomeCaixa);
							novaCaixa.setLocalizacao(localizacaoField.getText().isEmpty() ? null : localizacaoField.getText());
							caixaDAO.adicionar(novaCaixa);
							caixaId = caixaDAO.buscarIdCaixaPorNome(nomeCaixa);
						} else {
							caixaId = null;
						}
					}
				}
				produto.setCaixaID(caixaId);

				Task<Void> alterarTask = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						try (Connection conn = ConexaoBanco.getConexao()) {
							conn.setAutoCommit(false);
							servicoProduto.alterar(produto);
							LimpezaBancoDados.limparDadosInvalidos(conn);
							conn.commit();

							Platform.runLater(() -> {
								try {
									carregarDados();
									tabela.atualizarFiltros();
									AlertaUtils.mostrarAlertaInformacao("Sucesso", "Produto alterado com sucesso!");
									alterarProdutoStage.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							});
						} catch (SQLException ex) {
							throw new SQLException("Erro ao alterar produto: " + ex.getMessage(), ex);
						}
						return null;
					}
				};

				Thread thread = new Thread(alterarTask);
				thread.setDaemon(true);
				thread.start();

			} catch (NumberFormatException ex) {
				AlertaUtils.mostrarAlertaErro("Erro de Validação!", "A quantidade deve ser um número válido.");
			} catch (SQLException ex) {
				AlertaUtils.mostrarAlertaErro("Erro de Banco de Dados!", "Erro ao alterar produto: " + ex.getMessage());
			}
		});

		vbox.getChildren().addAll(new Label("Nome"), nomeField, new Label("Quantidade"), quantidadeField,
				new Label("Categoria"), categoriaField, new Label("Caixa"), caixaField,
				new Label("Localização"), localizacaoField, salvarButton);

		Scene scene = new Scene(vbox, 400, 400);
		alterarProdutoStage.setScene(scene);
		alterarProdutoStage.setResizable(false);
		alterarProdutoStage.setTitle("Alterar Produto");
		alterarProdutoStage.show();
	}

	//Formulário usado para alteração múltipla
	private void mostrarFormularioAlteracaoEmMassa(ObservableList<Produto> produtosSelecionados) {

		Stage alterarMassaStage = new Stage();
		alterarMassaStage.getIcons().add(new Image("/imagens/categoria.png"));
		alterarMassaStage.initModality(Modality.APPLICATION_MODAL);

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(20));

		TextField categoriaField = new TextField();
		TextField caixaField = new TextField();
		TextField localizacaoField = new TextField();

		Button salvarButton = new Button("Salvar Alterações");
		salvarButton.setOnAction(e -> {
			String novaCategoria = categoriaField.getText().trim();
			String novaCaixa = caixaField.getText().trim();
			String novaLocalizacao = localizacaoField.getText().trim();
			CaixaDAO caixaDAO = new CaixaDAO(servicoProduto.getConexao());
			Integer caixaId = null;

			if (!novaCaixa.isEmpty()) {
				try {
					caixaId = caixaDAO.buscarIdCaixaPorNome(novaCaixa);
					if (caixaId == 0) {
						Optional<ButtonType> result = AlertaUtils.mostrarConfirmacao("Caixa não encontrada!",
								"A caixa informada não existe. Deseja criá-la?");
						if (result.isPresent() && result.get() == ButtonType.OK) {
							Caixa novaCaixaObj = new Caixa();
							novaCaixaObj.setNomeCaixa(novaCaixa);
							novaCaixaObj.setLocalizacao(novaLocalizacao.isEmpty() ? null : novaLocalizacao);
							caixaDAO.adicionar(novaCaixaObj);
							caixaId = caixaDAO.buscarIdCaixaPorNome(novaCaixa);
						} else {
							caixaId = null;
						}
					}
				} catch (SQLException ex) {
					AlertaUtils.mostrarAlertaErro("Erro de Banco de Dados", "Erro ao buscar/criar a caixa: " + ex.getMessage());
					return;
				}
			}

			if (novaCaixa.isEmpty()) {
				caixaId = null;
			}

			Integer finalCaixaId = caixaId;
			Task<Void> alterarMassaTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try (Connection conn = ConexaoBanco.getConexao()) {
						conn.setAutoCommit(false);

						for (Produto produto : produtosSelecionados) {
							if (!novaCategoria.isEmpty()) {
								produto.setCategoria(novaCategoria);
							} else {
								produto.setCategoria(null);
							}
							produto.setCaixaID(finalCaixaId);
							produto.setLocalizacao(novaLocalizacao.isEmpty() ? null : novaLocalizacao);
						}

						servicoProduto.alterarMassa(produtosSelecionados);
						LimpezaBancoDados.limparDadosInvalidos(conn);
						conn.commit();

						Platform.runLater(() -> {
							try {
								carregarDados();
								tabela.atualizarFiltros();
								AlertaUtils.mostrarAlertaInformacao("Sucesso", "Produtos alterados com sucesso!");
								alterarMassaStage.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						});
					} catch (SQLException ex) {
						Platform.runLater(() -> AlertaUtils.mostrarAlertaErro("Erro de Banco de Dados",
								"Erro ao alterar produtos: " + ex.getMessage()));
					}
					return null;
				}
			};

			Thread thread = new Thread(alterarMassaTask);
			thread.setDaemon(true);
			thread.start();
		});

		vbox.getChildren().addAll(
				new Label("Nova Categoria "), categoriaField,
				new Label("Nova Caixa "), caixaField,
				new Label("Nova Localização "), localizacaoField,
				salvarButton
		);

		Scene scene = new Scene(vbox, 400, 300);
		alterarMassaStage.setScene(scene);
		alterarMassaStage.setResizable(false);
		alterarMassaStage.setTitle("Alterar  Vários Produtos");
		alterarMassaStage.show();
	}

}
