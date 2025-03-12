import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TweetAnalyzer {
    private final TweetProcessor tweetProcessor;

    public TweetAnalyzer(TweetProcessor tweetProcessor) {
        if (tweetProcessor == null) {
            throw new IllegalArgumentException("TweetProcessor не может быть null");
        }
        this.tweetProcessor = tweetProcessor;
    }

    public Map<String, Double> analyzeTweetsFromFile(String filePath, Map<String, Double> sentiments) throws IOException {
        if (filePath == null || sentiments == null) {
            throw new IllegalArgumentException("Аргументы не могут быть null");
        }

        File inputFile = new File(filePath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new FileNotFoundException("Файл не найден: " + filePath);
        }

        // Используем LinkedHashMap для сохранения порядка
        Map<String, Double> coordinateSentiments = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 4) {
                    String coordinatesPart = parts[0]; // Координаты находятся в первой части
                    String tweetText = parts[3]; // Текст твита находится в четвертой части

                    // Проверка корректности координат
                    if (!coordinatesPart.matches("\\[-?\\d+\\.\\d+, -?\\d+\\.\\d+\\]")) {
                        System.err.println("Некорректные координаты в строке: " + line);
                        continue; // Пропустить строку, если координаты некорректны
                    }

                    // Анализ sentiment
                    Double sentiment = tweetProcessor.analyzeTweetSentiment(tweetText, sentiments);

                    // Сохраняем результат по координатам
                    coordinateSentiments.put(coordinatesPart, sentiment);
                } else {
                    System.err.println("Строка содержит недостаточно данных: " + line);
                }
            }
        }

        return coordinateSentiments;
    }
}