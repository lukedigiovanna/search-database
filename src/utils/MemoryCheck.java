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
        System.out.println("\tFree memory: " + freeMemory / 1024 + " kB");
        System.out.println("\tAllocated memory: " + allocatedMemory / 1024 + " kB");
        System.out.println("\tMax memory: " + maxMemory / 1024 + " kB");
        System.out.println("\tTotal free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024 + " kB");
    
        System.out.println("\tUsing " + allocatedMemory / 1024.0 / 1024.0 / 1024.0 + " GB");
    }

}
