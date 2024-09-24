package com.controleestoque.servico;

import com.controleestoque.estoque.ConexaoBanco;
import com.controleestoque.estoque.Produto;
import com.controleestoque.estoque.ProdutoDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
/**
 * Classe de serviço para manipulação e gerenciamento de produtos no banco de dados.
 * Utiliza a classe ProdutoDAO para realizar operações CRUD e fornece funcionalidades adicionais.
 */
public class ServicoProduto {

	private ProdutoDAO produtoDAO;
    private Connection conexao;

    /**
     * Construtor que inicializa o ProdutoDAO e a conexão com o banco de dados.
     *
     * @param produtoDAO É a instância de ProdutoDAO para acesso aos dados de produto.
     * @param conexao É a conexão inicializada com o banco de dados.
     */
    public ServicoProduto(ProdutoDAO produtoDAO, Connection conexao) {
        this.produtoDAO = produtoDAO;
        this.conexao = conexao;
    }

    // Obtém a conexão com o banco de dados

    public Connection getConexao() {
        try {
            if (this.conexao == null || this.conexao.isClosed()) {
                this.conexao = ConexaoBanco.getConexao();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.conexao;
    }

    // CRUD de Produto utilizando a instância de ProdutoDAO.

    public List<Produto> listarProdutos() throws SQLException {
        return produtoDAO.listar();
        }

    public List<Produto> buscarProdutosComFiltros(String termo, String categoria, String localizacao, String caixa,
            Integer quantidadeMin, Integer quantidadeMax) throws SQLException {
        return produtoDAO.buscarProdutosComFiltros(termo, categoria, localizacao, caixa, quantidadeMin, quantidadeMax);
    }

    public void adicionar(Produto produto) throws SQLException {
        produtoDAO.adicionar(produto);
    }

    public void remover(Produto produto) throws SQLException {
        produtoDAO.remover(produto);
    }

    public void alterar(Produto produto) throws SQLException {
        produtoDAO.alterar(produto);
    }

    public void alterarMassa(List<Produto> produtos) throws SQLException {
        produtoDAO.alterarMassa(produtos);
    }
    
    public List<String> getTodasCategorias() throws SQLException {
        return produtoDAO.getTodasCategorias();
    }

    public List<String> getTodasLocalizacoes() throws SQLException {
        return produtoDAO.getTodasLocalizacoes();
    }

    public List<String> getTodasCaixas() throws SQLException {
        return produtoDAO.getTodasCaixas();
    }
}
