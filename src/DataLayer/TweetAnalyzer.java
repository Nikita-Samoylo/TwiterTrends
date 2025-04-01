package DataLayer;

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

        Map<String, Double> coordinateSentiments = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Разделяем строку на части, но учитываем, что в тексте твита могут быть табы
                int firstTab = line.indexOf('\t');
                int secondTab = line.indexOf('\t', firstTab + 1);
                int thirdTab = line.indexOf('\t', secondTab + 1);

                if (firstTab > 0 && secondTab > firstTab && thirdTab > secondTab) {
                    String coordinatesPart = line.substring(0, firstTab);
                    // Пропускаем вторую часть (подчеркивание)
                    String tweetText = line.substring(thirdTab + 1);

                    if (!coordinatesPart.matches("\\[-?\\d+\\.\\d+, -?\\d+\\.\\d+\\]")) {
                        System.err.println("Некорректные координаты в строке: " + line);
                        continue;
                    }

                    Double sentiment = tweetProcessor.analyzeTweetSentiment(tweetText, sentiments);
                    coordinateSentiments.put(coordinatesPart, sentiment);
                } else {
                    System.err.println("Строка содержит недостаточно данных: " + line);
                }
            }
        }

        return coordinateSentiments;
    }
}