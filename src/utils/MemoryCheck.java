package utils;

public class MemoryCheck {
    
    /**
     * Prints a report of the current state of memory usage in this application.
     */
    public static void print() {
        // Analyze memory usage
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        System.out.println("[-] Memory report:");
        System.out.println("Free memory: " + freeMemory / 1024 + " kB");
        System.out.println("Allocated memory: " + allocatedMemory / 1024 + " kB");
        System.out.println("Max memory: " + maxMemory / 1024 + " kB");
        System.out.println("Total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024 + " kB");
    
        System.out.println("Using " + freeMemory / 1000000000.0 + " GB");
    }

}
