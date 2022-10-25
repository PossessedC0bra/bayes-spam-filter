package ch.fhnw.dist.bayesspamfilter;

import ch.fhnw.dist.bayesspamfilter.dataset.EmailDataset;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        BayesSpamFilter spamFilter = new BayesSpamFilter();

        EmailDataset trainSet = new EmailDataset("/ham-anlern", "/spam-anlern");
        spamFilter.train(trainSet);

        if ("--calibrate".equals(args[0])) {
            EmailDataset calibrationSet = new EmailDataset("/ham-kallibrierung", "/spam-kallibrierung");
            classify(spamFilter, calibrationSet);
        }

        if ("--classify".equals(args[0])) {
            EmailDataset testSet = new EmailDataset("/ham-test", "/spam-test");
            classify(spamFilter, testSet);
        }
    }

    /* *************************************************************************** */

    private static void classify(BayesSpamFilter spamFilter, EmailDataset dataset) {
        File[] spamEmails = dataset.getSpamEmails();
        int actualSpam = spamEmails.length;
        int classifiedSpam = 0;
        for (File email : spamEmails) {
            if (spamFilter.predict(email.getPath())) {
                classifiedSpam++;
            }
        }

        File[] hamEmails = dataset.getHamEmails();
        int actualHam = hamEmails.length;
        int classifiedHam = 0;
        for (File email : hamEmails) {
            if (!spamFilter.predict(email.getPath())) {
                classifiedHam++;
            }
        }

        printConfusionMatrixAbsolute(actualSpam, classifiedSpam, actualHam, classifiedHam);
        printConfusionMatrixPercent(actualSpam, classifiedSpam, actualHam, classifiedHam);
    }

    /* *************************************************************************** */

    private static void printConfusionMatrixAbsolute(int actualSpam, int classifiedSpam, int actualHam, int classifiedHam) {
        printConfusionMatrix(classifiedSpam, actualSpam - classifiedSpam, actualHam - classifiedHam, classifiedHam);
    }

    private static void printConfusionMatrixPercent(int actualSpam, int classifiedSpam, int actualHam, int classifiedHam) {
        double tp = (double) classifiedSpam / actualSpam;
        double fp = (double) (actualSpam - classifiedSpam) / actualSpam;
        double fn = (double) (actualHam - classifiedHam) / actualHam;
        double tn = (double) classifiedHam / actualHam;

        printConfusionMatrix(tp, fp, fn, tn);
    }

    private static void printConfusionMatrix(double tp, double fp, double fn, double tn) {
        System.out.println();
        System.out.println("                   Predicted   ");
        System.out.println();
        System.out.println("                  Spam    Ham  ");
        System.out.println("                ---------------");
        System.out.printf("          Spam  | %.2f | %.2f |\n", tp, fp);
        System.out.println("Actual          ---------------");
        System.out.printf("          Ham   | %.2f | %.2f |\n", fn, tn);
        System.out.println("                ---------------");
    }
}