import java.io.*;
import java.text.*;
import java.util.*;

public class Practical3 {  // Renamed for clarity; was timeMethods

    static class Node {  // Simple class like a Python dict or tuple
        int key;
        String data;
        Node(int k, String d) {
            key = k;
            data = d;
        }
    }

    static final int SIZE = 32654;  // From instructions (keys 1-32654)
    static int N = 1000000;  // Searches per repetition; adjust if too slow (try 100000 first)

    // Linear Search: O(n) time - check each item like a for-loop in Python
    static String linearSearch(Node[] arr, int key) {
        for (int i = 1; i <= SIZE; i++) {  // Start at 1 (keys 1-based)
            if (arr[i].key == key) {
                return arr[i].data;
            }
        }
        return null;  // Not found (but all keys exist, so ok)
    }

    // Binary Search: O(log n) - halve each time, like binary in math
    static String binarySearch(Node[] arr, int key) {
        int low = 1;
        int high = SIZE;
        while (low <= high) {
            int mid = (low + high) / 2;  // Integer division (like // in Python)
            if (arr[mid].key == key) {
                return arr[mid].data;
            } else if (arr[mid].key < key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public static void main(String args[]) throws IOException {  // Handle file errors

        // Formats for pretty output (like f-strings in Python)
        DecimalFormat twoD = new DecimalFormat("0.00");
        DecimalFormat fourD = new DecimalFormat("0.0000");
        DecimalFormat fiveD = new DecimalFormat("0.00000");

        // Load data - like reading a file in Python with open()
        Node[] array = new Node[SIZE + 1];
        Scanner in = new Scanner(new File("ulysses.numbered"));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.length() > 5) {  // Skip empty lines
                int key = Integer.parseInt(line.substring(0, 5));  // "00001" -> 1
                String data = line.substring(6);  // After space
                array[key] = new Node(key, data);
            }
        }
        in.close();

        Random rand = new Random();  // For random keys

        // Timing for Linear Search
        double runTimeLinear = 0, runTime2Linear = 0;
        for (int repetition = 0; repetition < 30; repetition++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < N; i++) {
                int key = rand.nextInt(SIZE) + 1;  // Random 1 to 32654
                linearSearch(array, key);  // Do the search
            }
            long finish = System.currentTimeMillis();
            double time = (double) (finish - start);
            runTimeLinear += time;
            runTime2Linear += (time * time);
        }
        double aveLinear = runTimeLinear / 30;
        double stdLinear = Math.sqrt(runTime2Linear - 30 * aveLinear * aveLinear) / 29;  // rep-1=29

        // Timing for Binary Search (same structure)
        double runTimeBinary = 0, runTime2Binary = 0;
        for (int repetition = 0; repetition < 30; repetition++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < N; i++) {
                int key = rand.nextInt(SIZE) + 1;
                binarySearch(array, key);
            }
            long finish = System.currentTimeMillis();
            double time = (double) (finish - start);
            runTimeBinary += time;
            runTime2Binary += (time * time);
        }
        double aveBinary = runTimeBinary / 30;
        double stdBinary = Math.sqrt(runTime2Binary - 30 * aveBinary * aveBinary) / 29;

        // Tabulate results (simple table for clarity)
        System.out.println("\nStatistics\n");
        System.out.println("_______________________________________________");
        System.out.println("Search Type\tAverage Time (s)\tStd Dev (ms)");
        System.out.println("_______________________________________________");
        System.out.println("Linear\t\t" + fiveD.format(aveLinear / 1000) + "\t\t" + fourD.format(stdLinear));
        System.out.println("Binary\t\t" + fiveD.format(aveBinary / 1000) + "\t\t" + fourD.format(stdBinary));
        System.out.println("_______________________________________________");
        System.out.println("n (searches per run) = " + N);
        System.out.println("Average time per search (linear): " + fiveD.format((aveLinear / N) * 1000) + " \u00B5s");
        System.out.println("Average time per search (binary): " + fiveD.format((aveBinary / N) * 1000) + " \u00B5s");
        System.out.println("Repetitions = 30");
    }
}