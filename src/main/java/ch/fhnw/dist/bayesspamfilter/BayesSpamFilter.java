package ch.fhnw.dist.bayesspamfilter;

import ch.fhnw.dist.bayesspamfilter.dataset.EmailDataset;
import ch.fhnw.dist.bayesspamfilter.dataset.WordStatistics;
import ch.fhnw.dist.bayesspamfilter.util.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BayesSpamFilter {

    private static final double ALPHA = 1E-16;
    private static final double SPAM_THRESHOLD = 0.5;

    private int numberOfHamEmails;
    private int numberOfSpamEmails;
    public final Map<String, WordStatistics> wordStatistics;

    public BayesSpamFilter() {
        numberOfHamEmails = 0;
        numberOfSpamEmails = 0;
        wordStatistics = new HashMap<>();
    }

    /* *************************************************************************** */
    // TRAIN
    /* *************************************************************************** */

    public void train(EmailDataset trainDataset) {
        trainHam(trainDataset.getHamEmails());
        trainSpam(trainDataset.getSpamEmails());
    }

    private void trainHam(File[] emails) {
        numberOfHamEmails = emails.length;

        for (File email : emails) {
            Set<String> uniqueWords = FileUtil.getUniqueWords(email);
            for (String word : uniqueWords) {
                wordStatistics.compute(word, (k, v) -> {
                    if (v == null) {
                        return new WordStatistics(1, ALPHA);
                    }

                    v.addHamOccurrence();
                    return v;
                });
            }
        }
    }

    private void trainSpam(File[] emails) {
        numberOfSpamEmails = emails.length;

        for (File email : emails) {
            Set<String> uniqueWords = FileUtil.getUniqueWords(email);
            for (String word : uniqueWords) {
                wordStatistics.compute(word, (k, v) -> {
                    if (v == null) {
                        return new WordStatistics(ALPHA, 1);
                    }

                    v.addSpamOccurrence();
                    return v;
                });
            }
        }
    }

    /* *************************************************************************** */
    // PREDICT
    /* *************************************************************************** */

    public int predict(String filePath) {
        String[] words = FileUtil.getUniqueWords(new File(filePath)).toArray(String[]::new);
        return getProbabilityOfSpamGivenWords(words) > SPAM_THRESHOLD ? 1 : 0;
    }

    private double getProbabilityOfSpamGivenWords(String[] words) {
        String[] knownWords = getKnownWords(words);

        double pSpamGivenText = Arrays.stream(knownWords)
                .mapToDouble(this::getProbabilityOfSpamGivenWord)
                .reduce(getProbabilityOfSpam(), (a, b) -> a * b);
                
        double pHamGivenText = Arrays.stream(knownWords)
                .mapToDouble(this::getProbabilityOfHamGivenWord)
                .reduce(getProbabilityOfHam(), (a, b) -> a * b);

        return pSpamGivenText / (pSpamGivenText + pHamGivenText);
    }

    public String[] getKnownWords(String[] emailWords) {
        return Arrays.stream(emailWords)
                .filter(wordStatistics::containsKey)
                .toArray(String[]::new);
    }

    private double getProbabilityOfSpamGivenWord(String word) {
        double wordSpamicity = getProbabilityOfWordGivenSpam(word);
        double wordHamicity = getProbabilityOfWordGivenHam(word);

        return wordSpamicity / (wordSpamicity + wordHamicity);
    }

    private double getProbabilityOfHamGivenWord(String word) {
        double wordHamicity = getProbabilityOfWordGivenHam(word);
        double wordSpamicity = getProbabilityOfWordGivenSpam(word);

        return wordHamicity / (wordSpamicity + wordHamicity);
    }

    private double getProbabilityOfWordGivenSpam(String word) {
        return wordStatistics.get(word).getSpamOccurrences() / numberOfSpamEmails;
    }

    private double getProbabilityOfSpam() {
        return (double) numberOfSpamEmails / (numberOfHamEmails + numberOfSpamEmails);
    }

    private double getProbabilityOfWordGivenHam(String word) {
        return wordStatistics.get(word).getHamOccurrences() / numberOfHamEmails;
    }

    private double getProbabilityOfHam() {
        return (double) numberOfHamEmails / (numberOfHamEmails + numberOfSpamEmails);
    }
}
