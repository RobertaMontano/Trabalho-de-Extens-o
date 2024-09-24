package com.controleestoque.servico;

import com.controleestoque.estoque.Produto;
import com.controleestoque.util.AlertaUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe responsável por gerenciar o carregamento dos dados dos produtos.
 * Utiliza o serviço de produto para listar e atualizar a tabela com os dados.
 */
public class GerenciadorDadosProduto {

    private ServicoProduto servicoProduto;
    /**
     * Gerencia o carregamento e atualização dos dados dos produtos na tabela.
     */
    public GerenciadorDadosProduto(ServicoProduto servicoProduto) {
        this.servicoProduto = servicoProduto;
    }

    /**
     * Carrega todos os produtos do banco de dados e atualiza a tabela.
     * Os dados são adicionados ao ObservableList para exibição na tabela.
     *
     * @param produtosData É um ObservableList onde os produtos serão armazenados.
     * @param tabela    É a tabela que será atualizada com os dados carregados.
     */
    public void carregarDados(ObservableList<Produto> produtosData, TableView<Produto> tabela) {
        try {
            List<Produto> produtos = servicoProduto.listarProdutos();
            produtosData.clear();
            produtosData.addAll(produtos);
            tabela.setItems(produtosData);
            tabela.refresh();
        } catch (SQLException e) {
            AlertaUtils.mostrarAlertaErro("Erro!", "Erro ao carregar os dados: " + e.getMessage());
        }
    }

}
