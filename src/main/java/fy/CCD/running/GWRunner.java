//package fy.CCD.running;
//
//import fy.CCD.GW.GitWalk;
//
//public class GWRunner implements Runnable{
//    Thread t;
//    String threadName;
//    String path;
//    String base;
//
//    public GWRunner(String threadName, String path, String base) {
//        this.threadName = threadName;
//        this.path = path;
//        this.base = base;
//    }
//
//    public void run () {
//        System.out.println("running");
//        GitWalk walker = new GitWalk(path);
//        walker.walk(0, 100);
//    }
//
//    public void start() {
//        System.out.println("start thread : " + threadName);
//        if (t == null) {
//            t = new Thread(this, threadName);
//            t.start();
//        }
//        System.out.println("end thread : " + threadName);
//    }
//}
