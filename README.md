# bam-aggregate-task
The `bam-aggregate-task` is Kotlin workflow task which aggregates the 5' end of reads from an alignment file across sets of input
regions in BED 6+ format, producing corresponding vectors of read counts in JSON format. All the regions in each input file are
first resized to a constant width, which defaults to 2000 basepairs, around their center points. The task requires the input BAM
to have a corresponding index, located at `<input>.bam.bai`. The task is strand-aware: when a region in a BED 6+ input file lies
on the minus strand, the ordering of read counts around its center is reversed. If your input regions are not strand-specific, you
can use a strand designation of `.` in the sixth field of each BED file.

# Running locally
To run the task, first checkout the GitHub repository and build an executable JAR:

```
git clone https://www.github.com/krews-community/bam-aggregate-task
cd bam-aggregate-task && scripts/build.sh
```

Then, to run the task:

```
java -jar build/*.jar \
    --region-file input1.bed
    --alignments input.bam
    --expansionSize 4000
    --outputDir output
```

This will produce an output file at `output/input1_input.json` containing a vector of aggregated read counts in JSON format.
The above example uses a custom width of +/- 2000 basepairs around each peak summit, rather than the default +/- 1000.

# Contributors

The `scripts/` directory contains utilities you can use to build, test, and deploy.

## Building
To build the docker container with a version tag, use `scripts/build-image.sh vX.X.X`. To build the JAR locally, use `scripts/build-local.sh`.
To run the built JAR, use `scripts/run-local.sh`.

## Testing
To run automated tests from command line, use `scripts/test.sh`.

## Deploying
To deploy the image to our docker repository, use `scripts/push-image.sh`.
