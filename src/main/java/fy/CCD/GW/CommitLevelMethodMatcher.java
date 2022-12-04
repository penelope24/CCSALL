package fy.CCD.GW;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.common.collect.Multimap;
import fy.CCD.GW.data.CommitDiff;
import fy.CCD.GW.data.Hunk;
import fy.CCD.GW.data.MethodKey;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommitLevelMethodMatcher {
    // input
    CommitDiff commitDiff;
    // evidence
    Map<MethodDeclaration, MethodKey> evidence1 = new HashMap<>();
    Map<MethodDeclaration, MethodKey> evidence2 = new HashMap<>();
    // conclusion
    Map<Integer, Pair<MethodDeclaration, MethodDeclaration>> res = new HashMap<>();

    public CommitLevelMethodMatcher(CommitDiff commitDiff) {
        this.commitDiff = commitDiff;
    }

    public void parse() {
        commitDiff.fileDiffs.forEach(fileDiff -> {
            // v1
            {
                Multimap<MethodDeclaration, Hunk> map = fileDiff.ccMap1;
                Set<MethodDeclaration> methods = map.keySet();
                methods.forEach(m -> evidence1.put(m, new MethodKey(m, commitDiff.v1, fileDiff.path1)));
            }
            // v2
            {
                Multimap<MethodDeclaration, Hunk> map = fileDiff.ccMap2;
                Set<MethodDeclaration> methods = map.keySet();
                methods.forEach(m -> evidence2.put(m, new MethodKey(m, commitDiff.v2, fileDiff.path2)));
            }
            // analyse

        });
    }


}
