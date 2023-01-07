package fy.CCD.GW.data;

import org.eclipse.jgit.revwalk.RevCommit;

public class Delta {

    RevCommit curr;
    RevCommit par;
    TypeStatus currStatus;
    TypeStatus parStatus;

    public Delta() {
    }

    public Delta(RevCommit curr, RevCommit par) {
        this.curr = curr;
        this.par = par;
    }

    public RevCommit getCurr() {
        return curr;
    }

    public Delta setCurr(RevCommit curr) {
        this.curr = curr;
        return this;
    }

    public RevCommit getPar() {
        return par;
    }

    public Delta setPar(RevCommit par) {
        this.par = par;
        return this;
    }

    public TypeStatus getCurrStatus() {
        return currStatus;
    }

    public Delta setCurrStatus(TypeStatus currStatus) {
        this.currStatus = currStatus;
        return this;
    }

    public TypeStatus getParStatus() {
        return parStatus;
    }

    public Delta setParStatus(TypeStatus parStatus) {
        this.parStatus = parStatus;
        return this;
    }

    public void clearStatus() {
        this.currStatus = null;
        this.parStatus = null;
        System.gc();
    }
}
