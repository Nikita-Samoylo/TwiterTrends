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

    public Double analyzeTweetSentiment(String tweet, Map<String, Double> sentiments) {
        List<String> words = extractWords(tweet); // Извлекаем слова из твита
        double sum = 0.0;
        int count = 0;

        // Проверяем фразы разной длины (от 1 до 5 слов)
        for (int phraseLength = 5; phraseLength >= 1; phraseLength--) {
            for (int i = 0; i <= words.size() - phraseLength; i++) {
                // Собираем фразу из phraseLength слов
                StringBuilder phraseBuilder = new StringBuilder();
                for (int j = 0; j < phraseLength; j++) {
                    if (j > 0) {
                        phraseBuilder.append(" ");
                    }
                    phraseBuilder.append(words.get(i + j));
                }
                String phrase = phraseBuilder.toString();

                // Проверяем, есть ли фраза в словаре sentiments
                if (sentiments.containsKey(phrase)) {
                    sum += sentiments.get(phrase);
                    count++;
                    i += phraseLength - 1; // Пропускаем слова, которые уже вошли в фразу
                }
            }
        }

        if (count == 0) {
            return null; // Если ни одна фраза или слово не найдены в sentiments
        }

        return sum / count; // Возвращаем среднее значение sentiment
    }
}