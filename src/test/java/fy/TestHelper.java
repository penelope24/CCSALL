package fy;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.entry.GBEntry;
import fy.GD.mgraph.MethodPDG;

public class TestHelper {

    public static MethodPDG init_a_graph(String project, String file, MethodDeclaration n) {
        return GBEntry.one_pass_parse(project, file, n);
    }
}
