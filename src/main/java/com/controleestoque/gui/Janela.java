package com.controleestoque.gui;

import java.sql.SQLException;

import com.controleestoque.servico.GerenciadorDadosProduto;
import com.controleestoque.servico.GerenciadorFiltroProduto;
import com.controleestoque.servico.ServicoProduto;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Classe responsável pela configuração e exibição da interface principal do sistema.
 */
public class Janela {

    private BorderPane layoutPrincipal;
    private Menu menu;
    private Tabela tabela;
    private GerenciadorDadosProduto gerenciadorDadosProduto;
    private GerenciadorFiltroProduto gerenciadorFiltroProduto;
    private ControleBotoes controleBotoes;

    /**
     * Construtor da classe Janela. Inicializa e configura a interface gráfica.
     *
     * @param janelaPrincipal    A janela principal do JavaFX.
     * @param servicoProduto  O serviço responsável pelas operações de produtos.
     * @throws SQLException   Em caso de erro ao configurar a interface.
     */
    public Janela(Stage janelaPrincipal, ServicoProduto servicoProduto) throws SQLException {
        try {
            inicializarComponentes(servicoProduto);
            configurarLayout();
            configurarBotoes(servicoProduto);
            configurarCena(janelaPrincipal);
            janelaPrincipal.show();
        } catch (SQLException e) {
            System.err.println("Erro ao configurar a janela: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Inicializa os componentes da interface.
     *
     * @param servicoProduto  O serviço responsável pelas operações de produtos.
     * @throws SQLException   Em caso de erro ao inicializar os componentes.
     */
    private void inicializarComponentes(ServicoProduto servicoProduto) throws SQLException {
        layoutPrincipal = new BorderPane();
        menu = new Menu();
        tabela = new Tabela(servicoProduto);

        gerenciadorDadosProduto = new GerenciadorDadosProduto(servicoProduto);
        gerenciadorFiltroProduto = new GerenciadorFiltroProduto(servicoProduto, gerenciadorDadosProduto,
                tabela.getCampoPesquisa(), tabela.getCampoQuantidadeMinima(), tabela.getCampoQuantidadeMaxima(),
                tabela.getFiltroCategoria(), tabela.getFiltroLocalizacao(), tabela.getFiltroCaixa(),
                tabela.getProdutosData(), tabela.getTabela());

        controleBotoes = new ControleBotoes(servicoProduto, tabela, layoutPrincipal);
    }

    /**
     * Configura o layout principal da interface.
     */
    private void configurarLayout() {
        layoutPrincipal.setLeft(menu.getMenuLateral());
        layoutPrincipal.setCenter(tabela.getTabelaContainer());
        String imagemBackground = getClass().getResource("/imagens/background.png").toExternalForm();
        layoutPrincipal.setStyle("-fx-background-image: url('" + imagemBackground + "'); -fx-background-size: cover;");
    }

    /**
     * Configura os botões da interface com suas funcionalidades.
     *
     * @param servicoProduto  O serviço responsável pelas operações de produtos.
     * @throws SQLException   Em caso de erro ao configurar os botões.
     */
    private void configurarBotoes(ServicoProduto servicoProduto) throws SQLException {
        controleBotoes.configurarBotoes(menu.getBtnAddProduto(), menu.getBtnRemProduto(), menu.getBtnAlterarProduto(),
                tabela.getBtnAplicarFiltros(), tabela.getBtnLimparFiltros(), menu.getBtnTotalPorCategoria(),
                menu.getBtnAlternarGrafico(), menu.getBtnExibirTabela());
    }

    /**
     * Configura as propriedades da cena principal.
     *
     * @param janelaPrincipal  A janela principal do JavaFX.
     */
    private void configurarCena(Stage janelaPrincipal) {
        Scene scene = new Scene(layoutPrincipal, 1300, 900);
        janelaPrincipal.setScene(scene);
        janelaPrincipal.setTitle("Sistema de Gerenciamento de Estoque");
    }
}
