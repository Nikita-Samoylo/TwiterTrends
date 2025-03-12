import java.util.*;

public class TweetProcessor {
    public List<String> extractWords(String tweet) {
        List<String> words = new ArrayList<>();
        String[] parts = tweet.split("[^a-zA-Z]+");
        for (String part : parts) {
            if (!part.isEmpty()) {
                words.add(part.toLowerCase());
            }
        }
        return words;
    }

    public Double analyzeTweetSentiment(String tweet, Map<String, Double> sentiments) {
        List<String> words = extractWords(tweet);
        double sum = 0.0;
        int count = 0;

        for (String word : words) {
            if (sentiments.containsKey(word)) {
                sum += sentiments.get(word);
                count++;
            }
        }

        if (count == 0) {
            return null; // Если ни одно слово не найдено в sentiments
        }

        return sum / count; // Возвращаем среднее значение, даже если оно равно 0
    }
}