package fy.CCD.GW.multi_thread;


import fy.CCD.GW.GitWalk;

public class GWThread extends Thread{
    int nThread;
    GitWalk gitWalk;
    int start;
    int end;

    public GWThread(int nThread, GitWalk gitWalk, int start, int end) {
        this.nThread = nThread;
        this.gitWalk = gitWalk;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        this.gitWalk.walk(start, end);
    }
}
