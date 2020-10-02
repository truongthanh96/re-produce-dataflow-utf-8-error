package org.apache.beam;

import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.Duration;

public class ReproduceError {
    public static void main(String[] args){
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setProject(System.getenv("PROJECT"));
        options.setStagingLocation(System.getenv("STAGING"));
        options.setRegion(ObjectUtils.firstNonNull(System.getenv("REGION"),"asia-east1"));
        options.setRunner(DataflowRunner.class);
        boolean isStreaming = true;

        GenerateSequence generateSequence = GenerateSequence.from(0).withRate(1, Duration.standardSeconds(5));
        if(!isStreaming){
            generateSequence = generateSequence.to(2);
        }
        final Pipeline p = Pipeline.create(options);
        p.apply(generateSequence)
                .apply(ParDo.of(new CloneGitFn("https://github.com/DevopsJK/SuitAgent")));
        PipelineResult run = p.run();
        run.waitUntilFinish();

    }
}
