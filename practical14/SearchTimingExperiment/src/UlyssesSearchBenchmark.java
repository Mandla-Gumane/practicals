import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class UlyssesSearchBenchmark {

    // Simple record type: one key and its text
    private static class Entry {
        int id;
        String content;
        Entry(int id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    // Constants from the spec
    private static final int MAX_ID = 32654;
    private static final int SEARCHES_PER_RUN = 1_000_000;
    private static final int RUNS = 30;

    // Linear search: check each element until match
    private static String doLinearSearch(Entry[] data, int target) {
        for (int i = 1; i <= MAX_ID; i++) {
            if (data[i].id == target) {
                return data[i].content;
            }
        }
        return null;
    }

    // Binary search: halve the search space each step
    private static String doBinarySearch(Entry[] data, int target) {
        int low = 1, high = MAX_ID;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (data[mid].id == target) {
                return data[mid].content;
            } else if (data[mid].id < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    // Run one batch of searches using chosen method
    private static long runBatch(Entry[] dataset, boolean useBinary) {
        Random rng = new Random();
        long start = System.currentTimeMillis();
        for (int i = 0; i < SEARCHES_PER_RUN; i++) {
            int randomKey = rng.nextInt(MAX_ID) + 1;
            if (useBinary) {
                doBinarySearch(dataset, randomKey);
            } else {
                doLinearSearch(dataset, randomKey);
            }
        }
        return System.currentTimeMillis() - start;
    }

    // Compute average
    private static double mean(List<Long> values) {
        double sum = 0;
        for (long v : values) sum += v;
        return sum / values.size();
    }

    // Compute sample standard deviation
    private static double stdDev(List<Long> values, double mean) {
        double sumSq = 0;
        for (long v : values) {
            double diff = v - mean;
            sumSq += diff * diff;
        }
        return Math.sqrt(sumSq / (values.size() - 1));
    }

    // Load file into Entry array (1-based indexing)
    private static Entry[] loadFile(String filename) throws IOException {
        List<Entry> temp = new ArrayList<>();
        temp.add(null); // index 0 unused
        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.length() > 5) {
                int id = Integer.parseInt(line.substring(0, 5));
                String text = line.substring(6);
                temp.add(new Entry(id, text));
            }
        }
        sc.close();
        return temp.toArray(new Entry[0]);
    }

    public static void main(String[] args) {
        try {
            Entry[] dataset = loadFile("ulysses.numbered");

            List<Long> linearTimes = new ArrayList<>();
            List<Long> binaryTimes = new ArrayList<>();

            for (int r = 0; r < RUNS; r++) {
                linearTimes.add(runBatch(dataset, false));
                binaryTimes.add(runBatch(dataset, true));
            }

            double avgLinear = mean(linearTimes);
            double sdLinear = stdDev(linearTimes, avgLinear);

            double avgBinary = mean(binaryTimes);
            double sdBinary = stdDev(binaryTimes, avgBinary);

            System.out.println("=== Ulysses Search Benchmark ===");
            System.out.printf("Linear Search: avg %.5f s, std %.4f ms, %.5f µs/search%n",
                    avgLinear / 1000.0, sdLinear, (avgLinear / SEARCHES_PER_RUN) * 1000.0);
            System.out.printf("Binary Search: avg %.5f s, std %.4f ms, %.5f µs/search%n",
                    avgBinary / 1000.0, sdBinary, (avgBinary / SEARCHES_PER_RUN) * 1000.0);
            System.out.println("Runs = " + RUNS + ", Searches per run = " + SEARCHES_PER_RUN);

        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }
}
