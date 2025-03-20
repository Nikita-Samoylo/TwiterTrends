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
                String[] parts = line.split("\t");
                if (parts.length >= 4) {
                    String coordinatesPart = parts[0];
                    String tweetText = parts[3];

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