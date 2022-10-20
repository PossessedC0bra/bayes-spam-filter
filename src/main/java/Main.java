public class Main {
    public static void main(String[] args) {
        BayesSpamFilter bayesSpamFilter = new BayesSpamFilter();
        switch (args[0]) {
            case "--train":
                System.out.println("Training");

                bayesSpamFilter.train();
                break;
            case "--calibrate":
                System.out.println("Calibrating");
                break;
            case "--classify":
                System.out.println("Classifying");
                break;
            default:
                System.out.println("Unknown command");
        }
    }
}