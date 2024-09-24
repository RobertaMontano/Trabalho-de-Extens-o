package com.controleestoque.estoque;

/**
 * Classe que representa uma caixa de armazenamento no estoque.
 * Cada caixa possui um identificador único, um nome (opcional) e uma localização (opcional).
 */
public class Caixa {
	private int id;
	private String nomeCaixa;
	private String localizacao;

	/**
	 * Construtor padrão que inicializa a caixa sem dados.
	 */
	public Caixa() {

	}

	/**
	 * Construtor que inicializa a caixa com nome e localização.
	 *
	 * @param nomeCaixa   Nome da caixa.
	 * @param localizacao Localização da caixa.
	 */
	public Caixa(String nomeCaixa, String localizacao) {
		this.nomeCaixa = nomeCaixa;
		this.localizacao = localizacao;
	}

	// Getters e Setters da Caixa
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNomeCaixa() {
		return nomeCaixa;
	}

	public void setNomeCaixa(String nomeCaixa) {
		this.nomeCaixa = nomeCaixa;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
}
