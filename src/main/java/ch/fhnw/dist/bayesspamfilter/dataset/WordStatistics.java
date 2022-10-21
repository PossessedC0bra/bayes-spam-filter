package ch.fhnw.dist.bayesspamfilter.dataset;

public class WordStatistics {

    private double hamOccurrences;
    private double spamOccurrences;

    public WordStatistics(double hamOccurrences, double spamOccurrences) {
        this.hamOccurrences = hamOccurrences;
        this.spamOccurrences = spamOccurrences;
    }

    public double getHamOccurrences() {
        return hamOccurrences;
    }

    public void addHamOccurrence() {
        ++hamOccurrences;
    }

    public double getSpamOccurrences() {
        return spamOccurrences;
    }

    public void addSpamOccurrence() {
        ++spamOccurrences;
    }
}
