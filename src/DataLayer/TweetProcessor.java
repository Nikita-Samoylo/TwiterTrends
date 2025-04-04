package DataLayer;

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

    // анализ сентимента твита
    public Double analyzeTweetSentiment(String tweet, Map<String, Double> sentiments) {
        List<String> words = extractWords(tweet);
        double sum = 0.0;
        int count = 0;

        for (int phraseLength = 5; phraseLength >= 1; phraseLength--) {
            for (int i = 0; i <= words.size() - phraseLength; i++) {
                // сборка фразы из слов
                StringBuilder phraseBuilder = new StringBuilder();
                for (int j = 0; j < phraseLength; j++) {
                    if (j > 0) {
                        phraseBuilder.append(" ");
                    }
                    phraseBuilder.append(words.get(i + j));
                }
                String phrase = phraseBuilder.toString();
                if (sentiments.containsKey(phrase)) {
                    sum += sentiments.get(phrase);
                    count++;
                    i += phraseLength - 1;
                }
            }
        }
        if (count == 0) {
            return null;
        }
        return sum / count;
    }
}