package PresentationLayer;

import DataLayer.SentimentLoader;
import DataLayer.TweetAnalyzer;
import DataLayer.TweetProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AppGUI extends JFrame {

    private JTextArea resultArea;
    private JButton selectFileButton;
    private JButton drawMapButton; // Кнопка для анализа и рисования карты
    private File selectedFile; // Хранит выбранный файл

    public AppGUI() {
        // Настройка основного окна
        setTitle("Анализ твитов и рисование карты");
        setSize(800, 600); // Увеличили размер окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Создаем панель для компонентов
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Текстовое поле для вывода результатов
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Кнопка для выбора файла
        selectFileButton = new JButton("Выбрать файл с твитами");
        selectFileButton.addActionListener(e -> selectFile());
        buttonPanel.add(selectFileButton);

        // Кнопка для анализа твитов и рисования карты
        drawMapButton = new JButton("Нарисовать карту");
        drawMapButton.addActionListener(e -> analyzeAndDrawMap());
        buttonPanel.add(drawMapButton);

        // Добавляем панель с кнопками на основную панель
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Добавляем панель на окно
        add(panel);
    }

    private void selectFile() {
        // Создаем JFileChooser для выбора файла
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите файл с твитами");

        // Показываем диалог выбора файла
        int userSelection = fileChooser.showOpenDialog(this);

        // Если пользователь выбрал файл
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();

            // Проверяем, что файл текстовый
            if (isTextFile(selectedFile)) {
                JOptionPane.showMessageDialog(this, "Выбран файл: " + selectedFile.getName(), "Файл выбран", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Выбранный файл не является текстовым (.txt)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                selectedFile = null; // Сбрасываем выбранный файл
            }
        }
    }

    private boolean isTextFile(File file) {
        // Проверяем расширение файла
        String fileName = file.getName();
        return fileName.toLowerCase().endsWith(".txt");
    }

    private void analyzeAndDrawMap() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Файл не выбран!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Дополнительная проверка, что файл текстовый
        if (!isTextFile(selectedFile)) {
            JOptionPane.showMessageDialog(this, "Выбранный файл не является текстовым (.txt)!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Очищаем текстовое поле перед новым анализом
            resultArea.setText("");

            // Загружаем данные и анализируем твиты
            SentimentLoader sentimentLoader = new SentimentLoader();
            Map<String, Double> sentiments = sentimentLoader.loadSentiments("sentiments.csv");

            TweetProcessor tweetProcessor = new TweetProcessor();
            TweetAnalyzer tweetAnalyzer = new TweetAnalyzer(tweetProcessor);

            // Используем выбранный файл
            Map<String, Double> coordinateSentiments = tweetAnalyzer.analyzeTweetsFromFile(selectedFile.getAbsolutePath(), sentiments);

            // Выводим результаты в текстовое поле
            for (Map.Entry<String, Double> entry : coordinateSentiments.entrySet()) {
                String sentimentValue = (entry.getValue() == null) ? "null" : entry.getValue().toString();
                resultArea.append(entry.getKey() + " " + sentimentValue + "\n");
            }

            // Сообщение о том, что карта будет реализована позже
            JOptionPane.showMessageDialog(this, "Анализ твитов завершен. Карта будет реализована позже.", "Информация", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Ошибка при работе с файлами: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Произошла непредвиденная ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Запуск GUI в потоке обработки событий
        SwingUtilities.invokeLater(() -> {
            AppGUI app = new AppGUI();
            app.setVisible(true);
        });
    }
}