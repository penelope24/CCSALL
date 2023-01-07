package fy.utils.file;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaFileUtils {
    /**
     * 该方法会计算空格所占的行数
     * intelliJ等IDE会要求在文件末尾新起一行，这个newline会在ide显示，但并不计入行数
     * 这个差异并不影响实际使用
     * */
    public static int countSourceLineNum(String filePath) {
        try {
            InputStream is = new FileInputStream(filePath);
            List<String> res = IOUtils.readLines(is, "UTF-8");
            return res.size();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 由于数组是从0计数，而实际行号是从1计数
     * 因此在查询时，将输入的行号减1以匹配其在数组中的真正位置
     * 对于空行的查询，返回结果是空字符串""，而不是换行符"\n"
     * */
    public static String getLineByLineNum(String filePath, int lineNum) throws IOException {
        InputStream is = new FileInputStream(filePath);
        List<String> res = IOUtils.readLines(is, "UTF-8");
        if (lineNum-1 < res.size()) {
            return res.get(lineNum - 1);
        }
        return null;
    }

    /**
     * 由于数组是从0计数，而实际行号是从1计数
     * 因此在查询时，将输入的行号减1以匹配其在数组中的真正位置
     * 对于空行的查询，返回结果是空字符串""，而不是换行符"\n"
     * */
    public static String getLineByLineNum(File filePath, int lineNum) throws IOException {
        InputStream is = new FileInputStream(filePath);
        List<String> res = IOUtils.readLines(is, "UTF-8");
        if (lineNum-1 < res.size()) {
            return res.get(lineNum - 1);
        }
        return null;
    }

    public static Map<String, Integer> getLineDict(String filePath) throws IOException {
        Map<String, Integer> dict = new HashMap<>();
        int totalLine = countSourceLineNum(filePath);
        for (int i=0; i<totalLine; i++) {
            dict.put(getLineByLineNum(filePath, i), i);
        }
        return dict;
    }

    public static Map<Integer, String> getReverseLineDict(String filePath) throws IOException {
        Map<Integer, String> dict = new HashMap<>();
        int totalLine = countSourceLineNum(filePath);
        for (int i=0; i<totalLine; i++) {
            dict.put(i, getLineByLineNum(filePath, i));
        }
        return dict;
    }
}
