package wiktionary;

/**
 * A standalone command-line tool to parse the Wiktionary dump file.
 * This is not part of the Spring Boot application and should be run manually
 * from the IDE when the dictionary database needs to be created or updated.
 */
public class WiktionaryImporter {

    public static void main(String[] args) {
        wiktionary.WiktionarySetupService setupService = new wiktionary.WiktionarySetupService();

        String dumpFilePath = "wiktionary/enwiktionary-latest-pages-articles.xml.bz2";
        String outputDbPath = "wiktionary/db";

        setupService.parseDump(dumpFilePath, outputDbPath);
    }
}
