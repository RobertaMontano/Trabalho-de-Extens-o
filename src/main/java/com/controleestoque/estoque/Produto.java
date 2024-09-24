package com.controleestoque.estoque;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Classe que representa um produto no estoque.
 * Inclui informações como nome, quantidade, categoria (opcional), ID da caixa,
 * nome da caixa (opcional) e localização (opcional).
 */
public class Produto {
	private int id;
	private String nome;
	private int quantidade;
	private String categoria;
	private Integer caixaID;
	private String caixaNome;
	private String localizacao;
	private BooleanProperty selecionado;

	/**
	 * Construtor padrão que inicializa o produto sem dados.
	 */
	public Produto() {
		this.selecionado = new SimpleBooleanProperty(false);
	}

	/**
	 * Construtor que inicializa o produto com todos os atributos fornecidos.
	 *
	 * @param id          Identificador do produto.
	 * @param nome        Nome do produto.
	 * @param quantidade  Quantidade do produto.
	 * @param categoria   Categoria do produto.
	 * @param caixaID     Identificador da caixa associada ao produto.
	 * @param localizacao Localização do produto.
	 */
	public Produto(int id, String nome, int quantidade, String categoria, Integer caixaID, String localizacao) {
		this.id = id;
		this.nome = nome;
		this.quantidade = quantidade;
		this.categoria = categoria;
		this.caixaID = caixaID;
		this.localizacao = localizacao;
		this.selecionado = new SimpleBooleanProperty(false);
	}

	// Getters e Setters de Produto
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	/**
	 * Define o nome do produto.
	 *
	 * @param nome Nome do produto.
	 * @throws IllegalArgumentException Se o nome for nulo ou vazio.
	 */
	public void setNome(String nome) {
		if (nome == null || nome.trim().isEmpty()) {
			throw new IllegalArgumentException("Nome do produto é obrigatório.");
		}
		this.nome = nome;
	}

	public int getQuantidade() {
		return quantidade;
	}

	/**
	 * Define a quantidade do produto.
	 *
	 * @param quantidade Quantidade do produto.
	 * @throws IllegalArgumentException Se a quantidade for menor ou igual a zero.
	 */
	public void setQuantidade(int quantidade) {
		if (quantidade <= 0) {
			throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
		}
		this.quantidade = quantidade;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Integer getCaixaID() {
		return caixaID;
	}

	public void setCaixaID(Integer caixaID) {
		this.caixaID = caixaID;
	}

	public String getCaixaNome() {
		return caixaNome;
	}

	public void setCaixaNome(String caixaNome) {
		this.caixaNome = caixaNome;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}


	public boolean isSelecionado() {
		return selecionado.get();
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado.set(selecionado);
	}

	public BooleanProperty selecionadoProperty() {
		return selecionado;
	}
}
