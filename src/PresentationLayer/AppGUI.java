package PresentationLayer;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

public class AppGUI extends Application {

    private TextArea resultArea;
    private Button selectFileButton;
    private Button analyzeButton; // Переименовано для ясности
    private File selectedFile;
    private TweetAnalysisService tweetAnalysisService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Анализ твитов по штатам"); // Обновлен заголовок
            primaryStage.setWidth(1000);
            primaryStage.setHeight(700);

            // Инициализация сервиса с обработкой ошибок
            tweetAnalysisService = new TweetAnalysisService();

            BorderPane root = new BorderPane();

            // Текстовое поле с улучшенными настройками
            resultArea = new TextArea();
            resultArea.setEditable(false);
            resultArea.setWrapText(true);
            resultArea.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");

            ScrollPane scrollPane = new ScrollPane(resultArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            root.setCenter(scrollPane);

            // Панель кнопок
            HBox buttonPanel = new HBox(20);
            buttonPanel.setAlignment(Pos.CENTER);
            buttonPanel.setStyle("-fx-padding: 10px;");

            selectFileButton = new Button("Выбрать файл с твитами");
            selectFileButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");

            analyzeButton = new Button("Анализировать твиты"); // Переименовано
            analyzeButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");

            buttonPanel.getChildren().addAll(selectFileButton, analyzeButton);
            root.setBottom(buttonPanel);

            // Настройка сцены
            Scene scene = new Scene(root);
            try {
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            } catch (NullPointerException e) {
                System.err.println("Файл стилей не найден");
            }

            primaryStage.setScene(scene);
            primaryStage.show();

            // Обработчики событий
            selectFileButton.setOnAction(e -> selectFile(primaryStage));
            analyzeButton.setOnAction(e -> analyzeTweets()); // Обновленный обработчик

        } catch (Exception e) {
            showErrorAlert("Ошибка запуска", "Не удалось запустить приложение: " + e.getMessage());
        }
    }

    private void selectFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с твитами");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));

        selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            showInfoAlert("Файл выбран", "Выбран файл: " + selectedFile.getAbsolutePath());
        }
    }

    private void analyzeTweets() {
        if (selectedFile == null) {
            showErrorAlert("Ошибка", "Файл не выбран!");
            return;
        }

        try {
            resultArea.clear();
            Map<String, Double> stateSentiments = tweetAnalysisService.analyzeTweetsByState(selectedFile.getAbsolutePath());

            if (stateSentiments.isEmpty()) {
                showInfoAlert("Результат", "Не найдено твитов с определёнными штатами");
                return;
            }

            // Форматированный вывод
            StringBuilder sb = new StringBuilder();
            sb.append("Результаты анализа по штатам:\n\n");
            sb.append(String.format("%-5s %s\n", "Код", "Средний сентимент"));
            sb.append("----------------------------------------\n");

            stateSentiments.forEach((stateCode, sentiment) -> {
                String sentimentStr =
                        String.format("%.2f", sentiment);

                sb.append(String.format("%-5s %-20s\n", stateCode, sentimentStr));
            });

            resultArea.setText(sb.toString());
            showInfoAlert("Анализ завершен",
                    "Проанализировано твитов для " + stateSentiments.size() + " штатов");

        } catch (Exception e) {
            showErrorAlert("Ошибка анализа", e.getMessage());
            e.printStackTrace();
        }
    }


    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}