import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // Загрузка sentiment данных
            SentimentLoader sentimentLoader = new SentimentLoader();
            Map<String, Double> sentiments = sentimentLoader.loadSentiments("C:/Intellij programs/TwiterTrends/sentiments.csv");

            // Инициализация TweetProcessor и TweetAnalyzer
            TweetProcessor tweetProcessor = new TweetProcessor();
            TweetAnalyzer tweetAnalyzer = new TweetAnalyzer(tweetProcessor);

            // Анализ твитов
            String tweetsFilePath = "C:/Intellij programs/TwiterTrends/movie_tweets2014.txt";
            Map<String, Double> coordinateSentiments = tweetAnalyzer.analyzeTweetsFromFile(tweetsFilePath, sentiments);

            // Вывод результатов
            for (Map.Entry<String, Double> entry : coordinateSentiments.entrySet()) {
                String sentimentValue = (entry.getValue() == null) ? "null" : entry.getValue().toString();
                System.out.println(entry.getKey() + " " + sentimentValue);
            }

        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());

        }
    }
}