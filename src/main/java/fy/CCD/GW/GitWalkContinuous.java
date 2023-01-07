package fy.CCD.GW;

import fy.CCD.GW.data.CommitLine;
import fy.Config;
import fy.utils.git.JGitUtils;
import fy.utils.log.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GitWalkContinuous {
    public Config config;
    public Repository repository;
    public JGitUtils jgit;
    public List<RevCommit> allCommits = new ArrayList<>();
    public Set<RevCommit> traversed = new HashSet<>();
    public Logger logger;

    public GitWalkContinuous(Config config) {
        this.config = config;
        this.repository = JGitUtils.buildJGitRepository(config.input);
        this.jgit = new JGitUtils(config.input);
        this.logger = new Logger(config);
    }

    /**
     * init git history
     * @throws IOException
     * @throws GitAPIException
     */
    public void preWalk() throws IOException, GitAPIException {
        jgit.delete_lock_file();
        jgit.reset();
        ObjectId master = JGitUtils.getMaster(repository);
        if (master == null) {
            throw new IllegalStateException("cannot find master head to start traverse");
        }
        RevCommit head = repository.parseCommit(master);
        RevWalk revWalk = new RevWalk(repository);
        revWalk.markStart(head);
        for (RevCommit commit : revWalk) {
            allCommits.add(commit);
        }
    }

    public List<CommitLine> getValidCommitLines() {
        // get commit lines
        List<CommitLine> commitLines = new ArrayList<>();
        allCommits.forEach(commit -> {
            if (!traversed.contains(commit)) {
                List<RevCommit> commits = explore(commit);
                CommitLine commitLine = new CommitLine(jgit, commits);
                commitLines.add(commitLine);
            }
        });
        // get important commit lines
        List<CommitLine> validCommitLines = commitLines.stream()
                .filter(CommitLine::isSignificant)
                .collect(Collectors.toList());
        logger.log("significant commit lines num : " + validCommitLines.size(), null);
        logger.log("output batch size : " + config.output_batch, null);
        return validCommitLines;
    }

    /**
     * walk continuously by each commitLine, and parse each commit.
     * @throws GitAPIException
     * @throws IOException
     */
    public void walk() throws GitAPIException, IOException {
        logger.log("analyzing project: " + config.repoName, null);
        logger.log("project output base path: " + config.log_project, null);
        List<CommitLine> validCommitLines = getValidCommitLines();
        validCommitLines.forEach(commitLine -> {
            LineSolver solver = new LineSolver(commitLine, this);
            try {
                int idx = validCommitLines.indexOf(commitLine);
                logger.log("analyzing line " + idx, null);
                solver.walk();
            } catch (GitAPIException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<RevCommit> explore(RevCommit head) {
        List<RevCommit> res = new ArrayList<>();
        Deque<RevCommit> worklist = new ArrayDeque<>();
        worklist.add(head);
        while (!worklist.isEmpty()) {
            RevCommit commit = worklist.pop();
            if (traversed.contains(commit)) {
                return res;
            }
            traversed.add(commit);
            res.add(commit);
            RevCommit par = JGitUtils.findFirstParent(repository, commit);
            if (par != null) {
                worklist.add(par);
            }
        }
        return res;
    }
}
