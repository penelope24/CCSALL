package fy.CCS;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCD.GW.data.Hunk;
import fy.CCS.slicing.PDGBuilder;
import fy.CCS.slicing.SrcCodeTransformer;
import fy.CCS.track.DTEntry;
import fy.GD.mgraph.MethodPDG;
import fy.utils.git.JGitUtils;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class CCSVersionedTest {
    final String project = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/gerrit";
    Repository repository = JGitUtils.buildJGitRepository(project);
    final int MAX_DATA_DEPTH = 3;
    final int MAX_CTRL_DEPTH = 2;

    @Test
    void test() throws IOException {

    }

    public void slice1(Hunk hunk) {
        MethodDeclaration n = hunk.n1;
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, hunk.fileDiff.path1, n);
        List<Integer> chLines = hunk.getRemLines();
        Set<Integer> reserved = DTEntry.dependencyTrack(graph, chLines, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration n2 = SrcCodeTransformer.slice(nClone, reserved);
        System.out.println(n2);
    }
}
