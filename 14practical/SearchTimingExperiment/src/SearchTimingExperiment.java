import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SearchTimingExperiment {

    // Each record has a numeric key and some text
    private static class Record {
        int key;
        String text;
        Record(int k, String t) {
            key = k;
            text = t;
        }
    }

    // Constants from the spec
    private static final int MAX_KEY = 32654;
    private static final int SEARCHES_PER_RUN = 1_000_000;
    private static final int NUM_RUNS = 30;

    // Linear search: scan through the array until key is found
    private static String linearFind(Record[] data, int target) {
        for (int i = 1; i <= MAX_KEY; i++) {
            if (data[i].key == target) {
                return data[i].text;
            }
        }
        return null;
    }

    // Binary search: halve the search space each step
    private static String binaryFind(Record[] data, int target) {
        int low = 1, high = MAX_KEY;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (data[mid].key == target) {
                return data[mid].text;
            } else if (data[mid].key < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    // Run one batch of searches using either linear or binary
    private static long runOneBatch(Record[] data, boolean useBinary) {
        Random rng = new Random();
        long start = System.currentTimeMillis();
        for (int i = 0; i < SEARCHES_PER_RUN; i++) {
            int randomKey = rng.nextInt(MAX_KEY) + 1;
            if (useBinary) {
                binaryFind(data, randomKey);
            } else {
                linearFind(data, randomKey);
            }
        }
        return System.currentTimeMillis() - start;
    }

    // Compute average from a list of run times
    private static double average(List<Long> times) {
        double sum = 0;
        for (long t : times) sum += t;
        return sum / times.size();
    }

    // Compute sample standard deviation
    private static double sampleStdDev(List<Long> times, double mean) {
        double sumSq = 0;
        for (long t : times) {
            double diff = t - mean;
            sumSq += diff * diff;
        }
        return Math.sqrt(sumSq / (times.size() - 1));
    }

    // Load the file into a Record array (1-based indexing)
    private static Record[] loadData(String filename) throws IOException {
        List<Record> temp = new ArrayList<>();
        temp.add(null); // placeholder for index 0
        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.length() > 5) {
                int key = Integer.parseInt(line.substring(0, 5));
                String text = line.substring(6);
                temp.add(new Record(key, text));
            }
        }
        sc.close();
        return temp.toArray(new Record[0]);
    }

    public static void main(String[] args) {
        try {
            Record[] dataset = loadData("ulysses.numbered");

            // Collect timings
            List<Long> linearRuns = new ArrayList<>();
            List<Long> binaryRuns = new ArrayList<>();

            for (int r = 0; r < NUM_RUNS; r++) {
                linearRuns.add(runOneBatch(dataset, false));
                binaryRuns.add(runOneBatch(dataset, true));
            }

            // Compute statistics
            double meanLinear = average(linearRuns);
            double stdLinear = sampleStdDev(linearRuns, meanLinear);

            double meanBinary = average(binaryRuns);
            double stdBinary = sampleStdDev(binaryRuns, meanBinary);

            // Report results
            System.out.println("Search Timing Results");
            System.out.println("==============================");
            System.out.printf("Linear Search: avg %.5f s, std %.4f ms, %.5f µs/search%n",
                    meanLinear / 1000.0, stdLinear, (meanLinear / SEARCHES_PER_RUN) * 1000.0);
            System.out.printf("Binary Search: avg %.5f s, std %.4f ms, %.5f µs/search%n",
                    meanBinary / 1000.0, stdBinary, (meanBinary / SEARCHES_PER_RUN) * 1000.0);
            System.out.println("==============================");
            System.out.println("Runs = " + NUM_RUNS + ", Searches per run = " + SEARCHES_PER_RUN);

        } catch (IOException e) {
            System.err.println("Problem reading file: " + e.getMessage());
        }
    }
}
