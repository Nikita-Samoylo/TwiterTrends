package PresentationLayer;

import DataLayer.SentimentLoader;
import DataLayer.TweetAnalyzer;
import DataLayer.TweetProcessor;

import java.util.Map;

public class TweetAnalysisService {

    private final SentimentLoader sentimentLoader;
    private final TweetAnalyzer tweetAnalyzer;

    public TweetAnalysisService() {
        this.sentimentLoader = new SentimentLoader();
        this.tweetAnalyzer = new TweetAnalyzer(new TweetProcessor());
    }

    public Map<String, Double> analyzeTweets(String filePath) throws Exception {
        // Загружаем данные и анализируем твиты
        Map<String, Double> sentiments = sentimentLoader.loadSentiments("sentiments.csv");
        return tweetAnalyzer.analyzeTweetsFromFile(filePath, sentiments);
    }
}