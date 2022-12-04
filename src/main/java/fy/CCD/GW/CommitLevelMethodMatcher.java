package fy.CCD.GW;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCD.GW.data.CommitDiff;
import fy.CCD.GW.data.MethodKey;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

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
        commitDiff.fileDiffs.forEach(fileDiff -> {
            String file = fileDiff.path1;
        });
    }

    public void parse() {

    }


}
