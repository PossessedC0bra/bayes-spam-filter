package ch.fhnw.dist.bayesspamfilter.dataset;

import ch.fhnw.dist.bayesspamfilter.util.FileUtil;

import java.io.File;

public class EmailDataset {

    private final String hamFolder;
    private final String spamFolder;

    public EmailDataset(String hamFolder, String spamFolder) {
        this.hamFolder = hamFolder;
        this.spamFolder = spamFolder;
    }

    public File[] getHamEmails() {
        File folder = FileUtil.getResourceFile(hamFolder);
        if (folder == null) {
            return null;
        }
        return folder.listFiles();
    }

    public File[] getSpamEmails() {
        File folder = FileUtil.getResourceFile(spamFolder);
        if (folder == null) {
            return null;
        }
        return folder.listFiles();
    }
}
