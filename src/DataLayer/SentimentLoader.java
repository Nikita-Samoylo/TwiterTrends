package DataLayer;

import java.io.*;
import java.util.*;

public class SentimentLoader {
    public Map<String, Double> loadSentiments(String filePath) throws IOException {
        Map<String, Double> sentiments = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                sentiments.put(parts[0], Double.parseDouble(parts[1]));
            }
        }
        reader.close();
        return sentiments;
    }
}