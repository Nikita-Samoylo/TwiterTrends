package Presentation;

import Data.TweetAnalysisService;
import Map.State;
import Map.PolygonDrawer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppGUI extends Application {
    private TextArea resultArea;
    private Button selectFileButton;
    private Button analyzeButton;
    private Button showMapButton;
    private File selectedFile;
    private TweetAnalysisService tweetAnalysisService;
    private Map<String, Double> stateSentiments;
    private PolygonDrawer polygonDrawer;
    private List<State> states;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // настройка окна
            primaryStage.setTitle("Анализ твитов по штатам");
            primaryStage.setWidth(1000);
            primaryStage.setHeight(700);

            tweetAnalysisService = new TweetAnalysisService();

            states = tweetAnalysisService.loadStates();

            stateSentiments = new HashMap<>();
            polygonDrawer = new PolygonDrawer(states, stateSentiments);

            // панель
            TabPane tabPane = new TabPane();
            Tab analysisTab = new Tab("Анализ твитов");
            analysisTab.setClosable(false);
            analysisTab.setContent(createAnalysisTabContent());

            // карта
            Tab mapTab = new Tab("Карта США");
            mapTab.setClosable(false);
            mapTab.setContent(createMapTabContent());
            tabPane.getTabs().addAll(analysisTab, mapTab);

            // стиль
            Scene scene = new Scene(tabPane);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            showErrorAlert("Ошибка запуска", "Не удалось запустить приложение: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // создание вкладок
    private Node createAnalysisTabContent() {
        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("root");

        HBox topPanel = new HBox(10);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.getStyleClass().add("hbox");

        selectFileButton = new Button("Выбрать файл");
        selectFileButton.getStyleClass().add("button");

        analyzeButton = new Button("Анализировать");
        analyzeButton.getStyleClass().add("button");

        showMapButton = new Button("Показать карту");
        showMapButton.getStyleClass().add("button");

        selectFileButton.setOnAction(e -> selectFile());
        analyzeButton.setOnAction(e -> analyzeTweets());
        showMapButton.setOnAction(e -> showMap());

        topPanel.getChildren().addAll(selectFileButton, analyzeButton, showMapButton);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.getStyleClass().add("text-area");

        borderPane.setTop(topPanel);
        borderPane.setCenter(resultArea);

        return borderPane;
    }

    // диалог выбора файла
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с твитами");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt", "*.csv")
        );
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            resultArea.appendText("Выбран файл: " + selectedFile.getName() + "\n");
        }
    }

    // анализ твита
    private void analyzeTweets() {
        if (selectedFile == null) {
            showErrorAlert("Ошибка", "Файл не выбран!");
            return;
        }

        try {
            resultArea.clear();
            stateSentiments = tweetAnalysisService.analyzeTweetsByState(selectedFile.getAbsolutePath());

            if (stateSentiments.isEmpty()) {
                showInfoAlert("Результат", "Не найдено твитов с определёнными штатами");
                return;
            }

            // обновление отрисовки карты
            polygonDrawer = new PolygonDrawer(states, stateSentiments);
            TabPane tabPane = (TabPane) resultArea.getScene().getRoot();
            ((Tab)tabPane.getTabs().get(1)).setContent(polygonDrawer);

            // вывод результатов
            StringBuilder sb = new StringBuilder();
            sb.append("Результаты анализа по штатам:\n\n");
            sb.append(String.format("%-15s %s\n", "Штат", "Средний сентимент"));
            sb.append("----------------------------------------\n");

            stateSentiments.forEach((stateName, sentiment) -> {
                sb.append(String.format("%-15s %-20s\n", stateName, sentiment.toString()));
            });

            resultArea.setText(sb.toString());
            showInfoAlert("Анализ завершен",
                    "Проанализировано твитов для " + stateSentiments.size() + " штатов");

        } catch (Exception e) {
            showErrorAlert("Ошибка анализа", e.getMessage());
            e.printStackTrace();
        }
    }

    // вкладка с картой
    private void showMap() {
        TabPane tabPane = (TabPane) resultArea.getScene().getRoot();
        tabPane.getSelectionModel().select(1);
    }

    // содержимое карты
    private Node createMapTabContent() {
        polygonDrawer.getStyleClass().add("map-pane");
        return polygonDrawer;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}