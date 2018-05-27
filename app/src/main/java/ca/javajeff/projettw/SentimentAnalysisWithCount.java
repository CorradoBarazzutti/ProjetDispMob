package ca.javajeff.projettw;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.doccat.DoccatFactory;

/**
 * This class uses OPENNLP Maven dependencies for tweet sentiment analysis.
 * The implementation was done updating this:
 * file:///Users/conrad/Desktop/Twitter%20Sentiment%20Analysis%20using%20OpenNLP%20JAVA%20API.webarchive
 */
public class SentimentAnalysisWithCount {

    File training_file;

    DoccatModel model;
    static int positive = 0;
    static int negative = 0;

    public SentimentAnalysisWithCount(File training_file) {
        super();
        this.training_file = training_file;
        this.trainModel();
    }

    private void trainModel() {
        /**
         * Train the OpenNPL model with an input file
         * I got the input file from this link:
         * http://technobium.com/sentiment-analysis-using-opennlp-document-categorizer/
         */
        try {

            // prepare dataStream
            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(training_file);
            ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

            // Prepare parameters
            TrainingParameters trainingParameters = new TrainingParameters();
            trainingParameters.put("CUTOFF", "2");
            trainingParameters.put("TRAININGITERATIONS", "30");
            // TRAIN MODEL
            this.model = DocumentCategorizerME.train("en",
                    sampleStream,
                    TrainingParameters.defaultParams(),
                    new DoccatFactory());
        } catch (IOException e) {
            e.printStackTrace();
            Log.wtf("sentiment analysis", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("sentiment analysis", e.getMessage());
        }
    }


    public int classifyNewTweet(String[] tweet) throws IOException {
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(this.model);
        double[] outcomes = myCategorizer.categorize(tweet);
        String category = myCategorizer.getBestCategory(outcomes);

        if (category.equalsIgnoreCase("1")) {
            return 1;
        } else {
            return 0;
        }

    }
}

