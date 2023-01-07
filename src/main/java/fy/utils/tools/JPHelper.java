package fy.utils.tools;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import fy.utils.log.Logger;

import java.io.File;


public class JPHelper {

//    public static CompilationUnit getCompilationUnit(InputStream in) {
//        try {
//            CompilationUnit cu;
//            try {
//                cu = StaticJavaParser.parse(in);
//                return cu;
//            }
//            finally {
//                in.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static CompilationUnit getCompilationUnit(String input) {
        try {
            CompilationUnit cu;
            cu = StaticJavaParser.parse(new File(input));
            return cu;
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CompilationUnit getCompilationUnitWithLog(String input, Logger logger) {
        try {
            CompilationUnit cu;
            cu = StaticJavaParser.parse(new File(input));
            return cu;
        } catch (Exception | Error e) {
            logger.log(e, "during java parser parse");
        }
        return null;
    }
}
