package PresentationLayer;

import DataLayer.SentimentLoader;
import DataLayer.StateMapper;
import DataLayer.TweetAnalyzer;
import DataLayer.TweetProcessor;

import java.io.IOException;
import java.util.Map;

public class TweetAnalysisService {
    private final SentimentLoader sentimentLoader;
    private final TweetAnalyzer tweetAnalyzer;
    private final StateMapper stateMapper;

    public TweetAnalysisService() throws IOException {
        this.sentimentLoader = new SentimentLoader();
        this.tweetAnalyzer = new TweetAnalyzer(new TweetProcessor());
        this.stateMapper = new StateMapper("states.json");
    }

    public Map<String, Double> analyzeTweetsByState(String filePath) throws Exception {
        // 1. Анализируем как раньше (получаем координаты + сентимент)
        Map<String, Double> coordinateSentiments = tweetAnalyzer.analyzeTweetsFromFile(
                filePath,
                sentimentLoader.loadSentiments("sentiments.csv")
        );

        // 2. Преобразуем координаты в названия штатов
        return stateMapper.mapToStates(coordinateSentiments);
    }
}
