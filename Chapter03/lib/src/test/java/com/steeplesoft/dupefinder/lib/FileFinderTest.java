package com.steeplesoft.dupefinder.lib;

import com.steeplesoft.dupefinder.lib.FileFinder;
import com.steeplesoft.dupefinder.lib.model.FileInfo;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author jason
 */
public class FileFinderTest {
    @Test
    public void findDuplicates() {
        FileFinder ff = new FileFinder();
        ff.addPath("..\\test-data\\set1");
        ff.addPath("..\\test-data\\set2");
        ff.addPattern("*.txt");
        ff.find();
        final Map<String, List<FileInfo>> duplicates = ff.getDuplicates();
        Assert.assertFalse(duplicates.isEmpty());
        duplicates.forEach((k, v) -> System.out.println(k + ": " + v.size()));
    }
}
