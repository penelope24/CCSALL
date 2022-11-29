package fy.CCD.GW.utils;

import fy.CCD.GW.data.Hunk;
import fy.GD.export.ExpEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HKHelper {


    public static void log(Hunk hunk, BufferedWriter writer) {
        try {
            writer.write("commit msg: " + hunk.commitDiff.getCommitMessage());
            writer.newLine();
            writer.write("v1: " + hunk.commitDiff.v1 + "    " + "v2: " + hunk.commitDiff.v2);
            writer.newLine();
            writer.write("file name: " + hunk.fileDiff.getSimpleName());
            writer.newLine();
            writer.write(hunk.toString());
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void output1(String base, Hunk hunk, BufferedWriter logger) {
        String dirName = hunk.commitDiff.getCurrentVersion() + "_" + hunk.fileDiff.getSimpleName() + "_" + hunk.getEditStartLine();
        File dir = new File(base + "/" + dirName);
        if (!dir.exists()) {
            boolean f = dir.mkdir();
            assert f;
        }
        outputJsonGraphsToDir1(dir, hunk, logger);
    }

    public static void output2(String base, Hunk hunk, BufferedWriter logger) {
        String dirName = hunk.commitDiff.getCurrentVersion() + "_" + hunk.fileDiff.getSimpleName() + "_" + hunk.getEditStartLine();
        File dir = new File(base + "/" + dirName);
        if (!dir.exists()) {
            boolean f = dir.mkdir();
            assert f;
        }
        outputJsonGraphsToDir2(dir, hunk, logger);
    }

    public static void outputJsonGraphsToDir(File dir, Hunk hunk, BufferedWriter logger) {
        try {
            if (hunk.graph1 != null) {
                String path = dir.getAbsolutePath() + "/" + "g1" + ".json";
                ExpEntry.exportJSON(hunk.graph1, path, hunk);
                if (logger != null) {
                    logger.write("__g1__:" + path) ;
                }
            }
            if (hunk.graph2 != null) {
                String path = dir.getAbsolutePath() + "/" + "g2" + ".json";
                ExpEntry.exportJSON(hunk.graph2, path, hunk);
                if (logger != null) {
                    logger.write("__g2__:" + path) ;
                }
            }
            if (hunk.slice1 != null) {
                String path = dir.getAbsolutePath() + "/" + "slice1" + ".json";
                ExpEntry.exportJSON(hunk.slice1, path, hunk);
                if (logger != null) {
                    logger.write("__slice1__:" + path) ;
                }
            }
            if (hunk.slice2 != null) {
                String path = dir.getAbsolutePath() + "/" + "slice2" + ".json";
                ExpEntry.exportJSON(hunk.slice2, path, hunk);
                if (logger != null) {
                    logger.write("__slice2__:" + path) ;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void outputJsonGraphsToDir1(File dir, Hunk hunk, BufferedWriter logger) {
        try {
            if (hunk.graph1 != null) {
                String path = dir.getAbsolutePath() + "/" + "g1" + ".json";
                ExpEntry.exportJSON(hunk.graph1, path, hunk);
                if (logger != null) {
                    logger.write("__g1__:" + path) ;
                }
            }
            if (hunk.slice1 != null) {
                String path = dir.getAbsolutePath() + "/" + "slice1" + ".json";
                ExpEntry.exportJSON(hunk.slice1, path, hunk);
                if (logger != null) {
                    logger.write("__slice1__:" + path) ;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void outputJsonGraphsToDir2(File dir, Hunk hunk, BufferedWriter logger) {
        try {
            if (hunk.graph2 != null) {
                String path = dir.getAbsolutePath() + "/" + "g2" + ".json";
                ExpEntry.exportJSON(hunk.graph2, path, hunk);
                if (logger != null) {
                    logger.write("__g2__:" + path) ;
                }
            }
            if (hunk.slice2 != null) {
                String path = dir.getAbsolutePath() + "/" + "slice2" + ".json";
                ExpEntry.exportJSON(hunk.slice2, path, hunk);
                if (logger != null) {
                    logger.write("__slice2__:" + path) ;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
