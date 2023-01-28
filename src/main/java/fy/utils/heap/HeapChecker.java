package fy.utils.heap;

public class HeapChecker {

    public static void print() {
        double total = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024);
        double max = (Runtime.getRuntime().maxMemory()) / (1024.0 * 1024);
        double free = (Runtime.getRuntime().freeMemory()) / (1024.0 * 1024);
        double available = (max - total + free);
        double used = (total - free);
        System.out.println("total usage : " + total);
        System.out.println("max heap size : " + max);
        System.out.println("free usage : " + free);
        System.out.println("available size : " + available);
        System.out.println("used size : " + used);
        System.out.println("########");
    }

    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
}
