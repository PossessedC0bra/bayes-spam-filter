package ch.fhnw.dist.bayesspamfilter;

import ch.fhnw.dist.bayesspamfilter.dataset.EmailDataset;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Training...");

        BayesSpamFilter spamFilter = new BayesSpamFilter();

        EmailDataset trainSet = new EmailDataset("/ham-anlern", "/spam-anlern");
        spamFilter.train(trainSet);

        if ("--calibrate".equals(args[0])) {
            System.out.println("=== CALIBRATE ===");

            EmailDataset calibrationSet = new EmailDataset("/ham-kallibrierung", "/spam-kallibrierung");
            classify(spamFilter, calibrationSet);
        }

        if ("--classify".equals(args[0])) {
            System.out.println("=== CLASSIFY ===");

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
            if (spamFilter.predict(email.getPath()) == 1) {
                classifiedSpam++;
            }
        }

        File[] hamEmails = dataset.getHamEmails();
        int actualHam = hamEmails.length;
        int classifiedHam = 0;
        for (File email : hamEmails) {
            if (spamFilter.predict(email.getPath()) == 0) {
                classifiedHam++;
            }
        }

        printConfusionMatrix(actualSpam, classifiedSpam, actualHam, classifiedHam);
    }

    private static void printConfusionMatrix(int actualSpam, int classifiedSpam, int actualHam, int classifiedHam) {
        double tp = (double) classifiedSpam / actualSpam;
        double fp = (double) (actualSpam - classifiedSpam) / actualSpam;
        double fn = (double) (actualHam - classifiedHam) / actualHam;
        double tn = (double) classifiedHam / actualHam;

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