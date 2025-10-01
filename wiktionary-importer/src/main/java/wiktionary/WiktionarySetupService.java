package wiktionary;

import de.tudarmstadt.ukp.jwktl.JWKTL;
import java.io.File;

/**
 * A service class dedicated to handling the parsing of a Wiktionary dump file.
 * This is designed to be used by a standalone tool, not as a Spring-managed bean.
 */
public class WiktionarySetupService {


    /**
     * Parses a Wiktionary dump file and creates a local database.
     * This is a long-running, one-time setup process.
     *
     * @param dumpFilePath The path to the downloaded .xml.bz2 dump file.
     * @param outputDbPath The path to the directory where the database should be created.
     */
     public void parseDump(String dumpFilePath, String outputDbPath) {
        File dumpFile = new File(dumpFilePath);
        File outputDir = new File(outputDbPath);

        if (!dumpFile.exists()) {
            System.err.println("Wiktionary dump file not found at: " + dumpFilePath);
            return;
        }

        System.out.println("Starting Wiktionary dump parsing. This will take a long time...");
        JWKTL.parseWiktionaryDump(dumpFile, outputDir, true);
        System.out.println("Wiktionary parsing complete. Database created at: " + outputDbPath);
     }
}
