package com.controleestoque.estoque;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import com.controleestoque.util.ConfiguracaoBanco;

/**
 * Classe responsável pela inicialização e criação do banco de dados, incluindo
 * diretórios e tabelas necessárias.
 */
public class InicializacaoBancoDados {

    private static final String DATABASE_URL;
    private static final String DATABASE_DIR;
    private static final String DATABASE_NOME;

    static {
        Properties properties = ConfiguracaoBanco.getProperties();
        DATABASE_DIR = properties.getProperty("database.dir");
        DATABASE_NOME = properties.getProperty("database.nome");
        DATABASE_URL = "jdbc:derby:" + DATABASE_DIR + "/" + DATABASE_NOME + ";create=true";
    }

    /**
     * Cria o banco de dados e as tabelas se não existir.
     */
    public static void criarBancoSeNecessario() {
        try {
            criarDiretorioSeNecessario(DATABASE_DIR);
            try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
                if (conn != null) {
                    criarTabelasSeNecessario(conn);
                }
            } catch (SQLException e) {
                System.out.println("Erro ao criar o banco de dados: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Erro ao criar o diretório para o banco de dados: " + e.getMessage());
        }
    }

    /**
     * Cria o diretório do banco de dados se não existir.
     *
     * @param diretorio O caminho do diretório a ser criado.
     * @throws Exception Em caso de erro na criação do diretório.
     */
    private static void criarDiretorioSeNecessario(String diretorio) throws Exception {
        if (!Files.exists(Paths.get(diretorio))) {
            Files.createDirectories(Paths.get(diretorio));
        }
    }

    /**
     * Cria as tabelas no banco de dados se não existir.
     *
     * @param conexao A conexão ativa com o banco de dados.
     */
    private static void criarTabelasSeNecessario(Connection conexao) {
        criarTabela(conexao, TabelaCaixas(), "Caixas");
        criarTabela(conexao, TabelaProdutos(), "Produtos");
    }

    /**
     * Executa a criação de uma tabela no banco de dados.
     *
     * @param conexao      A Conexão ativa com o banco de dados.
     * @param sql       O comando SQL para criação da tabela.
     * @param nomeTabela O nome da tabela a ser criada.
     */
    private static void criarTabela(Connection conexao, String sql, String nomeTabela) {
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela '" + nomeTabela + "': " + e.getMessage());
        }
    }

    /**
     *  Estabelece o SQL que será utilizado na criação da tabela Produtos
     *
     * @return Comando SQL para criação da tabela.
     */
    private static String TabelaProdutos() {
        return "CREATE TABLE Produtos ("
                + "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                + "nome VARCHAR(255) NOT NULL, "
                + "quantidade INT NOT NULL, "
                + "categoria VARCHAR(255), "
                + "caixaID INT, "
                + "localizacao VARCHAR(255), "
                + "FOREIGN KEY (caixaID) REFERENCES Caixas(id) ON DELETE SET NULL)";
    }

    /**
     *  Estabelece o SQL que será utilizado na criação da tabela Caixas
     *
     * @return Comando SQL para criação da tabela.
     */
    private static String TabelaCaixas() {
        return "CREATE TABLE Caixas ("
                + "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                + "nomeCaixa VARCHAR(255), "
                + "localizacao VARCHAR(255))";
    }
}
