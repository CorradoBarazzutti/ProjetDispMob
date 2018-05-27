package ca.javajeff.projettw;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class performs sentiment analysis on a text using Stendford coreNPL library
 */
public class SentimentAnalyzer {

    StanfordCoreNLP pipeline;

    public SentimentAnalyzer() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

        this.pipeline = new StanfordCoreNLP(props);
    }

    /**
     * computes sentiment analysis on a tweet and returns the sentiment as an int form 0 (vary negative)
     * to 4 (very positive)
     */

    public double findSentiment(String line) {

        List<Double> sentiments = new ArrayList<>();
        List<Integer> sizes = new ArrayList<>();
        int mainSentiment = 0;

        if (line != null && line.length() > 0) {
            
            int longest = 0;
            Annotation annotation = this.pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();

                sentiments.add((double) sentiment);
                sizes.add(partText.length());

                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }

        /**
         * compute average: weighted on sentence lenght in the assumption that longest santence
         * are more important */

        Double weightedSentiment = 0.;
        int size = 0;
        for (int i = 0; i < sentiments.size(); i++) {
            weightedSentiment += sentiments.get(i) * (double) sizes.get(i);
            size += sizes.get(i);
        }
        weightedSentiment /= (double) size;
        return weightedSentiment;
    }

}
