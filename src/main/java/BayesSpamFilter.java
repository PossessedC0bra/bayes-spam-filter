import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BayesSpamFilter {

    private final int hamCount;
    public final Map<String, Integer> hamWords;

    private final int spamCount;
    private final Map<String, Integer> spamWords;

    public BayesSpamFilter() {
        hamCount = 0;
        hamWords = new HashMap<>();

        spamCount = 0;
        spamWords = new HashMap<>();
    }

    /* *************************************************************************** */

    public void train() {
        trainHam();
        trainSpam();
    }

    private void trainHam() {
        trainInternal("ham-anlern", hamCount, hamWords);
    }

    private void trainSpam() {
        trainInternal("spam-anlern", spamCount, spamWords);
    }

    private void trainInternal(String dataset, int emailCount, Map<String, Integer> wordFrequency) {
        URL hamTrainSetUrl = ResourceBase.class.getResource(dataset);
        URI hamTrainSetUri = null;
        try {
            hamTrainSetUri = hamTrainSetUrl.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        File hamTrainSetFile = new File(hamTrainSetUri);
        File[] emails = hamTrainSetFile.listFiles();
        for (File email : emails) {
            emailCount++;

            try (InputStream is = new FileInputStream(email)) {
                Scanner scanner = new Scanner(is);
                while (scanner.hasNext()) {
                    String word = scanner.next();
                    wordFrequency.compute(word, (k, v) -> v == null ? 1 : v + 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
