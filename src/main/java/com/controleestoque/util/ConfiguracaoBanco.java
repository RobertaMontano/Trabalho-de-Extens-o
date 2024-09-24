package com.controleestoque.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Classe responsável por gerenciar o carregamento e acesso às configurações do banco de dados
 * a partir do arquivo 'configuracao_banco.properties'.
 */
public class ConfiguracaoBanco {

    private static Properties properties = new Properties();
    private static boolean arquivoCarregadoComSucesso = false;

    //carrega as propriedades do arquivo configuracao_banco.properties
    static {
        try (InputStream input = ConfiguracaoBanco.class.getClassLoader()
                .getResourceAsStream("configuracao_banco.properties")) {
            if (input == null) {
                System.out.println("O arquivo properties não foi encontrado.");
            } else {
                properties.load(input);
                arquivoCarregadoComSucesso = true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // Retorna as propriedades do arquivo  .properties

    public static String getPropriedade(String chave) {
        if (!arquivoCarregadoComSucesso) {
            System.out.println("Propriedades não foram carregadas corretamente.");
        }
        return properties.getProperty(chave);
    }

    public static String getDatabaseUrl() {
        return getPropriedade("jdbc.url");
    }
    
    public static String getDatabaseDir() {
        return getPropriedade("database.dir");
    }

    public static String getDatabaseNome() {
        return getPropriedade("database.nome");
    }

    public static Properties getProperties() {
        return properties;
    }
}
