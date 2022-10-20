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

    private int hamEmailCount;
    private int spamEmailCount;
    public final Map<String, WordStatistics> wordStatistics;

    public BayesSpamFilter() {
        hamEmailCount = 0;
        spamEmailCount = 0;
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
        hamEmailCount = emails.length;

        for (File email : emails) {
            Set<String> uniqueWords = FileUtil.getUniqueWords(email);
            for (String word : uniqueWords) {
                wordStatistics.compute(word, (k, v) -> {
                    if (v == null) {
                        return new WordStatistics(1, ALPHA);
                    }

                    v.incrementHamFrequency();
                    return v;
                });
            }
        }
    }

    private void trainSpam(File[] emails) {
        spamEmailCount = emails.length;

        for (File email : emails) {
            Set<String> uniqueWords = FileUtil.getUniqueWords(email);
            for (String word : uniqueWords) {
                wordStatistics.compute(word, (k, v) -> {
                    if (v == null) {
                        return new WordStatistics(ALPHA, 1);
                    }

                    v.incrementSpamFrequency();
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

        double pSpamGivenText = getProbabilityOfSpam() * Arrays.stream(knownWords)
                .mapToDouble(this::getProbabilityOfSpamGivenWord)
                .reduce(1, (a, b) -> a * b);
        double pHamGivenText = getProbabilityOfHam() * Arrays.stream(knownWords)
                .mapToDouble(this::getProbabilityOfHamGivenWord)
                .reduce(1, (a, b) -> a * b);

        return pSpamGivenText / (pSpamGivenText + pHamGivenText);
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


    /* *************************************************************************** */

    public String[] getKnownWords(String[] emailWords) {
        return Arrays.stream(emailWords)
                .filter(wordStatistics::containsKey)
                .toArray(String[]::new);
    }

    /* *************************************************************************** */


    private double getProbabilityOfWordGivenSpam(String word) {
        return wordStatistics.get(word).getSpamOccurrences() / spamEmailCount;
    }

    private double getProbabilityOfSpam() {
        return (double) spamEmailCount / (hamEmailCount + spamEmailCount);
    }

    private double getProbabilityOfWordGivenHam(String word) {
        return wordStatistics.get(word).getHamOccurrences() / hamEmailCount;
    }

    private double getProbabilityOfHam() {
        return (double) hamEmailCount / (hamEmailCount + spamEmailCount);
    }
}
