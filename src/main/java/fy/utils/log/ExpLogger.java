//package fy.utils.log;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//
//public class ExpLogger {
//
//    public static void log(Throwable e, String path) {
//        try {
//            String msg = e.getClass().toString() + System.lineSeparator();
//            Files.write(Paths.get(path), msg.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//    }
//
//    public static void log(Throwable e, String path, String note) {
//        try {
//            String msg = e.getClass().toString() + " | " + note + System.lineSeparator();
//            Files.write(Paths.get(path), msg.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//    }
//
//    public static void logAndPrint(Throwable e, String path) {
//        try {
//            String msg = e.getClass().toString() + System.lineSeparator();
//            Files.write(Paths.get(path), msg.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//            System.out.println("an error or exception happens: " + msg);
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//    }
//
//    public static void logAndPrint(Throwable e, String path, String note) {
//        try {
//            String msg = e.getClass().toString() + " | " + note + System.lineSeparator();
//            Files.write(Paths.get(path), msg.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//            System.out.println("an error or exception happens: " + msg);
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//    }
//
//    public static String getStartInfo(Throwable e) {
//        StackTraceElement[] elements = e.getStackTrace();
//        return "Class Name:"+elements[0].getClassName()+" Method Name:"+elements[0].getMethodName()+" Line Number:"+elements[0].getLineNumber();
//    }
//
//}
