package Data;

import Map.StatesParser;
import Map.State;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TweetAnalysisService {
    private final SentimentLoader sentimentLoader;
    private final TweetAnalyzer tweetAnalyzer;
    private final StateMapper stateMapper;

    public TweetAnalysisService() throws IOException {
        this.sentimentLoader = new SentimentLoader();
        this.tweetAnalyzer = new TweetAnalyzer(new TweetProcessor());
        this.stateMapper = new StateMapper(loadStates());
    }
    // парсинг штатов
    public List<State> loadStates() throws IOException {
        List<State> states = StatesParser.parse("states.json");
        if (states == null || states.isEmpty()) {
            throw new IllegalStateException("Не удалось загрузить или распарсить данные о штатах");
        }
        return states;
    }

    public Map<String, Double> analyzeTweetsByState(String filePath) throws Exception {
        // первый анализ (получаем словарь координаты + сентимент)
        Map<String, Double> coordinateSentiments = tweetAnalyzer.analyzeTweetsFromFile(
                filePath,
                sentimentLoader.loadSentiments("sentiments.csv")
        );
        // второй анализ (получаем словарь штаты + сентимент)
        return stateMapper.mapToStates(coordinateSentiments);
    }
}