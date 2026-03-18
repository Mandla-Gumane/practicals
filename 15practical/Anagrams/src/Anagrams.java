// Anagrams.java
// CSC 211 Term 1 Practical 5 - Anagrams in Java
// Prompts used: Translated Python anagrams.py to Java, focusing on hashing for anagrams from ulysses.text
// Student: [Your Name Here] - Grandpa's custom version for submission
// Note: Assumes ulysses.text is in the same directory as this Java file.
// Compile and run: javac Anagrams.java && java Anagrams ulysses.text

import java.io.*;
import java.util.*;

public class Anagrams {

    // Helper method to generate the signature (sorted lowercase letters)
    private static String signature(String word) {
        char[] chars = word.toLowerCase().toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Anagrams <inputfile>");
            System.exit(1);
        }

        String inputFile = args[0];
        System.out.println("Data file: " + inputFile);

        // Part 1 & 2: Reading and cleaning up data, separating into words
        Map<String, Integer> wordCount = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String w : words) {
                    // Clean up: strip punctuation but leave apostrophes
                    String cleaned = w.replaceAll("[0123456789(,.;:_.!?\\-\\-\\-\\)]", "").toLowerCase();
                    if (!cleaned.isEmpty()) {
                        wordCount.put(cleaned, wordCount.getOrDefault(cleaned, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }

        // Part 3 & 4: Making signatures and collecting words with same signature
        Map<String, List<String>> anagrams = new HashMap<>();
        for (String w : wordCount.keySet()) {
            String sig = signature(w);
            anagrams.computeIfAbsent(sig, k -> new ArrayList<>()).add(w);
        }

        // Part 5: Creating permutations of lists and sorting (write to temp file and sort)
        try (PrintWriter pw = new PrintWriter(new File("anagrams"))) {
            for (List<String> list : anagrams.values()) {
                if (list.size() > 1) {
                    // Sort the list alphabetically for consistent output
                    Collections.sort(list);
                    String anagramList = String.join(" ", list);
                    pw.println(anagramList + "\\\\");
                    // Generate cyclic permutations
                    for (int i = 1; i < list.size(); i++) {
                        // Rotate: move first word to end
                        String first = anagramList.substring(0, anagramList.indexOf(' '));
                        anagramList = anagramList.substring(anagramList.indexOf(' ') + 1) + " " + first;
                        pw.println(anagramList + "\\\\");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error writing anagrams file: " + e.getMessage());
            System.exit(1);
        }

        // Sort the anagrams file
        try {
            // Read lines, sort, write to sorted file
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("anagrams.tex"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            }
            Collections.sort(lines);
            try (PrintWriter pw = new PrintWriter(new File("anagrams.sorted"))) {
                for (String line : lines) {
                    pw.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error sorting anagrams: " + e.getMessage());
            System.exit(1);
        }

        // Part 6: Producing the LaTeX document content in theAnagrams.tex
        File latexDir = new File("latex");
        if (!latexDir.exists()) {
            latexDir.mkdirs();
        }
        try (BufferedReader br = new BufferedReader(new FileReader("anagrams.sorted"));
             PrintWriter pw = new PrintWriter(new File("latex/Anagrams.tex"))) {
            String line;
            char letter = 'X';
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                char initial = line.charAt(0);
                if (Character.toLowerCase(initial) != Character.toLowerCase(letter)) {
                    letter = initial;
                    pw.printf("\n\\vspace{14pt}\n\\noindent\\textbf{\\Large %s}\\\\*[+12pt]\n", Character.toUpperCase(initial));
                }
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error generating Anagrams.tex: " + e.getMessage());
            System.exit(1);
        }

        // Clean up temp files (optional, but as in Python)
        new File("anagrams").delete();
        new File("anagrams.sorted").delete();

        System.out.println("Anagram processing complete. Check latex/theAnagrams.tex");
    }
}
