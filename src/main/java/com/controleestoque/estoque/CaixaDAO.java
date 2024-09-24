package com.controleestoque.estoque;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pelo acesso aos dados da entidade Caixa no banco de dados.
 */
public class CaixaDAO {

    private Connection conexao;

    /**
     * Construtor que recebe uma conexão com o banco de dados.
     *
     * @param conexao A conexão com o banco de dados.
     */
    public CaixaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Adiciona uma nova caixa no banco de dados.
     *
     * @param caixa A caixa a ser adicionada.
     * @throws SQLException Em caso de erro ao adicionar a caixa.
     */
    public void adicionar(Caixa caixa) throws SQLException {
        String sql = "INSERT INTO Caixas (nomeCaixa, localizacao) VALUES (?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            conexao.setAutoCommit(false);
            pstmt.setString(1, caixa.getNomeCaixa());
            pstmt.setString(2, caixa.getLocalizacao());
            pstmt.executeUpdate();
            conexao.commit();
        } catch (SQLException e) {
            conexao.rollback();
            throw new SQLException("Erro ao adicionar caixa: " + e.getMessage(), e);
        } finally {
            conexao.setAutoCommit(true);
        }
    }

    /**
     * Altera os dados de uma caixa existente no banco de dados.
     *
     * @param caixa A caixa com os novos dados.
     * @throws SQLException Em caso de erro ao alterar a caixa.
     */
    public void alterar(Caixa caixa) throws SQLException {
        String sql = "UPDATE Caixas SET nomeCaixa = ?, localizacao = ? WHERE id = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            conexao.setAutoCommit(false);
            pstmt.setString(1, caixa.getNomeCaixa());
            pstmt.setString(2, caixa.getLocalizacao());
            pstmt.setInt(3, caixa.getId());
            pstmt.executeUpdate();
            conexao.commit();
        } catch (SQLException e) {
            conexao.rollback();
            throw new SQLException("Erro ao alterar caixa: " + e.getMessage(), e);
        } finally {
            conexao.setAutoCommit(true);
        }
    }

    /**
     * Exclui uma caixa do banco de dados.
     *
     * @param caixa A caixa a ser excluída.
     * @throws SQLException Em caso de erro ao excluir a caixa.
     */
    public void excluir(Caixa caixa) throws SQLException {
        String sql = "DELETE FROM Caixas WHERE id = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            conexao.setAutoCommit(false);
            pstmt.setInt(1, caixa.getId());
            pstmt.executeUpdate();
            conexao.commit();
        } catch (SQLException e) {
            conexao.rollback();
            throw new SQLException("Erro ao excluir caixa: " + e.getMessage(), e);
        } finally {
            conexao.setAutoCommit(true);
        }
    }

    /**
     * Lista todas as caixas cadastradas no banco de dados.
     *
     * @return Lista de caixas cadastradas.
     * @throws SQLException Em caso de erro ao listar as caixas.
     */
    public List<Caixa> listar() throws SQLException {
        List<Caixa> caixas = new ArrayList<>();
        String sql = "SELECT * FROM Caixas";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                caixas.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar caixas: " + e.getMessage(), e);
        }
        return caixas;
    }

    /**
     * Busca o identificador de uma caixa a partir do nome.
     *
     * @param nomeCaixa O nome da caixa a ser buscada.
     * @return ID da caixa encontrada ou 0 caso não seja encontrada.
     * @throws SQLException Em caso de erro na busca.
     */
    public int buscarIdCaixaPorNome(String nomeCaixa) throws SQLException {
        String sql = "SELECT id FROM Caixas WHERE nomeCaixa = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nomeCaixa);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar ID do caixa pelo nome: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Mapeia o resultado de uma consulta para um objeto do tipo Caixa.
     *
     * @param rs É o ResultSet da consulta.
     * @return Objeto Caixa mapeado.
     * @throws SQLException Em caso de erro no mapeamento.
     */
    private Caixa mapearResultado(ResultSet rs) throws SQLException {
        Caixa caixa = new Caixa();
        caixa.setId(rs.getInt("id"));
        caixa.setNomeCaixa(rs.getString("nomeCaixa"));
        caixa.setLocalizacao(rs.getString("localizacao"));
        return caixa;
    }
}
