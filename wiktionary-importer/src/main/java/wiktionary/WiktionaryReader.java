package wiktionary;

import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IPronunciation;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryTranslation;
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage;
import de.tudarmstadt.ukp.jwktl.api.util.Language;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A standalone tool to read the parsed Wiktionary database,
 * extract translations for target languages, and test the data structure.
 */
public class WiktionaryReader {

    // Define the languages you are interested in, matching your WordDetails.java
    private static final Set<ILanguage> TARGET_LANGUAGES = Stream.of(
            // Use 3-letter ISO 639-2/T codes
            "kor", "jpn", "zho", "tha", // Korean, Japanese, Chinese, Thai
            "ita", "fra", "spa", "deu"  // Italian, French, Spanish, German
    ).map(Language::get).collect(Collectors.toSet());

    /**
     * A simple inner class to hold the extracted data for a single word,
     * including its alternative readings (e.g., hiragana, katakana).
     */
    public static class WordData {
        public Set<String> readings = new HashSet<>();
        public Set<String> translations = new HashSet<>();

        @Override
        public String toString() {
            return "Readings: " + readings + ", Translations: " + translations;
        }
    }

    public static void main(String[] args ) {
        String dbPath = "wiktionary/db";
        File dbDir = new File(dbPath);

        if (!dbDir.exists() || !dbDir.isDirectory()) {
            System.err.println("Wiktionary DB directory not found at: " + dbPath);
            System.err.println("Please run the WiktionaryImporter first.");
            return;
        }

        System.out.println("Opening Wiktionary database from: " + dbPath);
        IWiktionaryEdition wkt = JWKTL.openEdition(dbDir);

        // Language -> { Word -> WordData (translations & readings) }
        Map<String, Map<String, WordData>> dictionary = new HashMap<>();
        Set<String> seenLanguages = new HashSet<>(); // To log each language only once

        System.out.println("Processing entries for target languages...");
        for (IWiktionaryEntry entry : wkt.getAllEntries()) {
            ILanguage entryLanguage = entry.getWordLanguage();

            if (entryLanguage == null || !TARGET_LANGUAGES.contains(entryLanguage)) {
                continue;
            }

            String langKey = entryLanguage.getName().toLowerCase();
            // Log the language key the first time we see it to confirm it's being processed.
            if (seenLanguages.add(langKey)) {
                System.out.println("-> Found entries for language: " + langKey);
            }

            String word = entry.getWord();

            // Filter out entries that are not real words (e.g., punctuation, symbols)
            // by ensuring they contain at least one Unicode letter character.
            if (!word.matches(".*\\p{L}.*")) {
                continue;
            }

            WordData wordData = dictionary.computeIfAbsent(langKey, k -> new HashMap<>())
                                          .computeIfAbsent(word, k -> new WordData());

            // For each "sense" (meaning) of the word, add its English translations and definition
            for (IWiktionarySense sense : entry.getSenses()) {
                // Extract the definition (gloss)
                // For non-English words, the definition (gloss) is often the best source for the English translation.
                String gloss = sense.getGloss() != null ? sense.getGloss().getPlainText() : null;
                if (gloss != null && !gloss.isBlank()) {
                    wordData.translations.add(gloss);
                }

                // Extract English translations from the dedicated "Translations" section, if it exists.
                for (IWiktionaryTranslation translation : sense.getTranslations(Language.ENGLISH)) {
                    wordData.translations.add(translation.getTranslation());
                }
            }

            // Extract alternative readings (e.g., Hiragana, Katakana, Romaji)
            if (entry.getPronunciations() != null) {
                for (IPronunciation pronunciation : entry.getPronunciations()) {
                    if (pronunciation.getText() != null) {
                        wordData.readings.add(pronunciation.getText());
                    }
                }
            }

            // If we still have no translations, try to follow the entry link (for inflections).
            if (wordData.translations.isEmpty() && entry.getEntryLink() != null) {
                for (IWiktionaryEntry linkedEntry : wkt.getEntriesForWord(entry.getEntryLink())) {
                    // Ensure we are looking at the same language (e.g., French "mesurer" not English "mesurer")
                    if (entryLanguage.equals(linkedEntry.getWordLanguage())) {
                        for (IWiktionarySense linkedSense : linkedEntry.getSenses()) {
                            String linkedGloss = linkedSense.getGloss() != null ? linkedSense.getGloss().getPlainText() : null;
                            if (linkedGloss != null && !linkedGloss.isBlank()) {
                                wordData.translations.add(linkedGloss);
                            }
                        }
                    }
                }
            }

            // Final cleanup on all collected data.
            wordData.translations = cleanSet(wordData.translations);
            wordData.readings = cleanSet(wordData.readings);
        }

        System.out.println("Processing complete.");
        System.out.println("--- Dictionary Entry Counts ---");
        dictionary.forEach((langKey, wordMap) -> {
            System.out.println("Language: " + langKey + " -> " + wordMap.size() + " words found.");
        });

        System.out.println("Writing sample results to results.txt to verify character encoding...");

        // Write output to a file with UTF-8 encoding to bypass console limitations
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("results.txt"), "UTF-8"))) {
            writer.println("--- Sample Extracted Data ---");
            dictionary.forEach((langKey, wordMap) -> {
                writer.println("\n--- Language: " + langKey + " ---");
                wordMap.entrySet().stream()
                       .limit(25) // Print the first 25 words found for this language
                       .forEach(mapEntry -> writer.println("Word: '" + mapEntry.getKey() + "' -> " + mapEntry.getValue()));
            });
        } catch (Exception e) {
            System.err.println("Failed to write results to file: " + e.getMessage());
        }

        System.out.println("Sample results written to results.txt in the project root directory.");

        wkt.close();
    }

    private static Set<String> cleanSet(Set<String> input) {
        if (input == null || input.isEmpty()) {
            return new HashSet<>();
        }
        return input.stream()
                .map(t -> t.replaceAll("\\{\\{.*?}}", "").replaceAll("\\[\\[(?:[^|\\]]+\\|)?([^\\]]+)]]", "$1").replaceAll("[\\[\\]{}'<>|]", "").trim())
                // Add a final filter to remove strings with template remnants or junk characters.
                .filter(t -> !t.isEmpty() && t.length() > 1 && !t.matches(".*[>|].*"))
                .collect(Collectors.toSet());
    }
}
