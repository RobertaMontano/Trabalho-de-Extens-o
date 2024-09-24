package com.controleestoque.estoque;
/**
 * Exceção personalizada para os erros de validação.
 */
public class ValidacaoException extends Exception {
	private static final long serialVersionUID = 1L;

	public ValidacaoException(String message) {
		super(message);
	}

	public ValidacaoException(String message, Throwable cause) {
		super(message, cause);
	}
}
