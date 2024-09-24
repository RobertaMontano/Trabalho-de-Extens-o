package com.controleestoque.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária para realizar operações de limpeza no banco de dados.
 */
public class LimpezaBancoDados {

	private Connection conexao;

	// Construtor que inicializa a conexão
	public LimpezaBancoDados(Connection conexao) {
		this.conexao = conexao;
	}

	// Limpa todas as tabelas
	public void limparTabelas() throws SQLException {
		
		excluirEResetarTabela("Produtos");
		excluirEResetarTabela("Caixas");
	}

	// Exclui os dados e reseta os IDs de uma tabela
	private void excluirEResetarTabela(String nomeTabela) throws SQLException {
		
		excluirDadosTabela(nomeTabela); 
		resetarIdsTabela(nomeTabela); 
	}

	// Exclui todos os dados de uma tabela
	private void excluirDadosTabela(String nomeTabela) throws SQLException {
		
		String sql = "DELETE FROM " + nomeTabela;
		try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Erro ao excluir dados da tabela " + nomeTabela + ": " + e.getMessage());
		}
	}

	// Reseta os IDs da tabela para começar do valor 1
	private void resetarIdsTabela(String nomeTabela) throws SQLException {
		// Derby usa RESTART WITH
		String sql = "ALTER TABLE " + nomeTabela + " ALTER COLUMN id RESTART WITH 1"; 
		try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Erro ao resetar IDs da tabela " + nomeTabela + ": " + e.getMessage());
		}
	}

	// Remove caixas que não possuem nenhum produto
	public static void removerCaixasVazias(Connection conn) throws SQLException {
		
        String sql = "DELETE FROM Caixas WHERE id NOT IN (SELECT DISTINCT caixaID FROM Produtos WHERE caixaID IS NOT NULL AND nome IS NOT NULL AND quantidade > 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int linhasAfetadas = pstmt.executeUpdate();
            System.out.println(linhasAfetadas + " caixas vazias removidas.");
        } catch (SQLException e) {
            System.err.println("Erro ao remover caixas vazias: " + e.getMessage());
            throw e;
        }
    }

	// Remove categorias que não possuem produtos
	public static void removerCategoriasVazias(Connection conn) throws SQLException {

        String sql = "DELETE FROM Produtos WHERE (categoria IS NULL OR categoria = '') " +
                     "AND id NOT IN (SELECT DISTINCT id FROM Produtos WHERE nome IS NOT NULL AND quantidade > 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int linhasAfetadas = pstmt.executeUpdate();
            System.out.println(linhasAfetadas + " categorias vazias removidas.");
        } catch (SQLException e) {
            System.err.println("Erro ao remover categorias vazias: " + e.getMessage());
            throw e;
        }
    }

	// Remove localizações que não possuem produtos
	public static void removerLocalizacoesVazias(Connection conn) throws SQLException {
    	
        String sql = "DELETE FROM Produtos WHERE (localizacao IS NULL OR localizacao = '') " +
                     "AND id NOT IN (SELECT DISTINCT id FROM Produtos WHERE nome IS NOT NULL AND quantidade > 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int linhasAfetadas = pstmt.executeUpdate();
            System.out.println(linhasAfetadas + " localizações vazias removidas.");
        } catch (SQLException e) {
            System.err.println("Erro ao remover localizações vazias: " + e.getMessage());
            throw e;
        }
    }

	// Limpa registros inválidos
	public static void limparDadosInvalidos(Connection conn) throws SQLException {
		removerCaixasVazias(conn);
		removerLocalizacoesVazias(conn);
		removerCategoriasVazias(conn);
	}
}
