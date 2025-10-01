# Wiktionary Importer

This is a standalone command-line utility for parsing a Wiktionary XML dump file and creating a local, queryable database using the JWKTL library.

## Purpose

The main `backend` application uses this generated database to validate AI-generated word translations. This tool is responsible for the heavy, one-time data processing task, keeping the main application lightweight.

## Setup

1.  **Download the Dump File:**
    *   Go to the Wikimedia Dumps site.
    *   Navigate to `enwiktionary` -> `(latest date)`.
    *   Download the file named `enwiktionary-latest-pages-articles.xml.bz2`.

2.  **Place the File:**
    *   Create a `wiktionary` directory at the root of the `LanguageLearning` project.
    *   Place the downloaded `.xml.bz2` file inside this `wiktionary` directory. The final path should be `LanguageLearning/wiktionary/enwiktionary-latest-pages-articles.xml.bz2`.

## Execution

To run the importer and generate the database, navigate to the root `LanguageLearning` directory in your terminal and execute the following command:

**On Windows (PowerShell):**
```sh
.\backend\gradlew :wiktionary-importer:run
```

**On macOS / Linux:**
```sh
./backend/gradlew :wiktionary-importer:run
```

**Note:** This is a very long-running and resource-intensive process. It may take several hours to complete. Once finished, a `db` directory will be created inside the `wiktionary` folder.