package org.apache.beam;

import org.apache.beam.sdk.transforms.DoFn;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class CloneGitFn extends DoFn<Long, String> {
    private final String githubUrl;
    private final static Logger LOG = LoggerFactory.getLogger(CloneGitFn.class);
    public CloneGitFn(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    @ProcessElement
    public void process(@Element Long count, OutputReceiver<String> outputReceiver) throws IOException, GitAPIException {
        if(count != 1) return;
        try{
            File localDir = new File("./tmp");
            if(localDir.exists()){
                FileUtils.delete(localDir, FileUtils.RECURSIVE);
            }
            assert localDir.mkdir();
            final CloneCommand cloneCommand =
                    Git.cloneRepository().setURI(githubUrl).setDirectory(localDir);
            cloneCommand.call();
            outputReceiver.output(githubUrl);
            LOG.info("Success clone");
        }catch (Throwable t){
            LOG.error("Found error", t);
        }

    }
}
