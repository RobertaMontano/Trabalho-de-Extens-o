package com.controleestoque.servico;

import com.controleestoque.estoque.Produto;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;
/**
 * Classe responsável pelo cálculo de totalização de produtos por categoria.
 */
public class GerenciadorTotalizacaoProduto {

    /**
     * Calcula o total de itens para cada categoria presente na lista de produtos fornecida.
     *
     * @param produtosData A lista observável de produtos a ser analisada.
     * @return Map contendo cada categoria como chave e o total de itens como valor.
     */
    public static Map<String, Integer> calcularTotalPorCategoria(ObservableList<Produto> produtosData) {
    	
        Map<String, Integer> totalPorCategoria = new HashMap<>();
        for (Produto produto : produtosData) {
            String categoria = (produto.getCategoria() == null || produto.getCategoria().trim().isEmpty())
                    ? "Sem Categoria" : produto.getCategoria();

            int quantidade = produto.getQuantidade();

            totalPorCategoria.put(categoria, totalPorCategoria.getOrDefault(categoria, 0) + quantidade);
        }
        return totalPorCategoria;
    }
}
