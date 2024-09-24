package com.controleestoque;

import com.controleestoque.estoque.ConexaoBanco;
import com.controleestoque.estoque.InicializacaoBancoDados;
import com.controleestoque.estoque.ProdutoDAO;
import com.controleestoque.gui.Janela;
import com.controleestoque.servico.ServicoProduto;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;


import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        try {
            String iconPath = "/imagens/caixas.png";
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));
            inicializarBancoDeDados();

            if (connection != null) {
                ProdutoDAO produtoDAO = new ProdutoDAO(connection);
                ServicoProduto servicoProduto = new ServicoProduto(produtoDAO, connection);

//                // Gerar Dados
//                if (servicoProduto.listarProdutos().isEmpty()) {
//                    GeradorDados.gerarProdutosEmMassa(connection, 20);
//                    System.out.println("Produtos gerados em massa com sucesso.");
//                }

                new Janela(primaryStage, servicoProduto);
            } else {
                System.err.println("Conexão com o banco de dados não foi estabelecida.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro inesperado ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void inicializarBancoDeDados() {
        try {
            InicializacaoBancoDados.criarBancoSeNecessario();
            connection = ConexaoBanco.getConexao();

            if (connection != null && !connection.isClosed()) {
                System.out.println("Banco de dados inicializado e conexão estabelecida com sucesso.");
            } else {
                System.err.println("Falha ao inicializar a conexão com o banco de dados.");
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("Erro de SQL ao inicializar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro inesperado ao inicializar o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (connection != null) {
            ConexaoBanco.fecharConexao(connection);
            System.out.println("Conexão com o banco de dados fechada com sucesso.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
