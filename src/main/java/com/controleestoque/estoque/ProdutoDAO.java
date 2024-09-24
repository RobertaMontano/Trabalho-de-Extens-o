package com.controleestoque.estoque;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pelo acesso aos dados da entidade Produto no banco de dados.
 */
public class ProdutoDAO {

	private Connection conexao;

	/**
	 * Construtor que recebe a conexão com o banco de dados.
	 *
	 * @param conexao A conexão com o banco de dados.
	 */
	public ProdutoDAO(Connection conexao) {
		this.conexao = conexao;
	}

	/**
	 * Adiciona um novo produto no banco de dados.
	 *
	 * @param produto O produto a ser adicionado.
	 * @throws SQLException Em caso de erro ao adicionar o produto.
	 */
	public void adicionar(Produto produto) throws SQLException {

		String query = "INSERT INTO Produtos (nome, quantidade, categoria, caixaID, localizacao) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conexao.prepareStatement(query)) {
			conexao.setAutoCommit(false);

			stmt.setString(1, produto.getNome());
			stmt.setInt(2, produto.getQuantidade());
			stmt.setString(3, produto.getCategoria());

			if (produto.getCaixaID() == null) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, produto.getCaixaID());
			}

			if (produto.getLocalizacao() == null || produto.getLocalizacao().trim().isEmpty()) {
				stmt.setNull(5, Types.VARCHAR);
			} else {
				stmt.setString(5, produto.getLocalizacao());
			}

			stmt.executeUpdate();
			conexao.commit();
		} catch (SQLException e) {
			conexao.rollback();
			throw e;
		}
	}

	/**
	 * Remove um produto do banco de dados.
	 *
	 * @param produto O produto a ser removido.
	 * @throws SQLException Em caso de erro ao remover o produto.
	 */
	public void remover(Produto produto) throws SQLException {
		String query = "DELETE FROM Produtos WHERE id = ?";
		try (PreparedStatement stmt = conexao.prepareStatement(query)) {
			stmt.setInt(1, produto.getId());
			stmt.executeUpdate();
			conexao.commit();
		} catch (SQLException e) {
			conexao.rollback();
			throw e;
		}
	}

	/**
	 * Altera as informações de um produto existente no banco de dados.
	 *
	 * @param produto O produto com as informações atualizadas.
	 * @throws SQLException Em caso de erro ao alterar o produto.
	 */
	public void alterar(Produto produto) throws SQLException {

		String query = "UPDATE Produtos SET nome = ?, categoria = ?, quantidade = ?, caixaID = ?, localizacao = ? WHERE id = ?";
		try (PreparedStatement stmt = conexao.prepareStatement(query)) {
			conexao.setAutoCommit(false);

			stmt.setString(1, produto.getNome());
			stmt.setString(2, produto.getCategoria());
			stmt.setInt(3, produto.getQuantidade());

			if (produto.getCaixaID() == null) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, produto.getCaixaID());
			}

			if (produto.getLocalizacao() == null || produto.getLocalizacao().trim().isEmpty()) {
				stmt.setNull(5, Types.VARCHAR);
			} else {
				stmt.setString(5, produto.getLocalizacao());
			}

			stmt.setInt(6, produto.getId());
			stmt.executeUpdate();
			conexao.commit();
		} catch (SQLException e) {
			conexao.rollback();
			throw e;
		}
	}

	/**
	 * Altera as informações de múltiplos produtos no banco de dados.
	 *
	 * @param produtos Lista de produtos com as informações atualizadas.
	 * @throws SQLException Em caso de erro ao alterar os produtos.
	 */
	public void alterarMassa(List<Produto> produtos) throws SQLException {
		String query = "UPDATE Produtos SET categoria = ?, caixaID = ?, localizacao = ? WHERE id = ?";
		try (PreparedStatement stmt = conexao.prepareStatement(query)) {
			for (Produto produto : produtos) {
				stmt.setString(1, produto.getCategoria());
				stmt.setObject(2, produto.getCaixaID());
				stmt.setString(3, produto.getLocalizacao());
				stmt.setInt(4, produto.getId());
				stmt.addBatch();
			}
			stmt.executeBatch();
			conexao.commit();
		} catch (SQLException e) {
			conexao.rollback();
			throw e;
		}
	}

	/**
	 * Lista todos os produtos cadastrados no banco de dados.
	 *
	 * @return Lista de produtos cadastrados.
	 * @throws SQLException Em caso de erro ao listar os produtos.
	 */
	public List<Produto> listar() throws SQLException {
		List<Produto> produtos = new ArrayList<>();
		String sql = "SELECT p.id, p.nome, p.quantidade, p.categoria, c.nomeCaixa, p.localizacao FROM Produtos p"
				+ " LEFT JOIN Caixas c ON p.caixaId = c.id";

		try (PreparedStatement pstmt = conexao.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				produtos.add(mapearResultado(rs));
			}
		}
		return produtos;
	}

	/**
	 * Busca produtos com base em filtros específicos.
	 *
	 * @param termo          O termo de busca para o nome ou caixa do produto.
	 * @param categoria      A categoria do produto.
	 * @param localizacao    A localização do produto.
	 * @param caixa          O nome da caixa onde o produto está armazenado.
	 * @param quantidadeMin  A quantidade mínima do produto.
	 * @param quantidadeMax  A quantidade máxima do produto.
	 * @return Lista de produtos que correspondem aos filtros.
	 * @throws SQLException Em caso de erro ao buscar produtos.
	 */
	public List<Produto> buscarProdutosComFiltros(String termo, String categoria, String localizacao, String caixa,
												  Integer quantidadeMin, Integer quantidadeMax) throws SQLException {
		List<Produto> produtos = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
				"SELECT p.*, c.nomeCaixa FROM Produtos p LEFT JOIN Caixas c ON p.caixaId = c.id WHERE 1=1 ");

		if (termo != null && !termo.isEmpty()) {
			sql.append("AND (LOWER(p.nome) LIKE ? OR LOWER(c.nomeCaixa) LIKE ?) ");
		}
		if (categoria != null) {
			sql.append("AND LOWER(p.categoria) = LOWER(?) ");
		}
		if (localizacao != null) {
			sql.append("AND LOWER(p.localizacao) = LOWER(?) ");
		}
		if (caixa != null) {
			sql.append("AND LOWER(c.nomeCaixa) = LOWER(?) ");
		}
		if (quantidadeMin != null) {
			sql.append("AND p.quantidade >= ? ");
		}
		if (quantidadeMax != null) {
			sql.append("AND p.quantidade <= ? ");
		}

		try (PreparedStatement pstmt = conexao.prepareStatement(sql.toString())) {
			int paramIndex = 1;

			if (termo != null && !termo.isEmpty()) {
				String termoBusca = "%" + termo.toLowerCase() + "%";
				pstmt.setString(paramIndex++, termoBusca);
				pstmt.setString(paramIndex++, termoBusca);
			}
			if (categoria != null) {
				pstmt.setString(paramIndex++, categoria.toLowerCase());
			}
			if (localizacao != null) {
				pstmt.setString(paramIndex++, localizacao.toLowerCase());
			}
			if (caixa != null) {
				pstmt.setString(paramIndex++, caixa.toLowerCase());
			}
			if (quantidadeMin != null) {
				pstmt.setInt(paramIndex++, quantidadeMin);
			}
			if (quantidadeMax != null) {
				pstmt.setInt(paramIndex++, quantidadeMax);
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					produtos.add(mapearResultado(rs));
				}
			}
		}

		return produtos;
	}

	/**
	 * Retorna todas as categorias cadastradas.
	 *
	 * @return Lista de categorias.
	 * @throws SQLException Em caso de erro ao buscar categorias.
	 */
	public List<String> getTodasCategorias() throws SQLException {
		List<String> categorias = new ArrayList<>();
		String sql = "SELECT DISTINCT p.categoria FROM Produtos p WHERE p.categoria IS NOT NULL AND p.categoria != ''";
		try (PreparedStatement pstmt = conexao.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				categorias.add(rs.getString("categoria"));
			}
		}
		return categorias;
	}

	/**
	 * Retorna todas as localizações cadastradas.
	 *
	 * @return Lista de localizações.
	 * @throws SQLException Em caso de erro ao buscar localizações.
	 */
	public List<String> getTodasLocalizacoes() throws SQLException {
		List<String> localizacoes = new ArrayList<>();
		String sql = "SELECT DISTINCT p.localizacao FROM Produtos p WHERE p.localizacao IS NOT NULL AND p.localizacao != ''";
		try (PreparedStatement pstmt = conexao.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				localizacoes.add(rs.getString("localizacao"));
			}
		}
		return localizacoes;
	}

	/**
	 * Retorna todas as caixas cadastradas.
	 *
	 * @return Lista de caixas.
	 * @throws SQLException Em caso de erro ao buscar caixas.
	 */
	public List<String> getTodasCaixas() throws SQLException {
		List<String> caixas = new ArrayList<>();
		String sql = "SELECT DISTINCT c.nomeCaixa FROM Caixas c JOIN Produtos p ON c.id = p.caixaId WHERE c.nomeCaixa " +
				"IS NOT NULL AND c.nomeCaixa != ''";
		try (PreparedStatement pstmt = conexao.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				caixas.add(rs.getString("nomeCaixa"));
			}
		}
		return caixas;
	}

	/**
	 * Mapeia o resultado de uma consulta para um objeto Produto.
	 *
	 * @param rs O ResultSet da consulta.
	 * @return  Objeto Produto mapeado.
	 * @throws SQLException Em caso de erro ao mapear o resultado.
	 */
	protected Produto mapearResultado(ResultSet rs) throws SQLException {
		Produto produto = new Produto();
		produto.setId(rs.getInt("id"));
		produto.setNome(rs.getString("nome"));
		produto.setQuantidade(rs.getInt("quantidade"));
		produto.setCategoria(rs.getString("categoria"));
		produto.setCaixaNome(rs.getString("nomeCaixa"));
		produto.setLocalizacao(rs.getString("localizacao"));
		return produto;
	}
}
