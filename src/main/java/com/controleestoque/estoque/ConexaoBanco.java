package com.controleestoque.estoque;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.controleestoque.util.ConfiguracaoBanco;

/**
 * Classe responsável pela conexão e transação com o banco de dados.
 */
public class ConexaoBanco {

	/**
	 * Estabelece e retorna uma conexão com o banco de dados.
	 *
	 * @return Conexão com o banco de dados ou null em caso de falha.
	 */
	public static Connection getConexao() {
		try {
			String url = ConfiguracaoBanco.getDatabaseUrl();
			Connection conn = DriverManager.getConnection(url);
			if (conn != null && !conn.isClosed()) {
				conn.setAutoCommit(false);
			}
			return conn;
		} catch (SQLException e) {
			System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Fecha a conexão com o banco de dados.
	 *
	 * @param conexao A conexão a ser fechada.
	 */
	public static void fecharConexao(Connection conexao) {
		if (conexao != null) {
			try {
				conexao.close();
			} catch (SQLException e) {
				System.out.println("Erro ao fechar a conexão: " + e.getMessage());
			}
		}
	}

	/**
	 * Testa a conexão com o banco de dados.
	 *
	 * @return true se a conexão foi bem-sucedida, false caso contrário.
	 */
	public static boolean testarConexao() {
		try (Connection conn = getConexao()) {
			return conn != null && !conn.isClosed();
		} catch (SQLException e) {
			System.out.println("Falha ao testar a conexão: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Confirma a transação.
	 *
	 * @param conexao A conexão onde a transação será confirmada.
	 */
	public static void commit(Connection conexao) {
		try {
			if (conexao != null && !conexao.getAutoCommit()) {
				conexao.commit();
			}
		} catch (SQLException e) {
			System.out.println("Erro ao confirmar a transação: " + e.getMessage());
		}
	}

	/**
	 * Reverte a transação.
	 *
	 * @param conexao A conexão onde a transação será revertida.
	 */
	public static void rollback(Connection conexao) {
		try {
			if (conexao != null && !conexao.getAutoCommit()) {
				conexao.rollback();
			}
		} catch (SQLException e) {
			System.out.println("Erro ao reverter a transação: " + e.getMessage());
		}
	}
}
