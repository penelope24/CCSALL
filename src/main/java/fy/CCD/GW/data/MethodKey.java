package fy.CCD.GW.data;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Objects;

public class MethodKey {

    MethodDeclaration n;
    String version;
    String file;
    int startLine;
    String name;
    int paramNums;

    public MethodKey(MethodDeclaration n, String version, String file) {
        this.n = n;
        this.version = version;
        this.file = file;
        startLine = n.getRange().isPresent() ?
                n.getRange().get().begin.line
                :
                -1;
        this.name = n.getNameAsString();
        this.paramNums = n.getParameters().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodKey methodKey = (MethodKey) o;
        return startLine == methodKey.startLine &&
                paramNums == methodKey.paramNums &&
                n.equals(methodKey.n) &&
                version.equals(methodKey.version) &&
                Objects.equals(file, methodKey.file) &&
                name.equals(methodKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, version, file, startLine, name, paramNums);
    }
}
