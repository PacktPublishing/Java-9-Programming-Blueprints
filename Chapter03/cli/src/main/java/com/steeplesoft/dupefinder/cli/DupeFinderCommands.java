package com.steeplesoft.dupefinder.cli;

import com.steeplesoft.dupefinder.lib.FileFinder;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;

/**
 *
 * @author jason
 */
public class DupeFinderCommands {
    @Command
    public void findDupes(@Option("pattern") List<String> patterns,
            @Option("path") List<String> paths,
            @Option("verbose") @Default("false") boolean verbose,
            @Option("show-timings") @Default("false") boolean showTimings) {

        if (verbose) {
            System.out.println("Scanning for duplicate files.");
            System.out.println("Search paths:");
            paths.forEach(p -> System.out.println("\t" + p));
            System.out.println("Search patterns:");
            patterns.forEach(p -> System.out.println("\t" + p));
            System.out.println();
        }

        final Instant startTime = Instant.now();
        FileFinder ff = new FileFinder();
        patterns.forEach(p -> ff.addPattern(p));
        paths.forEach(p -> ff.addPath(p));

        ff.find();

        System.out.println("The following duplicates have been found:");
        java.math.BigInteger b;
        final AtomicInteger group = new AtomicInteger(1);
        ff.getDuplicates().forEach((name, list) -> {
            System.out.printf("Group #%d:%n", group.getAndIncrement());
            list.forEach(fileInfo -> System.out.println("\t" + fileInfo.getPath()));
        });
        final Instant endTime = Instant.now();

        if (showTimings) {
            Duration duration = Duration.between(startTime, endTime);
            long hours = duration.toHours();
            long minutes = duration.minusHours(hours).toMinutes();
            long seconds = duration.minusHours(hours).minusMinutes(minutes).toMillis() / 1000;
            System.out.println(String.format("%nThe scan took %d hours, %d minutes, and %d seconds.%n", hours, minutes, seconds));
        }
    }
}
