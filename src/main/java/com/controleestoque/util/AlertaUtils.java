package com.controleestoque.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;
/**
 * Classe utilitária para exibição de alertas e caixas de diálogo no sistema.
 */
public class AlertaUtils {

    // Caminhos dos ícones
    private static final String ICON_CONFIRMACAO = "/imagens/verificacao.png";
    private static final String ICON_ERRO = "/imagens/erro.png";
    private static final String ICON_INFORMACAO = "/imagens/informacao.png";

    // Exibe um alerta de confirmação (ok ou cancelar)
    public static Optional<ButtonType> mostrarConfirmacao(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        definirIcone(alert, ICON_CONFIRMACAO);
        return alert.showAndWait();
    }

    //Exibe um alerta de erro
    public static void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        definirIcone(alert, ICON_ERRO);
        alert.showAndWait();
    }
    // Exibe um alerta informativo
    public static void mostrarAlertaInformacao(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        definirIcone(alert, ICON_INFORMACAO);
        alert.showAndWait();
    }

     //Define um ícone para a caixa de diálogo.
    private static void definirIcone(Alert alert, String iconPath) {
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            alertStage.getIcons().add(new Image(AlertaUtils.class.getResourceAsStream(iconPath)));
        } catch (Exception e) {
            System.err.println("Erro ao definir o ícone do alerta: " + e.getMessage());
        }
    }
}
