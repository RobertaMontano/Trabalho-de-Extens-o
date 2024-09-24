package com.controleestoque.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ListCell;

/**
 * Classe responsável por gerenciar a exibição de um texto de prompt em uma célula de ComboBox.
 * Resolve o problema de exibição de texto quando nenhum item é selecionado no ComboBox.
 *
 * @param <T> O tipo de item que será exibido no ComboBox.
 */
public class ComboBoxPromptCell<T> extends ListCell<T> {

	private final StringProperty textoPrompt = new SimpleStringProperty();

	/**
	 * Construtor que inicializa a célula com o texto de prompt fornecido.
	 *
	 * @param textoPrompt O texto a ser exibido como prompt quando a célula está vazia.
	 */
	public ComboBoxPromptCell(String textoPrompt) {

		this.textoPrompt.addListener((obs, oldText, newText) -> {
			if (isEmpty() || getItem() == null) {
				setText(newText);
			}
		});
		setTextoPrompt(textoPrompt);
	}

	// Propriedade para acessar o texto do prompt
	public StringProperty textoPromptProperty() {
		return textoPrompt;
	}

	// Define o texto do prompt
	public final void setTextoPrompt(String textoPrompt) {
		textoPromptProperty().set(textoPrompt);
	}

	// Retorna o texto de prompt definido
	public final String getTextoPrompt() {
		return textoPromptProperty().get();
	}

	//Exibe o texto do prompt se o item estiver vazio.
	@Override
	protected void updateItem(T item, boolean empty) {

		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(getTextoPrompt());
		} else {
			setText(item != null ? item.toString() : null);
		}
	}
}
