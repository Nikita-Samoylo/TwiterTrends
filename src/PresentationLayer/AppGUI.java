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
    private Button drawMapButton;
    private File selectedFile;
    private TweetAnalysisService tweetAnalysisService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Анализ твитов и рисование карты");
        primaryStage.setWidth(1000); // Увеличиваем ширину окна
        primaryStage.setHeight(700); // Увеличиваем высоту окна

        // Инициализация сервиса для анализа твитов
        tweetAnalysisService = new TweetAnalysisService();

        // Создаем корневой контейнер
        BorderPane root = new BorderPane();

        // Текстовое поле для вывода результатов
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true); // Перенос текста
        resultArea.setStyle("-fx-font-size: 14px;"); // Увеличиваем размер шрифта

        // Добавляем текстовое поле в ScrollPane
        ScrollPane scrollPane = new ScrollPane(resultArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);


        // Панель для кнопок
        HBox buttonPanel = new HBox(20); // Горизонтальный контейнер с отступом 20
        buttonPanel.setAlignment(Pos.CENTER); // Центрируем кнопки

        // Кнопка для выбора файла
        selectFileButton = new Button("Выбрать файл с твитами");
        selectFileButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;"); // Стиль кнопки

        // Кнопка для анализа твитов и рисования карты
        drawMapButton = new Button("Нарисовать карту");
        drawMapButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;"); // Стиль кнопки

        // Добавляем кнопки на панель
        buttonPanel.getChildren().addAll(selectFileButton, drawMapButton);

        // Добавляем панель с кнопками на основную панель
        root.setBottom(buttonPanel);

        // Устанавливаем сцену и показываем окно
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Обработчики событий для кнопок
        selectFileButton.setOnAction(e -> selectFile(primaryStage));
        drawMapButton.setOnAction(e -> analyzeAndDrawMap());
    }

    private void selectFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с твитами");

        // Устанавливаем фильтр для текстовых файлов
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Текстовые файлы (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Показываем диалог выбора файла
        selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Файл выбран");
            alert.setHeaderText(null);
            alert.setContentText("Выбран файл: " + selectedFile.getName());
            alert.showAndWait();
        }
    }

    private void analyzeAndDrawMap() {
        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Файл не выбран!");
            alert.showAndWait();
            return;
        }

        try {
            // Очищаем текстовое поле перед новым анализом
            resultArea.clear();

            // Анализируем твиты с помощью сервиса
            Map<String, Double> coordinateSentiments = tweetAnalysisService.analyzeTweets(selectedFile.getAbsolutePath());

            // Выводим результаты в текстовое поле
            for (Map.Entry<String, Double> entry : coordinateSentiments.entrySet()) {
                String sentimentValue = (entry.getValue() == null) ? "null" : entry.getValue().toString();
                resultArea.appendText(entry.getKey() + " " + sentimentValue + "\n");
            }

            // Сообщение о том, что карта будет реализована позже
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информация");
            alert.setHeaderText(null);
            alert.setContentText("Анализ твитов завершен. Карта будет реализована позже.");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Произошла ошибка: " + e.getMessage());
            alert.showAndWait();
        }
    }
}