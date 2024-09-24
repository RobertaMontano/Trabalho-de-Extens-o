package com.controleestoque.util;

import com.controleestoque.estoque.Produto;
import com.controleestoque.estoque.ProdutoDAO;
import com.controleestoque.estoque.Caixa;
import com.controleestoque.estoque.CaixaDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
/**
 * Classe utilitária responsável pela geração de dados fictícios para o banco de dados.
 * Gera produtos e caixas em massa, facilitando o preenchimento e testes da aplicação.
 */
public class GeradorDados {

	private static final Random random = new Random();

	//Gera uma quantidade especifica de produtos e os insere no banco de dados.
	public static void gerarProdutosEmMassa(Connection connection, int quantidade) {
		
		ProdutoDAO produtoDAO = new ProdutoDAO(connection);
		CaixaDAO caixaDAO = new CaixaDAO(connection);
		try {
			if (caixaDAO.listar().isEmpty()) {
				gerarCaixas(connection, 100);
			}

			var caixas = caixaDAO.listar();
			if (caixas.isEmpty()) {
				System.err.println("Nenhuma caixa está disponível.");
				return;
			}

			for (int i = 1; i <= quantidade; i++) {
				Caixa caixaAssociada = caixas.get(random.nextInt(caixas.size()));

				Produto produto = new Produto();
				produto.setNome("Produto " + i);

				produto.setQuantidade(random.nextInt(500) + 1);

				produto.setCategoria("Categoria " + (random.nextInt(5) + 1));
				produto.setCaixaID(caixaAssociada.getId());

				produtoDAO.adicionar(produto);
			}

			System.out.println("Geração de " + quantidade + " produtos concluída com sucesso.");
		} catch (SQLException e) {
			System.err.println("Erro ao gerar produtos: " + e.getMessage());
			e.printStackTrace();
		}
	}
	// Gera uma quantidade especifica de caixas aleatórias
	public static void gerarCaixas(Connection connection, int quantidade) {
		
		CaixaDAO caixaDAO = new CaixaDAO(connection);
		try {
			for (int i = 1; i <= quantidade; i++) {
				Caixa caixa = new Caixa("Caixa " + i, "Estante " + i);
				caixaDAO.adicionar(caixa);
			}

			System.out.println("Geração de " + quantidade + " caixas concluída com sucesso.");
		} catch (SQLException e) {
			System.err.println("Erro ao gerar caixas: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
