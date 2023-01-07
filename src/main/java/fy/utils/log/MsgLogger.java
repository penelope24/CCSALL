//package fy.utils.log;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//
//public class MsgLogger {
//
//    public static void logAndPrint (String msg, String output) {
//        msg += System.lineSeparator();
//        try {
//            Files.write(Paths.get(output), msg.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
//            System.out.println(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
