package fy.GB.frontend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 打印dot文件时候，可能源码中一些符号，导致构图失败
 * 比如: 字符串中的引号
 * 这个工具就是用来过滤这些噪音的
 */
public class DotStrFilter {
    private static final Pattern p;
    private static final Pattern dotP;
    private static final Pattern quotationP;

    static {
        p = Pattern.compile("\r|\n|\r\n");
        dotP = Pattern.compile(",");
        quotationP = Pattern.compile("\"");
    }

    public static String filterQuotation(String originalStr) {
        return originalStr.replaceAll("\"", "'");
    }

    public static String AstNodeFilter(String originalStr) {
        Matcher matcher = p.matcher(originalStr);
        Matcher matcher2 = dotP.matcher(matcher.replaceAll(""));
        Matcher matcher3 = quotationP.matcher(matcher2.replaceAll("."));
        return matcher3.replaceAll("'");
    }
}
